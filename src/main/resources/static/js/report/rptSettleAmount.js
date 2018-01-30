var table;
var orgTree;
function initForm() {
    table = new TableInit();
    table.Init();

    buildSelect('upload_month', months);
    orgTree = new ZtreeSelect("treeOrg", "menuContent", "upload_latnId",80);
    orgTree.Init(orgs);
   
}


function queryLog(btn) {
    $.ajax({
        type: "POST",
        url: hostUrl + "rptSettleAmount/list",
        data: {
            month: $("#upload_month").val(),
            latnId: orgTree.val(),
            zbCode: $("#upload_zbCode").val()
        },
        dataType: "json",
        beforeSend: function () {
        	$(btn).button("loading");
        },
        success: function (r) {
            if (r.state) {
                var data = r.data;
                if(Array.prototype.isPrototypeOf(data) && data.length === 0){
                	toastr.warning('查询数据为空!');
                }
                table.load(data);

            } else {
            	toastr.error('查询失败' + r.msg);
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

//导出
function exportData() {
    var month = $("#upload_month").val();
    var latnId = orgTree.val();
    var zbCode = $("#upload_zbCode").val();

    var names = ['month', 'latnId', 'zbCode'];
    var params = [month, latnId, zbCode];

    var form = $("#form_export");   //定义一个form表单
    form.attr('action', hostUrl + 'rptSettleAmount/export');
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

function collectData(btn) {
	var month = $("#upload_month").val();
    var latnId = orgTree.val();
    var zbCode = $("#upload_zbCode").val(); 
    $.ajax({
        type: "POST",
        url: hostUrl + "rptSettleAmount/collect",
        data: { month: month, 
        	    latnId: latnId, 
        	    zbCode: zbCode
        	},
        dataType: "json",
        beforeSend: function () {
            $(btn).button("loading");
        },
        success: function (r) {
            if (r.state) {
                toastr.warning('成功:'+r.msg);
            } else {
                toastr.error('失败'+r.msg);
                
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
                field: 'val',
                width:'80px',
                title: '地市'
            }, {
                field: 'val1',
                width:'120px',
                title: 'IC卡漫游结算'
            }, {
                field: 'val2',
                width:'120px',
                title: '流量卡收入'
            }, {
                field: 'val3',
                width:'120px',
                title: '资金流向(赠款及ISALE)'
            }, {
                field: 'val4',
                width:'200px',
                title: '网间结算'
            }, {
                field: 'val5',
                width:'80px',
                title: '网间结算本月预结算'
            }, {
                field: 'val6',
                width:'80px',
                title: '网间结算终结算'
            }, {
                field: 'val7',
                width:'80px',
                title: '网间结算上月预结算冲销'
            }, {
                field: 'val8',
                width:'80px',
                title: '网内结算'
            }, {
                field: 'val9',
                width:'80px',
                title: '一点结算*N'
            }, {
                field: 'val10',
                width:'80px',
                title: '网内结算支出*N'
            }, {
                field: 'val11',
                width:'80px',
                title: '网内结算收入'
            }, {
                field: 'val12',
                width:'80px',
                title: '漫游结算收入'
            }, {
                field: 'val13',
                width:'80px',
                title: '分摊收入'
            }, {
                field: 'val14',
                width:'80px',
                title: '大客户分摊'
            }, {
                field: 'val15',
                width:'80px',
                title: '公免公纳'
            }, {
                field: 'val16',
                width:'80px',
                title: '移动转售收入'
            }, {
                field: 'val17',
                width:'80px',
                title: '装移机分摊*N'
            }, {
                field: 'val18',
                width:'80px',
                title: '集团分摊收入'
            }, {
                field: 'val19',
                width:'80px',
                title: '积分收入'
            }, {
                field: 'val20',
                width:'80px',
                title: '积分计提*N'
            }, {
                field: 'val21',
                width:'80px',
                title: '营改增前积分兑换*N(省公司)'
            }, {
                field: 'val22',
                width:'80px',
                title: '积分兑换电信业务销项转回*N(省公司)'
            }, {
                field: 'val23',
                width:'80px',
                title: '积分兑换(省公司)'
            }, {
                field: 'val24',
                width:'80px',
                title: '分成业务*N'
            }, {
                field: 'val25',
                width:'80px',
                title: '全国分成出账*N'
            }, {
                field: 'val26',
                width:'80px',
                title: '省内分成出账*N'
            }, {
                field: 'val27',
                width:'80px',
                title: '电子渠道折扣*N'
            }, {
                field: 'val28',
                width:'80px',
                title: '本地分成出账*N'
            }, {
                field: 'val29',
                width:'80px',
                title: '驻地网分成*N'
            }]
        });


    };

    
    //刷新数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
    };


    return oTableInit;
};

