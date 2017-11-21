var table;
var isTree;
function initForm() {
    table = new TableInit();
    table.Init();

    buildSelect('upload_month', months);
    isTree = new ZtreeSelect("treeOrg", "menuContent", "upload_reportId", 90);
    isTree.Init(reportIds);
   
}


function queryData() {
    $.ajax({
        type: "POST",
        url: hostUrl + "rptSettleQuery/list",
        data: {
            month: $("#upload_month").val(),
            reportId: isTree.val()
        },
        dataType: "json",
        success: function (r) {
            if (r.state) {
                var data = r.data;
                table.load(data);

            } else {
                toastr.error('查询失败');
                toastr.error(r.msg);
            }
        },
        error: function (result) {
            toastr.error('发送请求失败');
        }
    });

}

//导出
function exportData() {
    var month = $("#upload_month").val();
    var reportId = isTree.val();

    var names = ['month', 'reportId'];
    var params = [month, reportId];

    var form = $("#form_export");   //定义一个form表单
    form.attr('action', hostUrl + 'rptSettleQuery/export');
    form.empty();
    names.forEach(function (v, i) {
        var input = $('<input>');
        input.attr('type', 'hidden');
        input.attr('name', v);
        input.attr('value', params[i]);
        form.append(input);
    });

    form.submit();   //表单提交

}

//审核查询
function auditQuery(){
	
	
}

function detailData(row) {

        $.ajax({
            type: "POST",
            url: hostUrl + "rptSettleQuery/detail",
            data: {"logId": row.logId, "incomeSource":row.incomeSource},
            dataType: "json",
            success: function (r) {
                if (r.state) {
                    toastr.info('删除成功');
                    hideAlert();

                    queryLog()
                } else {
                    toastr.error('提删除失败');
                    toastr.error(r.msg);
                }
            },
            error: function (result) {
                toastr.error('发送请求失败');
            }
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
            pageSize: 10,                       //每页的记录行数（*）
            pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
            // search: true,                       //是否显示表格搜索
            strictSearch: false,                 //设置为 true启用 全匹配搜索，否则为模糊搜索
            showColumns: false,                  //是否显示所有的列
            showRefresh: false,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            // height: 600,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "ID",                     //每一行的唯一标识，一般为主键列
            showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
             
            data: [],
            columns: [{
            	checkbox:true
            },{
                field: 'logId',
                width:'80px',
                title: '流水号'
            }, {
                field: 'reportId',
                width:'120px',
                title: '报表编号'
            }, {
                field: 'reportName',
                width:'120px',
                title: '报表名称'
            }, {
                field: 'month',
                width:'120px',
                title: '账期'
            }, {
                field: 'incomeSource',
                width:'200px',
                title: '收入来源'
            }, {
                field: 'status',
                width:'80px',
                title: '状态'
            }, {
                field: 'fileSeq',
                width:'80px',
                title: '重传次数'
            }, {
                field: 'createDate',
                width:'80px',
                title: '下发时间'
            }, {
                field: 'importDate',
                width:'80px',
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

    
    //刷新数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
    };


    return oTableInit;
};

