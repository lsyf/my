var table;
var orgTree;
function initIncomeData() {
    table = new TableInit();
    table.Init();

    buildSelect('upload_month', months);

    orgTree = new ZtreeSelect("treeOrg", "menuContent", "upload_latnId");
    orgTree.Init(orgs);


    initForm();

}

function queryLog() {
    var title = ' 稽核结果';
    $.ajax({
        type: "POST",
        url: hostUrl + "importIncomeData/list",
        data: {
            month: $('#upload_month').val(),
            latnId: orgTree.val()
        },
        dataType: "json",
        success: function (r) {
            if (r.state) {
                $('#title_table').text(title);
                table.load(r.data);
            } else {
                toastrError(r.msg);
            }
        },
        error: function (result) {
            toastrError('发送请求失败');
        }
    });

}

function uploadData() {
    $('#form_upload').submit();
}

function itsmData() {
    var selects = table.getSelections();
    if (selects == null || selects.length == 0) {
        toastr.info('未选中任何流水号');
        return;
    }

    var logIds = [];
    var txt = "";
    var temp;
    var error = null;
    try {
        selects.forEach(function (d, i) {
            //不符合送审  或者已送审 校验
            if (d.isItsm != '1' || d.itsmStatus != '0') {
                error = '不符合送审资格(无需送审或已送审)';
                throw new Error(error);
            }
            //地市统一校验
            if (i == 0) {
                temp = d.city;
            } else if (temp !== d.city) {
                error = '送审地市不一致';
                throw new Error(error);
            }

            logIds.push(d.logId);
            txt += d.logId + ', '
        });
    }
    catch (e) {
        error = e.message;
    }

    if (error != null) {
        toastr.info(error);
        return;
    }

    editAlert('警告', '是否确定送审流水号: ' + txt, '送审', function () {
        $.ajax({
            type: "POST",
            url: hostUrl + "importIncomeData/itsm",
            data: {logIds: logIds},
            dataType: "json",
            beforeSend: function () {
                toastr.info('送审中...');
                $('#btn_itsm').button("loading");
                hideAlert();
            },
            success: function (r) {
                if (r.state) {
                    toastr.info('送审成功');
                    queryLog();
                } else {
                    toastrError('送审失败' + r.msg);
                }
            },
            error: function (result) {
                toastrError('请求失败');
            },
            always: function () {
                $('#btn_itsm').button("reset");
            }
        });
    });
    showAlert();

}

function initForm() {
    initValidator();

    validatorForm = $('#form_upload').validate({
        rules: {
            file: 'required',
            remark: 'required',
            latnId: 'checkHidden'
        },
        messages: {
            file: "必须选择文件",
            remark: "必须输入备注",
            latnId: "必须选择本地网"
        },
        ignore: "",
        submitHandler: function (form) {
            $(form).ajaxSubmit({
                url: hostUrl + "importIncomeData/upload",
                type: 'post',
                contentType: 'multipart/form-data',
                beforeSubmit: function () {
                    $('#btn_upload').button("loading");
                },
                success: function (r) {
                    $('#btn_upload').button("reset");
                    if (r.state) {
                        $('#upload_file').fileinput('clear');
                        toastr.info('导入成功');
                        queryLog();
                    } else {

                        toastrError('导入失败:' + r.msg);
                    }
                },
                error: function (r) {
                    $('#btn_upload').button("reset");
                    toastrError('导入失败:' + r);

                }
            });
        }
    });


    $('#form_upload').on("change", "input[type=text], input[name], select", function () {
        $('#form_upload').validate().element(this);
    });

}

function removeData() {

    var selects = table.getSelections();
    if (selects == null || selects.length == 0) {
        toastr.info('未选中任何流水号');
        return;
    }

    var logIds = [];
    var txt = "";
    var month = "";
    selects.forEach(function (d, i) {
        logIds.push(d.logId);
        txt += d.logId + ', ';
        month = d.month;
    });

    editAlert('警告', '是否确定删除流水号: ' + txt, '删除', function () {
        $.ajax({
            type: "POST",
            url: hostUrl + "importIncomeData/remove",
            data: {logIds: logIds, month: month},
            dataType: "json",
            success: function (r) {
                if (r.state) {
                    toastr.info('删除成功');
                    hideAlert();
                    queryLog()
                } else {
                    toastrError('删除失败:' + r.msg);
                }
            },
            error: function (result) {
                toastrError('发送请求失败');
            }
        });
    });
    showAlert();
}

function commitData() {

    var selects = table.getSelections();
    if (selects == null || selects.length == 0) {
        toastr.info('未选中任何流水号');
        return;
    }

    var logIds = [];
    var txt = "";
    selects.forEach(function (d, i) {
        logIds.push(d.logId);
        txt += d.logId + ', '
    });

    editAlert('警告', '是否确定提交流水号: ' + txt, '提交', function () {
        hideAlert();
        $('#btn_commit').button("loading");
        $.ajax({
            type: "POST",
            url: hostUrl + "importIncomeData/commit",
            data: {logIds: logIds},
            dataType: "json",
            success: function (r) {
                $('#btn_commit').button("reset");
                if (r.state) {
                    toastr.info('提交成功');
                    queryLog()
                } else {
                    toastrError('提交失败:' + r.msg);
                }

            },
            error: function (result) {
                $('#btn_commit').button("reset");
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
            toolbar: "#table_title",
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: true,                   //是否显示分页（*）
            sortable: true,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            contentType: 'application/x-www-form-urlencoded',
            sidePagination: "client",           //分页方式：client客户端分页，server服务端分页（*）
            pageNumber: 1,                       //初始化加载第一页，默认第一页
            pageSize: 10,                       //每页的记录行数（*）
            pageList: [10, 50, 100],        //可供选择的每页的行数（*）
            search: true,                       //是否显示表格搜索
            strictSearch: false,                 //设置为 true启用 全匹配搜索，否则为模糊搜索
            showColumns: false,                  //是否显示所有的列
            showRefresh: false,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            height: 750,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "ID",                     //每一行的唯一标识，一般为主键列
            showToggle: false,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            rowStyle: function () {
                return 'table-row';
            },
            data: [],
            columns: [{
                field: 'check',
                checkbox: true,
                align: 'center',
                valign: 'middle'
            }, {
                field: 'logId',
                sortable: true,
                title: '流水号'
            }, {
                field: 'city',
                sortable: true,
                title: '地市',
                formatter: function (v) {
                    return [
                        '<div title="' + v + '" ' +
                        'style="width:50px; white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
                        + v + '</div>'
                    ].join('');
                }
            }, {
                field: 'fileName',
                sortable: true,
                title: '导入文件',
                formatter: function (v) {
                    return [
                        '<div title="' + v + '" ' +
                        'style="width:100px; white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
                        + v + '</div>'
                    ].join('');
                }
            }, {
                field: 'num',
                sortable: true,
                title: '记录数'
            }, {
                field: 'sum',
                sortable: true,
                title: '合计(元)'
            }, {
                field: 'sum2',
                sortable: true,
                title: '收入(元)'
            }, {
                field: 'userName',
                sortable: true,
                title: '操作人'
            }, {
                field: 'action',
                sortable: true,
                title: '提交状态'
            }, {
                field: 'isItsm',
                title: '送审资格',
                sortable: true,
                formatter: function (v) {
                    if (v == '1')
                        return '需要送审';
                    else
                        return '无需送审';
                }
            }, {
                field: 'itsmStatus',
                title: '送审状态',
                sortable: true,
                formatter: function (v) {
                    switch (v) {
                        case '0':
                            return null;
                        case '1':
                            return '待审批';
                        case '2':
                            return '审批通过';
                        case '3':
                            return '审批不通过';
                    }
                }
            }, {
                field: 'itsmOrderNo',
                sortable: true,
                title: 'ITSM单号'
            }, {
                field: 'itsmUrl',
                title: 'ITSM审批流转',
                formatter: function (a, b, c) {
                    if (b.itsmOrderNo == null) {
                        return null;
                    }
                    return [
                        '<button type="button" class="view btn btn-primary btn-xs">订单跟踪</button>'
                    ].join('');
                },
                events: operateEvents
            }, {
                field: 'remark',
                title: '导入说明',
                formatter: function (v) {
                    return [
                        '<div title="' + v + '" ' +
                        'style="width:200px; white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
                        + v + '</div>'
                    ].join('');
                }
            }]
        });


    };

    //操作 监听
    window.operateEvents = {
        'click .view': function (e, value, row, index) {
            console.log(row.itsmUrl)
            window.open(row.itsmUrl)
        }
    };

    //加载数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
    };

    oTableInit.getSelections = function () {
        return $('#table_upload').bootstrapTable('getSelections');
    };


    return oTableInit;
};

