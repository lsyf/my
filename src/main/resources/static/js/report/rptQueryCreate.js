var table;

var orgTree;
var custTree;
function initRptQueryCreate() {
    table = new TableInit();
    table.Init();

    buildSelect('form_month', months);
    orgTree = new ZtreeSelect("treeOrg", "menuContent", "form_latnId", 50);
    orgTree.Init(orgs);


}

function queryData() {
    var latnId = orgTree.txt();
    //如果是股份则不传输地市信息
    latnId = latnId == "股份" ? null : latnId;
    $.ajax({
        type: "POST",
        url: hostUrl + "rptQueryCreate/list",
        data: {
            month: $("#form_month").val(),
            latnId: latnId,
            incomeSource: $("#form_incomeSource").val()
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

function removeData(btn) {

    var selects = table.getSelections();
    if (selects == null || selects.length == 0) {
        toastr.info('未选中任何流水号');
        return;
    }

    var excelIds = [];
    selects.forEach(function (d, i) {
        excelIds.push(d.excelId);
    });

    editAlert('警告', '是否确定删除数据，数量:' + excelIds.length, '删除', function () {
        $.ajax({
            type: "POST",
            url: hostUrl + "rptQueryCreate/remove",
            data: {
                excelIds: excelIds
            },
            dataType: "json",
            beforeSend: function () {
                $(btn).button("loading");
            },
            success: function (r) {
                if (r.state) {
                    toastr.info('删除成功');
                    queryData();
                    hideAlert();
                } else {
                    toastr.error('删除失败:' + r.msg);
                }
            },
            error: function (result) {
                toastr.error('发送请求失败');
            },
            complete: function () {
                $(btn).button("reset");
            }
        });
    });
    showAlert();
}


function createData(btn) {
    var latnId = orgTree.txt();
    //如果是股份则不传输地市信息
    latnId = latnId == "股份" ? null : latnId;
    $.ajax({
        type: "POST",
        url: hostUrl + "rptQueryCreate/create",
        data: {
            month: $("#form_month").val(),
            latnId: latnId,
            incomeSource: $("#form_incomeSource").val()
        },
        dataType: "json",
        beforeSend: function () {
            $(btn).button("loading");
        },
        success: function (r) {
            if (r.state) {
                toastr.info('生成文件成功');
                queryData();
            } else {
                toastr.error('生成文件失败:' + r.msg);
            }
        },
        error: function (result) {
            toastr.error('发送请求失败');
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
            pageSize: 10,                       //每页的记录行数（*）
            pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
            search: true,                       //是否显示表格搜索
            strictSearch: false,                 //设置为 true启用 全匹配搜索，否则为模糊搜索
            showColumns: false,                  //是否显示所有的列
            showRefresh: false,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            // height: 600,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "excelId",                     //每一行的唯一标识，一般为主键列
            showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            data: [],
            columns: [{
                field: 'check',
                checkbox: true,
                align: 'center',
                valign: 'middle'
            }, {
                field: 'excelId',
                title: '流水号'
            }, {
                field: 'fileName',
                title: '文件名'
            }, {
                field: 'incomeSource',
                title: '收入来源'
            }, {
                field: 'areaName',
                title: '地市'
            }, {
                field: 'acctMonth',
                title: '报表月份'
            }, {
                field: 'createTime',
                title: '创建时间'
            }]
        });


    };


    oTableInit.getSelections = function () {
        return $table.bootstrapTable('getSelections');
    };

    //刷新数据
    oTableInit.load = function (data) {
        $table.bootstrapTable('load', data);
    };


    return oTableInit;
}


