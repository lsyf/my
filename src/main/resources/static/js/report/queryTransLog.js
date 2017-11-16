var table;
var orgTree;
var isTree;
function initTransLog() {
    table = new TableInit();
    table.Init();

    buildSelect('upload_month', months);
    orgTree = new ZtreeSelect("treeOrg", "menuContent", "upload_latnId", 50);
    orgTree.Init(orgs);
    isTree = new ZtreeSelect("treeOrg2", "menuContent2", "upload_incomeSource", 90);
    isTree.Init(incomeSources);
   
}

function queryLog() {
    $.ajax({
        type: "POST",
        url: hostUrl + "queryTransLog/list",
        data: {
            month: $("#upload_month").val(),
            latnId: orgTree.val(),
            incomeSource: isTree.val(),
            taxtId: $("#upload_taxtId").val()
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
function downLoadData() {
    var month = $("#upload_month").val();
    var latnId = orgTree.val();
    var incomeSource = isTree.val();
    var taxtId = $("#upload_taxtId").val();

    editAlert('警告', '是否确定导出:  账期' + month + ", 地市" + latnId
        + ', 收入来源' + incomeSource + ', 数据类型' + taxtId, '导出', function () {
        $.ajax({
            type: "POST",
            url: hostUrl + "queryTransLog/downLoad",
            data: {
                month: month,
                latnId: latnId,
                incomeSource: incomeSource,
                taxtId: taxtId
            },
            dataType: "json",
            success: function (r) {
                if (r.state) {
                    toastr.info('导出成功');
                    hideAlert();
                } else {
                    toastr.error('导出失败');
                    toastr.error(r.msg);
                }
            },
            error: function (result) {
                toastr.error('发送请求失败');
            }
        });
    });
    showAlert();
}

//电子档案下载
function downData(row) {
    
    editAlert('警告', '是否确定导出批次号:' + row.batchId , '导出', function () {
        $.ajax({
            type: "POST",
            url: hostUrl + "queryTransLog/downLog",
            data:{"batchId": row.batchId,
            	  "month": row.month
            },
            dataType: "json",
            success: function (r) {
                if (r.state) {
                    toastr.info('导出成功');
                    hideAlert();
                } else {
                    toastr.error('导出失败');
                    toastr.error(r.msg);
                }
            },
            error: function (result) {
                toastr.error('发送请求失败');
            }
        });
    });
    showAlert();
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
                field: 'month',
                width:'80px',
                title: '账期'
            }, {
                field: 'incomeSource',
                width:'120px',
                title: '收入来源编码'
            }, {
                field: 'incomeName',
                width:'120px',
                title: '收入来源名称'
            }, {
                field: 'codeName',
                width:'120px',
                title: '本地网名称'
            }, {
                field: 'batchId',
                width:'200px',
                title: '批次号'
            }, {
                field: 'subId',
                width:'80px',
                title: '版本号'
            }, {
                field: 'status',
                width:'80px',
                title: '状态'
            }, {
                field: 'createDate',
                width:'150px',
                title: '创建时间'
            }, {
                field: 'lstUpd',
                width:'150px',
                title: '最后修改时间'
            }, {
                field: 'voucherCode',
                width:'200px',
                title: '凭证号'
            },{
                field: 'operate',
                title: '操作',
                events: operateEvents,
                formatter: operateFormatter
            }]
        });


    };

  //操作 监听
    window.operateEvents = {
        'click .downlog': function (e, value, row, index) {
        	downData(row);
        }
    };

    //操作显示format
    function operateFormatter(value, row, index) {
        return [
            '<button type="button" class="downlog btn btn-danger btn-xs">下载</button>'
        ].join('');
    }
    
    //刷新数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
    };


    return oTableInit;
};
