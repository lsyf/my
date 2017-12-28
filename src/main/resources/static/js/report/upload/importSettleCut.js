var table;
var table2;
var orgTree;
function initSettleCut() {
    buildSelect('upload_month', months);
    orgTree = new ZtreeSelect("treeOrg", "menuContent", "upload_reportId", 90);
    orgTree.Init(orgs);


    table = new TableInit();
    table.Init();
    table2 = new TableInit2();
    table2.Init();


    initForm();
}

function queryLog() {
    $.ajax({
        type: "POST",
        url: hostUrl + "importSettleCut/list",
        data: {
            month: $("#upload_month").val(),
            reportId: orgTree.val()
        },
        dataType: "json",
        success: function (r) {
            if (r.state) {
                var data = r.data;
                table.load(data.datas);
                table2.load(data.logs);
            } else {
                toastrError('查询失败');
                toastrError(r.msg);
            }
        },
        error: function (result) {
            toastrError('发送请求失败');
        }
    });

}

function initForm() {
    initValidator();

    validatorForm = $('#form_upload').validate({
        rules: {
            file: 'required',
            remark: 'required'
        },
        messages: {
            file: "必须选择文件",
            remark: "必须输入备注"
        },
        ignore: "",
        submitHandler: function (form) {
            $(form).ajaxSubmit({
                url: hostUrl + "importSettleCut/upload",
                type: 'post',
                contentType: 'multipart/form-data',
                beforeSubmit: function () {
                    $('#btn_upload').button("loading");
                },
                success: function (r) {
                    $('#btn_upload').button("reset");
                    if (r.state) {
                        $(form).resetForm();
                        //orgTree.reset();

                        toastr.info('导入成功');
                        queryLog()
                    } else {
                        toastrError('导入失败:' + r.msg);
                    }
                },
                error: function (r) {
                    $('#btn_upload').button("reset");
                    toastrError('导入失败');
                }
            });
        }
    });


    $('#form_upload').on("change",
        "input[type=text], input[name], select",
        function () {
            $('#form_upload').validate().element(this);
        });

}


function removeData(row) {
    editAlert('警告', '是否确定删除流水号: ' + row.logId, '删除', function () {
        $.ajax({
            type: "POST",
            url: hostUrl + "importSettleCut/remove",
            data: {"logId": row.logId, month: row.acctMonth},
            dataType: "json",
            success: function (r) {
                if (r.state) {
                    toastr.info('删除成功');
                    hideAlert();

                    queryLog()
                } else {
                    toastrError('删除失败:' +r.msg);
                }
            },
            error: function (result) {
                toastrError('发送请求失败');
            }
        });
    });
    showAlert();
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
            rowStyle: function () {
                return 'table-row';
            },
            columns: [{
                field: 'logId',
                title: '流水号'
            }, {
                field: 'acctMonth',
                title: '账期'
            }, {
                field: 'latnId',
                title: 'c4id'
            }, {
                field: 'areaName',
                title: 'c4名称'
            }, {
                field: 'reportId',
                title: '报表编号'
            }, {
                field: 'zbCode',
                title: '指标编号'
            }, {
                field: 'rate',
                title: '切割比例'
            }]
        });


    };

    //刷新数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
    };

    return oTableInit;
};


//Table初始化
var TableInit2 = function () {
    var oTableInit = new Object();

    //初始化Table
    oTableInit.Init = function () {
        $('#table_upload2').bootstrapTable({
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
            uniqueId: "ID",                     //每一行的唯一标识，一般为主键列
            showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            rowStyle: function () {
                return 'table-row';
            },
            columns: [{
                field: 'logId',
                title: '流水号'
            }, {
                field: 'acctMonth',
                title: '账期'
            }, {
                field: 'fileName',
                title: '导入文件'
            }, {
                field: 'count',
                title: '记录数'
            }, {
                field: 'userId',
                title: '操作人ID'
            }, {
                field: 'detail',
                title: '导入说明'
            }, {
                field: 'importDate',
                title: '导入时间'
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
        'click .remove': function (e, value, row, index) {
            removeData(row);
        }
    };

    //操作显示format
    function operateFormatter(value, row, index) {
        return [
            '<button type="button" class="remove btn btn-danger btn-xs">删除</button>'
        ].join('');
    }

    //刷新数据
    oTableInit.load = function (data) {
        $('#table_upload2').bootstrapTable('load', data);
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            month: $("#upload_month").val(),
            reportId: orgTree.val()
        };
        return temp;
    };
    return oTableInit;
};

