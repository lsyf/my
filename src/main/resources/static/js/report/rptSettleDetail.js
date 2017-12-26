var table;
var table2;
function initSettleDetail() {

    table = new TableInit();
   
    table2 = new TableInit2();
    table2.Init();
    
    table.Init(report_titles, report_datas);
    table2.load(report_detail);

}

//原始数据
var TableInit = function () {
    var oTableInit = new Object();
    var $table = $('#table_query');

    //初始化Table
    oTableInit.Init = function (titles, datas) {
        var columns = createColumns(titles);
        $table.bootstrapTable('destroy');
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
            pageList: [50, 100,500],        //可供选择的每页的行数（*）
            //search: true,                       //是否显示表格搜索
            strictSearch: false,                 //设置为 true启用 全匹配搜索，否则为模糊搜索
            showColumns: false,                  //是否显示所有的列
            showRefresh: false,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            height: 600,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "reportId",                     //每一行的唯一标识，一般为主键列
            showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            data: datas,
            columns: columns
        });


    };

    function createColumns(titles) {
        var cols = [
            {
                class: 'table_colum1',
                field: 'BUKRS',
                title: '组织代码',
                align: 'left',
                halign: 'center'
            },
            {
                class: 'table_colum1',
                field: 'REPORT_ID',
                title: '报表编号',
                align: 'left',
                halign: 'center',
            },
            {
                class: 'table_colum1',
                field: 'EXTEND_001',
                title: '扩展字段1',
                align: 'left',
                halign: 'center'
            },
            {
                class: 'table_colum1',
                field: 'EXTEND_002',
                title: '扩展字段2',
                align: 'left',
                halign: 'center'
            },
            {
                class: 'table_colum1',
                field: 'EXTEND_003',
                title: '扩展字段3',
                align: 'left',
                halign: 'center'
            }
        ];
        var col_remark = {
            class: 'table_colum3',
            field: 'REMARK',
            title: '数据状态',
            align: 'left',
            halign: 'center',
            formatter:function(value,row,index){
            	var a ='';
            	if(value ==1){
            		var a = '原始数据'; 
            	}else{
            		var a = '切割后数据'; 
            	}
            	return a;
            }
        } ;
        titles.forEach(function (t) {
            var col = {
                class: 'table_colum2',
                field: t.id,
                title: t.name,
                // formatter: dataRound,
                halign: 'center',
                align: 'right',
                // cellStyle: testCellStyle
            };
            cols.push(col);
        });

        cols.push(col_remark)
        return cols;
    }

    //刷新数据
    oTableInit.load = function (data) {
        $table.bootstrapTable('load', data);
    };

    return oTableInit;
}

//Table初始化
var TableInit2 = function () {
    var oTableInit = new Object();

    //初始化Table
    oTableInit.Init = function () {
        $('#table_query2').bootstrapTable({
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: true,                   //是否显示分页（*）
            sortable: false,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            queryParams: oTableInit.queryParams,//传递参数（*）
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
            uniqueId: "reportId",                     //每一行的唯一标识，一般为主键列
            showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            rowStyle: function () {
                return 'table-row';
            },
            data: [],
            columns: [{
                field: 'reportId',
                title: '报表编号'
            }, {
                field: 'reportName',
                title: '报表名称'
            }, {
                field: 'acctMonth',
                title: '账期'
            }, {
                field: 'areaId',
                title: '地市编号'
            }, {
                field: 'areaName',
                title: '地市名'
            }, {
                field: 'horCode',
                title: '渠道编码'
            }, {
                field: 'verCode',
                title: '指标编码'
            }, {
                field: 'indexAllis',
                title: '指标名称'
            }, {
                field: 'incomeSource',
                title: '收入来源编号'
            }, {
                field: 'sourceName',
                title: '收入来源名称'
            }, {
                field: 'indexData',
                title: '数据'
            }]
        });


    };

  
    
    //刷新数据
    oTableInit.load = function (data) {
        $('#table_query2').bootstrapTable('load', data);
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
    		logId: logId.val(),
        	incomeSource: incomeSource.val()
        };
        return temp;
    };
    return oTableInit;
};

