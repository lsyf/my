var table;

function initSettleDetail() {

    table = new TableInit();
   
    table.Init(report_titles, report_datas);
   
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
            pageSize: 10,                       //每页的记录行数（*）
            pageList: [10, 50],        //可供选择的每页的行数（*）
            //search: true,                       //是否显示表格搜索
            strictSearch: false,                 //设置为 true启用 全匹配搜索，否则为模糊搜索
            showColumns: false,                  //是否显示所有的列
            showRefresh: false,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            height: 400,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
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
