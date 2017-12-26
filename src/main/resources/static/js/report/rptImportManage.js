var table;

function initImportManage() {
    table = new TableInit();
    table.Init();
  
    initDatePicker();
}

function initDatePicker() {
    $('.form_datetime').datetimepicker({
        format: "yyyy-mm-dd hh:00",
        autoclose: true,
        todayBtn: true,
        minView: 'day',
        maxView: 'year',
        // startView: 'day',
        todayHighlight: true,
        language:'zh-CN'
    });

    var m_this = moment().format('YYYY-MM-DD HH:00');
    var m_last = moment().add(-1, 'd').format('YYYY-MM-DD HH:00');
    $('#query_start').val(m_last);
    $('#query_end').val(m_this);

}
function queryLog() {
    $.ajax({
        type: "POST",
        url: hostUrl + "rptImportManage/list",
        data: {
        	startDate: $("#query_start").val(),
        	endDate: $("#query_end").val(),
        	fileName: $("#query_fileName").val(),
        	userName: $("#query_userId").val()
        },
        dataType: "json",
        success: function (r) {
            if (r.state) {
                var data = r.data;
                table.load(data);

            } else {
                toastr.error('查询失败'+r.msg);
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
            pageSize: 50,                       //每页的记录行数（*）
            pageList: [50,100,500],        //可供选择的每页的行数（*）
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
            	checkbox:true
            },{
                field: 'logId',
                width: '80px',
                title: '流水号'
            },{
                field: 'month',
                width: '80px',
                title: '账期'
            }, {
                field: 'useName',
                width: '120px',
                title: '操作人'
            }, {
                field: 'codeName',
                width: '120px',
                title: '本地网'
            }, {
                field: 'incomeSource',
                width: '80px',
                title: '收入来源'
            }, {
                field: 'fileName',
                width: '200px',
                title: '导入文件'
            }, {
                field: 'importDate',
                width: '120px',
                title: '入库时间'
            }, {
                field: 'exportDesc',
                width: '80px',
                title: '入库描述'
            }]
        });


    };

   
    //刷新数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
    };


    return oTableInit;
};

