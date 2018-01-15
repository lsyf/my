var table;
var table2;
var isTree;
function initSingleBillForm() {
    table = new TableInit();
    table.Init();
    
    table2 = new TableDetail();
    table2.Init();
    
    buildSelect('query_month', months);
    isTree = new ZtreeSelect("treeOrg", "menuContent", "query_latnId", 80);
    isTree.Init(orgs);

}


function queryData(btn) {
	
	$.ajax({
        type: "POST",
        url: hostUrl + "rptQuerySingleBill/list",
        data: {
            month: $("#query_month").val(),
            latnId: isTree.val(),
            phone:$("#query_phone").val()
        },
        dataType: "json",
        beforeSend: function () {
        	$(btn).button("loading");
        },
        success: function (r) {
            if (r.state) {
                var data = r.data;
                table.load(data.list2);
                table2.load(data.list);
            } else {
                toastr.error('查询失败'+r.msg);
            }
        },
        error: function (result) {
            toastr.error('连接服务器请求失败!');
        },
        complete: function () {
            $(btn).button("reset");
        }
    });

}

//Table初始化
var TableInit = function () {
    var oTableInit = new Object();
    var $table = $('#table_query');

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
            pageSize: 20,                       //每页的记录行数（*）
            pageList: [20, 50,100],        //可供选择的每页的行数（*）
            //search: true,                       //是否显示表格搜索
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
                field: 'phoneId',
                title: '号码'
            }, {
                field: 'subCode',
                title: '科目代码'
            }, {
                field: 'verticalIndexName',
                title: '科目名称',
                formatter: function (v) {
                    return [
                        '<div title="' + v + '" ' +
                        'style="width:300px; white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
                        + v + '</div>'
                    ].join('');
                }
            }, {
                field: 'unpayFee',
                title: '金额'
            }]
        });

    };

   
    //刷新数据
    oTableInit.load = function (data) {
        $('#table_query').bootstrapTable('load', data);
    };


    return oTableInit;
};


var TableDetail = function () {
    var oTableInit = new Object();
    var $table = $('#table_detail');

    //初始化Table
    oTableInit.Init = function () {
        $table.bootstrapTable({
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            // pagination: true,                   //是否显示分页（*）
            sortable: true,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            contentType: 'application/x-www-form-urlencoded',
            sidePagination: "client",           //分页方式：client客户端分页，server服务端分页（*）
            pageNumber: 1,                       //初始化加载第一页，默认第一页
            pageSize: 20,                       //每页的记录行数（*）
            pageList: [20, 50, 100],        //可供选择的每页的行数（*）
            // search: true,                       //是否显示表格搜索
            strictSearch: false,                 //设置为 true启用 全匹配搜索，否则为模糊搜索
            showColumns: false,                  //是否显示所有的列
            showRefresh: false,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            height: 600,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "rowNum",                     //每一行的唯一标识，一般为主键列
            showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            data: [],
            columns: [
            	{
                    field: 'phoneId',
                    title: '号码'
                }, {
                    field: 'acctItemTypeId',
                    sortable: true,
                    title: '科目ID'
                }, {
                    field: 'name',
                    title: '账目名称',
                    formatter: function (v) {
                        return [
                            '<div title="' + v + '" ' +
                            'style="width:200px; white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
                            + v + '</div>'
                        ].join('');
                    }
                }, {
                    field: 'acctItemTypeCode',
                    title: '四级账目'
                }, {
                    field: 'acctItemTypeName',
                    title: '四级账目名称',
                    formatter: function (v) {
                        return [
                            '<div title="' + v + '" ' +
                            'style="width:200px; white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
                            + v + '</div>'
                        ].join('');
                    }
                }, {
                    field: 'unpayFee',
                    sortable: true,
                    title: '金额'
                }, {
                    field: 'subCode',
                    sortable: true,
                    title: '科目代码'
                }, {
                    field: 'verticalIndexName',
                    title: '科目名称',
                    formatter: function (v) {
                        return [
                            '<div title="' + v + '" ' +
                            'style="width:200px; white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
                            + v + '</div>'
                        ].join('');
                    }
                }, {
                    field: 'seqNbr',
                    title: '报表行号'
                }, {
                    field: 'queryType',
                    title: '账单类型'
                }
            ]
        });


    };


  //刷新数据
    oTableInit.load = function (data) {
        $('#table_detail').bootstrapTable('load', data);
    };

    oTableInit.Init();

    return oTableInit;
}