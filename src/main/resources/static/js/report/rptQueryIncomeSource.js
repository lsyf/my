var table;

var orgTree;
var custTree;
function initRptQueryIncomeSource() {
    table = new TableInit();


    buildSelect('form_month', months);
    orgTree = new ZtreeSelect("treeOrg", "menuContent", "form_latnId", 50);
    orgTree.Init(orgs);
    custTree = new ZtreeSelect("treeOrg2", "menuContent2", "form_cust", 90);
    custTree.Init(custs);


}


function removeData(btn) {
    editAlert('警告', '是否确定删除缓存', '删除', function () {
        $.ajax({
            type: "POST",
            url: hostUrl + "rptQueryIncomeSource/remove",
            data: {
                month: $("#form_month").val(),
                latnId: orgTree.val(),
                cust: custTree.val(),
                type: $("#form_type").val()
            },
            dataType: "json",
            beforeSend: function () {
                $(btn).button("loading");
            },
            success: function (r) {
                if (r.state) {
                    toastr.info('删除成功');
                    table.load([]);
                    hideAlert();
                } else {
                    toastr.error('删除失败:' + r.msg);
                }
            },
            error: function (result) {
                toastr.error('发送请求失败');
            },
            complete:function () {
                $(btn).button("reset");
            }
        });
    });
    showAlert();
}


function queryData() {

    var title = $("#form_month").val() + "-"
        + orgTree.txt() + "-"
        + custTree.txt() + "-"
        + $("#form_type").find("option:selected").text();

    $.ajax({
        type: "POST",
        url: hostUrl + "rptQueryIncomeSource/list",
        timeout: 30000,
        data: {
            month: $("#form_month").val(),
            latnId: orgTree.val(),
            cust: custTree.val(),
            type: $("#form_type").val()
        },
        dataType: "json",
        beforeSend: function () {
            $('#btn_query').button("loading");
        },
        success: function (r) {
            $('#btn_query').button("reset");
            if (r.state) {
                var data = r.data;
                $('#title_table').text(title);
                table.Init(data.titles, data.datas);
            } else {
                toastr.error('查询失败');
                toastr.error(r.msg);
            }
        },
        error: function (result) {
            $('#btn_query').button("reset");
            toastr.error('发送请求失败');
        }
    });

}

function exportData(isMulti, btn) {
    var month = $("#form_month").val();
    var latnId = orgTree.val();
    var cust = custTree.val();
    var type = $("#form_type").val();

    var names = ['month', 'latnId', 'cust', 'type', 'isMulti'];
    var params = [month, latnId, cust, type, isMulti];

    var form = $("#form_export");   //定义一个form表单
    form.attr('action', hostUrl + 'rptQueryIncomeSource/export');
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


//Table初始化
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
            pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
            search: true,                       //是否显示表格搜索
            strictSearch: false,                 //设置为 true启用 全匹配搜索，否则为模糊搜索
            showColumns: false,                  //是否显示所有的列
            showRefresh: false,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            // height: 600,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "rowNum",                     //每一行的唯一标识，一般为主键列
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
                field: 'rowNum',
                align: 'center',
                valign: 'middle',
                title: '行次',
                formatter: function (value, row, index) {
                    return index + 1;
                }
            }, {
                class: 'table_colum1',
                field: 'id',
                title: '指标编码',

            },
            {
                class: 'table_colum1',
                field: 'pid',
                title: '上级编码',
                align: 'left',
                halign: 'center',
            },
            {
                class: 'table_colum1',
                field: 'name',
                title: '产品收入项目名称',
                align: 'left',
                halign: 'center'
            }
        ];

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


        return cols;
    }


    //刷新数据
    oTableInit.load = function (data) {
        $table.bootstrapTable('load', data);
    };


    return oTableInit;
}

