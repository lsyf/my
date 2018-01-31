var table;

function initFundsFeeForm() {
    table = new TableInit();
    table.Init();

    buildSelect('upload_month', months);
    CommSelect('upload_reportId', reportIds);

}

function queryLog(btn) {
    $.ajax({
        type: "POST",
        url: hostUrl + "rptFundsFeeStatus/list",
        data: {
            month: $("#upload_month").val(),
            reportId: $("#upload_reportId").val()
        },
        dataType: "json",
        beforeSend: function () {
            $(btn).button("loading");
        },
        success: function (r) {
            if (r.state) {
                var data = r.data;
                if(Array.prototype.isPrototypeOf(data) && data.length === 0){
                	toastr.warning('查询数据为空!');
                }
                table.load(data);

            } else {
                toastr.error('查询失败'+r.msg);
                
            }
        },
        error: function (result) {
            toastr.error('连接服务器请求失败!');
        },
        complete:function() {
        	$(btn).button("reset");
        }
    });

}

//回退
function quitData(id) {
    var month = $("#upload_month").val();
    var id = id ;

    editAlert('警告', '是否确定回退月份: ' + month + ',报表编号:' + id, '回退', function () {
        $.ajax({
            type: "POST",
            url: hostUrl + "rptFundsFeeStatus/quit",
            data: {month: month, reportId: id},
            dataType: "json",
            success: function (r) {
                if (r.state) {
                    toastr.info('回退成功');
                    hideAlert();

                    queryLog()
                } else {
                    toastr.error('回退失败' + r.msg);
                }
            },
            error: function (result) {
                toastr.error('连接服务器请求失败!');
            }
        });
    });
    showAlert();
}

//电子档案下载
function downData(id) {
    var month = $("#upload_month").val();

    var selects = $('#table_upload').bootstrapTable('getSelections');
    if (selects.length == 0) {
        toastr.info('未选中任何数据');
        return;
    }
    var logs = [];
    selects.forEach(function (data, i) {
        logs.push(data.voucherCode);
    });


}

//Table初始化
var TableInit = function () {
    var oTableInit = new Object();

    //初始化Table
    oTableInit.Init = function () {
        $('#table_upload').bootstrapTable({
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: true,                   //是否显示分页（*）
            sortable: false,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            contentType: 'application/x-www-form-urlencoded',
            sidePagination: "client",           //分页方式：client客户端分页，server服务端分页（*）
            pageNumber: 1,                       //初始化加载第一页，默认第一页
            pageSize: 50,                       //每页的记录行数（*）
            pageList: [50, 100, 500],        //可供选择的每页的行数（*）
            // search: true,                       //是否显示表格搜索
            strictSearch: false,                 //设置为 true启用 全匹配搜索，否则为模糊搜索
            showColumns: false,                  //是否显示所有的列
            showRefresh: false,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            height: 600,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "ID",                     //每一行的唯一标识，一般为主键列
            showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表

            data: [],
            columns: [{
                field: 'reportName',
                width: '80px',
                title: '报表名称'
            }, {
                field: 'status',
                width: '120px',
                title: '报表状态',
                formatter: function (value, row, index) {
                    var a = '';
                    if (value == "一审") {
                        var a = '<span style="color:#00CD00">' + value + '</span>';
                    } else if (value == "二审") {
                        var a = '<span style="color:#FF0000">' + value + '</span>';
                    } else if (value == "准备数据") {
                        var a = '<span style="color:#FF0000">' + value + '</span>';
                    } else if (value == "准备生成文件") {
                        var a = '<span style="color:#FF0000">' + value + '</span>';
                    } else if (value == "生成文件成功") {
                        var a = '<span style="color:#FF0000">' + value + '</span>';
                    } else if (value == "通知集团") {
                        var a = '<span style="color:#FF0000">' + value + '</span>';
                    } else if (value == "集团入库") {
                        var a = '<span style="color:#FF0000">' + value + '</span>';
                    } else if (value == "通知集团失败") {
                        var a = '<span style="color:#FF0000">' + value + '</span>';
                    } else if (value == "集团入库失败") {
                        var a = '<span style="color:#FF0000">' + value + '</span>';
                    } else if (value == "过账成功") {
                        var a = '<span style="color:#FF0000">' + value + '</span>';
                    } else if (value == "过账失败") {
                        var a = '<span style="color:#FF0000">' + value + '</span>';
                    } else if (value == "SAP冲销") {
                        var a = '<span style="color:#FF0000">' + value + '</span>';
                    } else if (value == "SAP删除") {
                        var a = '<span style="color:#FF0000">' + value + '</span>';
                    } else {
                        var a = '<span style="color:#BEBEBE">' + value + '</span>';
                    }
                    return a;
                }
            }, {
                field: 'voucherCode',
                width: '120px',
                title: '凭证号',
                formatter: function (v) {
                    return [
                        '<div title="' + v + '" ' +
                        'style="width:200px; white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
                        + v + '</div>'
                    ].join('');
                }
            }, {
                field: '123',
                width: '120px',
                title: '操作',
                formatter: operateFormatter,
                events: operateEvents,
            }]
        });


    };

    //操作显示format
    function operateFormatter(value, row, index) {
        return [
            '<button type="button" class="download btn btn-primary btn-xs">下载</button> \
            <button type="button" class="back btn btn-danger btn-xs">回退</button>'
        ].join('');
    }

    //操作 监听
    window.operateEvents = {
        'click .download': function (e, value, row, index) {
            downData(row.reportName.substring(0, 1));
        },
        'click .back': function (e, value, row, index) {
            quitData(row.reportName.substring(0, 1));
        }

    };

    //刷新数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
    };


    return oTableInit;
};

