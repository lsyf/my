var table;

var isTree;
function initForm() {
    table = new TableInit();
    table.Init();
   
    buildSelect('upload_month', months);

    isTree = new ZtreeSelect("treeOrg", "menuContent", "upload_reportId", 80);
    isTree.Init(reportIds);

}


function queryData(btn) {
    $.ajax({
        type: "POST",
        url: hostUrl + "rptSettleOldData/list",
        data: {
            month: $("#upload_month").val(),
            reportId: isTree.val()
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
        complete:function () {
        	$(btn).button("reset");
           
        }
    });

}

//导出
function exportData() {
    var selects = table.getSelections();
    if (selects.length == 0) {
        toastr.info('未选中任何数据');
        return;
    }

    var logs = [];
    selects.forEach(function (a) {
        var temp = new Object();
        temp.logId = a.logId;
        temp.reportId = a.reportId;
        temp.incomeSource = a.incomeSource;
        logs.push(temp);
    });

    var temp = {logs: logs};
    var param = JSON.stringify(temp);

    var form = $("#form_export");   //定义一个form表单    
    form.attr('action', hostUrl + 'rptSettleOldData/export');
    form.empty();

    var input = $('<input>');
    input.attr('type', 'hidden');
    input.attr('name', 'temp');
    input.attr('value', param);
    form.append(input);

    form.submit();   //表单提交
}


function detailData(row) {
    var logId = row.logId;
    var incomeSource = row.incomeSource;

    window.open("rptSettleDetail2.html?logId=" + logId + "&incomeSource=" + incomeSource);
}


//Table初始化
var TableInit = function () {
    var oTableInit = new Object();
    var $table = $('#table_upload');

    //初始化Table
    oTableInit.Init = function () {
        $table.bootstrapTable({
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
            //search: true,                       //是否显示表格搜索
            strictSearch: false,                 //设置为 true启用 全匹配搜索，否则为模糊搜索
            showColumns: false,                  //是否显示所有的列
            showRefresh: false,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            height: 800,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "ID",                     //每一行的唯一标识，一般为主键列
            showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表

            data: [],
            columns: [{
                checkbox: true
            }, {
                field: 'logId',
                title: '流水号'
            }, {
                field: 'reportId',
                title: '报表编号'
            }, {
                field: 'reportName',
                title: '报表名称',
                formatter: function (v) {
                    return [
                        '<div title="' + v + '" ' +
                        'style="width:200px; white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
                        + v + '</div>'
                    ].join('');
                }
            }, {
                field: 'month',
                title: '账期'
            }, {
                field: 'incomeSource',
                title: '收入来源'
            }, {
                field: 'status',
                title: '状态'
            }, {
                field: 'fileSeq',
                title: '重传次数'
            }, {
                field: 'createDate',
                title: '下发时间'
            }, {
                field: 'importDate',
                title: '导入时间'
            }, {
                field: 'operate',
                title: '操作',
                events: operateEvents,
                formatter: operateFormatter
            }]
        });

    };

    //操作 监听
    window.operateEvents = {
        'click .detail': function (e, value, row, index) {
            detailData(row);
        }
    };

    //操作显示format
    function operateFormatter(value, row, index) {
        return [
            '<button type="button" class="detail btn btn-success btn-xs">详细</button>'
        ].join('');
    }

    oTableInit.getSelections = function () {
        return $table.bootstrapTable('getSelections');
    };

    //刷新数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
    };


    return oTableInit;
};
