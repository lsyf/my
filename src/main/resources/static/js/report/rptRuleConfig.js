var table;
var orgTree;
var form_orgTree;
function initRuleConfigForm() {
    table = new TableInit();
    table.Init();

    buildSelect('query_month', months);
    buildSelect('query_card', cards);
    
    
    orgTree = new ZtreeSelect("treeOrg", "menuContent", "query_latnId", 80);
    orgTree.Init(orgs);
    
  //表单下拉加载
    buildSelect('form_month', months);
    buildSelect('form_card', cards);
    form_orgTree = new ZtreeSelect("treeOrg2", "menuContent2", "form_latnId");
    form_orgTree.Init(orgs);

}

function showPanel(a){
	 
	if (a){
         
        $("#form-panel").hide();
        $("#table-panel").show();
    } else {
    	 
        $("#form-panel").show();
        $("#table-panel").hide();
        var form = $("#rule_form");
        form.resetForm();
       
       
    }
}

function queryList(btn) {
    $.ajax({
        type: "POST",
        url: hostUrl + "rptRuleConfig/list",
        data: {
            month: $("#query_month").val(),
            latnId: orgTree.val(),
            cardType: $("#query_card").val(),
            logId: $("#query_logId").val(),
            discount: $("#query_discount").val()           
        },
        dataType: "json",
        beforeSend: function () {
        	$(btn).button("loading");
        },
        success: function (r) {
            if (r.state) {
                var data = r.data;
                table.load(data);

            } else {
                toastr.error('查询失败'+r.msg);
            }
        },
        error: function (result) {
            toastr.error('连接服务器请求失败!');
        },
        complete:function(){
        	$(btn).button("reset");
        }
    });

}

function showData(row) {

    
    showPanel(0);
    $("#form_month").val(row.month);
    form_orgTree.txt(row.latnName);
    form_orgTree.val(row.latnId);
  
    $('#form_logId').val(row.logId);
    $('#form_card').val(row.cardTypeId);   
    $('#form_discount').val(row.discount);
    $('#platformAmount_val').val(row.platformAmount);
    $('#inactiveAmount_val').val(row.inactiveAmount);

}

function doUpdate() {
    var form = $('#rule_form');

    form.ajaxSubmit({
        url: hostUrl + 'rptRuleConfig/update',
        type: 'post',
        resetForm: true,
        beforeSubmit: function () {
            $("#btn_save").button("loading");
        },
        success: function (r) {
            $("#btn_save").button("reset");

            if (r.state) {
                toastr.info('更新成功');
               
                showPanel(1);
                queryList() ;
            } else {
                toastr.error('更新失败'+r.msg);
            }
        },
        error: function () {
            $("#btn_save").button("reset");
            toastr.error("连接服务器请求失败!");
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
            height: 700,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "logId",                     //每一行的唯一标识，一般为主键列
            showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
             
            data: [],
            columns: [{
                field: 'logId',
                width:'80px',
                title: '编号'
            }, {
                field: 'month',
                width:'120px',
                title: '帐期'
            }, {
                field: 'latnName',
                width:'120px',
                title: '本地网'
            }, {
                field: 'codeName',
                width:'120px',
                title: '营业区'
            }, {
                field: 'cardType',
                width:'80px',
                title: '卡类型'
            }, {
                field: 'discount',
                width:'80px',
                title: '折扣率'
            }, {
                field: 'platformAmount',
                width:'80px',
                title: '平台过期卡金额(元)'
            }, {
                field: 'inactiveAmount',
                width:'80px',
                title: '库存过期卡金额(元)'
            },{
            	field: 'userName',
                title: '修改人',
                width: 100
            },{
            	field: 'lstUpd',
            	title: '修改时间',
                width: 100
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
    		'click .edit': function (e, value, row, index) {
    			showData(row);
            }
    };

    //操作显示format
    function operateFormatter(value, row, index) {
        return [
            '<button type="button" class="edit btn btn-primary btn-xs">修改</button>'
        ].join('');
    }
    
    //刷新数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
    };


    return oTableInit;
};

