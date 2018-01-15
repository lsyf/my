var table;
var table_audit;

function initFundsFeeForm() {
    table = new TableInit();
    table.Init();
    table_audit = new TableAudit();
    
    
    buildSelect('upload_month', months);
    CommSelect('upload_reportId', reportIds);
   
}

function queryLog() {
    
	$.ajax({
        type: "POST",
        url: hostUrl + "rptFundsFeeAudit/list",
        data: {
            month: $("#upload_month").val(),
            reportId: $("#upload_reportId").val()
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
        complete:function() {
        	$("#btn_query").button("reset");
        }
    });

}


//审核查询
function listAudit(type, btn) {
	//var month = $("#upload_month").val();
	//var reportId = $("#upload_reportId").val();

    $.ajax({
        type: "POST",
        url: hostUrl + "rptFundsFeeAudit/listAudit",
        data: {
            month: $("#upload_month").val(),
            reportId: $("#upload_reportId").val()
        },
        dataType: "json",
        beforeSend: function () {
            $(btn).button("loading");
        },
        success: function (r) {
            $(btn).button("reset");
            if (r.state) {
                var data = r.data.list;
                var rptCaseId = r.data.rptCaseId;
                //var reportId = r.data.reportId;
                table_audit.load(data);
                editAudit("审核流程", type, function () {
                    auditData(rptCaseId, "1");
                }, function () {
                    auditData(rptCaseId, "0");
                });
                showAudit();
            } else {
                toastr.error('查询失败' + r.msg);
            }
        },
        error: function (result) {
           
            toastr.error('连接服务器请求失败!');
        },
        complete:function() {
        	$(btn).button("reset");
        }
    });
}

function auditData(rptCaseId, status) {
    $.ajax({
        type: "POST",
        url: hostUrl + "rptFundsFeeAudit/audit",
        data: {
            rptCaseId: rptCaseId,
            status: status,
            comment: $('#audit_comment').val()
        },
        dataType: "json",
        success: function (r) {
            if (r.state) {
                if (status == '0') {
                    toastr.warning('审核不通过, 成功!');
                    hideAudit();
                    return
                }
                listAudit('edit')
            } else {
                toastr.error('查询失败' + r.msg);
            }
        },
        error: function (result) {
            toastr.error('连接服务器请求失败!');
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
            height: 800,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "ID",                     //每一行的唯一标识，一般为主键列
            showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
             
            data: [],
            columns: [{
                field: 'txtMessage',
                title: '文本信息',
                formatter: function (v) {
                    var val="";
                	if(v ==null){
                       val ="-";	
                    }else{
                    	val =['<div title="' + v + '" ' +
                        'style="width:80px; white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
                        + v + '</div>'
                    ].join('')
                    }
                	return val;
                }
            }, {
                field: 'prctr',
                title: '利润中心编码'
            }, {
                field: 'sapFinCode',
                title: 'SAP科目编码'
            },{
                field: 'sapFinCodeName',
                title: 'SAP科目名称',
                formatter: function (v) {
                    var val="";
                	if(v ==null){
                       val ="-";	
                    }else{
                    	val =['<div title="' + v + '" ' +
                        'style="width:80px; white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
                        + v + '</div>'
                    ].join('')
                    }
                	return val;
                }
            }, {
                field: 'kunnr',
                title: '客户编码'
            }, {
                field: 'kunnrName',
                title: '客户名称',
                formatter: function (v) {
                    var val="";
                	if(v ==null){
                       val ="-";	
                    }else{
                    	val =['<div title="' + v + '" ' +
                        'style="width:80px; white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
                        + v + '</div>'
                    ].join('')
                    }
                	return val;
                }
            }, {
                field: 'jieFbalance',
                title: '借方金额'
            }, {
                field: 'daiFbalance',
                title: '贷方金额'
            } ]
        });


    };

    
    //刷新数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
    };


    return oTableInit;
};


//审核
var TableAudit = function () {
  var oTableInit = new Object();
  var $table = $('#table_audit');

  //初始化Table
  oTableInit.Init = function () {
      $table.bootstrapTable({
          striped: true,                      //是否显示行间隔色
          cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
          // pagination: true,                   //是否显示分页（*）
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
          //height: 600,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
          uniqueId: "rowNum",                     //每一行的唯一标识，一般为主键列
          showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
          cardView: false,                    //是否显示详细视图
          detailView: false,                   //是否显示父子表
          data: [],
          columns: [
              {
                  field: 'seqNo',
                  title: '序号'
              },
              {
                  field: 'descText',
                  title: '名称'
              },
              {
                  field: 'time',
                  title: '审核时间'
              },
              {
                  field: 'auditorName',
                  title: '审核人'
              },
              {
                  field: 'auditStatus',
                  title: '状态',
                  formatter: function (v) {
                      if (v == null) {
                          return "待审核";
                      } else if (v == 1) {
                          return "审核通过";
                      }
                      return v;
                  }
              },
              {
                  field: 'auditComment',
                  title: '审核意见'
              }
          ]
      });


  };


  //刷新数据
  oTableInit.load = function (data) {
      $table.bootstrapTable('load', data);
  };

  oTableInit.Init();

  return oTableInit;
}