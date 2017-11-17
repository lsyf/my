var table;
var isTree;
function initFundsFeeForm() {
    table = new TableInit();
    table.Init();

    buildSelect('upload_month', months);
    isTree = new ZtreeSelect("treeOrg", "menuContent", "upload_reportId", 90);
    isTree.Init(reportIds);
   
}

function queryLog() {
    $.ajax({
        type: "POST",
        url: hostUrl + "rptStatusFundsFee/list",
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
                field: 'reportName',
                width:'80px',
                title: '报表名称'
            }, {
                field: 'status',
                width:'120px',
                title: '报表状态',
                formatter:function(value,row,index){
                	var a ='';
                	if(value =="一审"){
                		var a = '<span style="color:#00CD00">'+value+'</span>';  
                	}else if(value =="二审"){
                		var a = '<span style="color:#FF0000">'+value+'</span>'; 
                	}else if(value =="准备数据"){
                		var a = '<span style="color:#FF0000">'+value+'</span>'; 
                	}else if(value =="准备生成文件"){
                		var a = '<span style="color:#FF0000">'+value+'</span>'; 
                	}else if(value =="生成文件成功"){
                		var a = '<span style="color:#FF0000">'+value+'</span>'; 
                	}else if(value =="通知集团"){
                		var a = '<span style="color:#FF0000">'+value+'</span>'; 
                	}else if(value =="集团入库"){
                		var a = '<span style="color:#FF0000">'+value+'</span>'; 
                	}else if(value =="通知集团失败"){
                		var a = '<span style="color:#FF0000">'+value+'</span>'; 
                	}else if(value =="集团入库失败"){
                		var a = '<span style="color:#FF0000">'+value+'</span>'; 
                	}else if(value =="过账成功"){
                		var a = '<span style="color:#FF0000">'+value+'</span>'; 
                	}else if(value =="过账失败"){
                		var a = '<span style="color:#FF0000">'+value+'</span>'; 
                	}else if(value =="SAP冲销"){
                		var a = '<span style="color:#FF0000">'+value+'</span>'; 
                	}else if(value =="SAP删除"){
                		var a = '<span style="color:#FF0000">'+value+'</span>'; 
                	}else{
                		var a = '<span style="color:#BEBEBE">'+value+'</span>'; 
                	}
                	return a;
                }
            }, {
                field: 'voucherCode',
                width:'120px',
                title: '凭证号'
            }]
        });


    };

    
    //刷新数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
    };


    return oTableInit;
};

