var table;
var orgTree;
function initTransLog() {
    table = new TableInit();
    table.Init();

    buildSelect('query_month', months);
    CommSelect('query_incomeSource', incomeSources);
    orgTree = new ZtreeSelect("treeOrg", "menuContent", "query_latnId", 80);
    orgTree.Init(orgs);
    
}

function queryState() {
	
	$.ajax({
        type: "POST",
        url: hostUrl + "rptQueryAudit/list",
        data: {
            month: $("#query_month").val(),
            latnId: orgTree.val(),
            incomeSource: $("#query_incomeSource").val()
        },
        dataType: "json",
        beforeSend: function () {
        	$("#btn_query").button("loading");
   
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
        complete: function () {
            $("#btn_query").button("reset");
        },
    });

}
function queryFee(btn){
	$.ajax({
        type: "POST",
        url: hostUrl + "rptQueryAudit/listFee",
        data: {
            month: $("#query_month").val(),
            latnId: orgTree.val(),
            incomeSource: $("#query_incomeSource").val()
        },
        dataType: "json",
        beforeSend: function () {
        	$(btn).button("loading");
        },
        success: function (r) {
            if (r.state) {
                var data = r.data;
                table.load(data,2);

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

function quitData(){
	var month =$("#query_month").val();
	var latnId = orgTree.val();
	var incomeSource= $("#query_incomeSource").val();
	editAlert('警告', '是否确定回退月份: ' + month+ ',营业区:'+latnId+ ',收入来源:'+incomeSource, '回退', function (){
		$.ajax({
	        type: "POST",
	        url: hostUrl + "rptQueryAudit/quit",
	        data: {
	            month: $("#query_month").val(),
	            latnId: orgTree.val(),
	            incomeSource: $("#query_incomeSource").val()
	        },
	        dataType: "json",
	        success: function (r) {
	            if (r.state) {
	            	toastr.warning('回退成功');
	            	hideAlert();
	            	queryState();
	            } else {
	                toastr.error('回退失败'+r.msg);	                
	            }
	        },
	        error: function (result) {
	            toastr.error('连接服务器请求失败!');
	        }
	    });
		
	});
	showAlert();	
}
//四审
function auditData(btn) {
	var month = $("#query_month").val();	 
	var selects = $('#table_upload').bootstrapTable('getSelections');
	if(selects.length==0){
		toastr.warning('未选中任何数据');
		return;
	}
	var logs = [];
	selects.forEach(function(data,i){
		logs.push(data.codeName);
	});
	
	$.ajax({
        type: "POST",
        url: hostUrl + "rptQueryAudit/audit",
        data: {
            month: $("#query_month").val(),
            logs: logs         
        },
        dataType: "json",
        beforeSend: function () {
        	$(btn).button("loading");
        },
        success: function (r) {
            if (r.state) {
            	toastr.warning('审核成功');
            	hideAlert();
            	queryState();
            } else {
                toastr.error('审核失败'+r.msg);
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
            uniqueId: "ID",                     //每一行的唯一标识，一般为主键列
            showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表

            data: [],
            columns: [{
            	checkbox:true
            },{
                field: 'codeName',
                width: '80px',
                title: '地市'
            }, {
                field: 'income0',
                width: '120px',
                title: '汇总',
                formatter:imageFormat
                
            }, {
                field: 'income02',
                width: '120px',
                title: '营业汇总',
                formatter:imageFormat
            }, {
                field: 'income50',
                width: '120px',
                title: 'OCS计费汇总',
                formatter:imageFormat
            }, {
                field: 'income06',
                width: '80px',
                title: '滞纳金及欠费收回',
                formatter:imageFormat
            }, {
                field: 'income80',
                width: '80px',
                title: '合约补贴收入*N',
                formatter:imageFormat
            }, {
                field: 'income03',
                width: '80px',
                title: '预付费业务',
                formatter:imageFormat
            }, {
                field: 'income05',
                width: '80px',
                title: '其他收入一',
                formatter:imageFormat
            }, {
                field: 'income07',
                width: '80px',
                title: '其他收入二',
                formatter:imageFormat
            }, {
                field: 'income10',
                width: '80px',
                title: '网间结算',
                formatter:imageFormat
            }, {
                field: 'income20',
                width: '80px',
                title: '网内结算',
                formatter:imageFormat
            }, {
                field: 'income30',
                width: '80px',
                title: '分摊收入',
                formatter:imageFormat
            }, {
                field: 'income40',
                width: '80px',
                title: '积分收入',
                formatter:imageFormat
            }, {
                field: 'income60',
                width: '80px',
                title: '分成业务*N',
                formatter:imageFormat
            }, {
                field: 'income70',
                width: '80px',
                title: '退费减收',
                formatter:imageFormat
            }, {
                field: 'income08',
                width: '80px',
                title: '全网资金结算',
                formatter:imageFormat
            }, {
                field: 'income901',
                width: '80px',
                title: '关联交易整理一*N(分公司)',
                formatter:imageFormat
            }, {
                field: 'income902',
                width: '80px',
                title: '关联交易整理二*N(分公司)',
                formatter:imageFormat
            }]
        });


    };
    
    var type = 1;
    function imageFormat(value,row,index){
    	if(type == 2){
    		return value;
    	}
    	
    	var txt = "";
    	var clas= "";
    	switch(value){
    	case 0:
    		txt = "";
    		clas = "cell_white";
    		break;
    	case 1:
    		txt = "一审";
    		clas = "cell_orange";
    		break;
    	case 2:
    		txt = "二审";
    		clas = "cell_orange";
    		break;
    	case 3:
    		txt = "三审";
    		clas = "cell_bule";
    		break;
    	case 4:
    		txt = "汇总";
    		clas = "cell_orange";
    		break;
    	case 6:
    		txt = "四审";
    		clas = "cell_pale_green";
    		break;
    	case 7:
    		txt = "R";
    		clas = "cell_yellow";
    		break;
    	case 8:
    		txt = "P1";
    		clas = "cell_yellow";
    		break;
    	case 9:
    		txt = "P2";
    		clas = "cell_yellow";
    		break;
    	case 10:
    		txt = "S1";
    		clas = "cell_green";
    		break;
    	case 11:
    		txt = "S2";
    		clas = "cell_purple";
    		break;
    	case 12:
    		txt = "F1";
    		clas = "cell_red";
    		break;
    	case 13:
    		txt = "F2";
    		clas = "cell_red";
    		break;
    	case 14:
    		txt = "S3";
    		clas = "cell_green";
    		break;
    	case 15:
    		txt = "F3";
    		clas = "cell_red";
    		break;
    	case 16:
    		txt = "F4";
    		clas = "cell_red";
    		break;
    	case 17:
    		txt = "F5";
    		clas = "cell_red";
    		break;
    	default:
    		return;
    	}
    	return [
            '<span class="'+clas+' btn-xs">'+txt+'</span>'
        ].join('');
    }
    
    //刷新数据
    oTableInit.load = function (data,a) {
    	type = a;
        $('#table_upload').bootstrapTable('load', data);
    };
    

    return oTableInit;
};

