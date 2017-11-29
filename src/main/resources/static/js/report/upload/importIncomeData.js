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

                table.load(r.data);
            } else {
                toastr.error(r.msg);
            }
        },
        error: function (result) {
            toastr.error('发送请求失败');
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
                $('#btn_itsm').button("reset");
                if (r.state) {
                    toastr.info('送审成功');
                    queryLog();
                } else {
                    toastr.error('送审失败' + r.msg);
                }
            },
            error: function (result) {
                $('#btn_itsm').button("reset");
                toastr.error('请求失败');
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
                        toastr.error('导入失败:' + r.msg);
                    }
                },
                error: function (r) {
                    $('#btn_upload').button("reset");
                    toastr.error('导入失败');
                    toastr.error(r);
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
    selects.forEach(function (d, i) {
        logIds.push(d.logId);
        txt += d.logId + ', '
    });

    editAlert('警告', '是否确定删除流水号: ' + txt, '删除', function () {
        $.ajax({
            type: "POST",
            url: hostUrl + "importIncomeData/remove",
            data: {logIds: logIds},
            dataType: "json",
            success: function (r) {
                if (r.state) {
                    toastr.info('删除成功');
                    hideAlert();
                    queryLog()
                } else {
                    toastr.error('删除失败:' + r.msg);
                }
            },
            error: function (result) {
                toastr.error('发送请求失败');
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
        $.ajax({
            type: "POST",
            url: hostUrl + "importIncomeData/commit",
            data: {logIds: logIds},
            dataType: "json",
            beforeSubmit: function () {
                $('#btn_commit').button("loading");
            }, success: function (r) {
                $('#btn_commit').button("reset");
                if (r.state) {
                    toastr.info('提交成功');
                } else {
                    toastr.error('提交失败:' + r.msg);
                }
                queryLog()
            },
            error: function (result) {
                $('#btn_commit').button("reset");
                toastr.error('发送请求失败');
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
            search: true,                       //是否显示表格搜索
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
            data: [],
            columns: [{
                field: 'check',
                checkbox: true,
                align: 'center',
                valign: 'middle'
            }, {
                field: 'logId',
                title: '流水号'
            }, {
                field: 'city',
                title: '地市'
            }, {
                field: 'fileName',
                title: '导入文件'
            }, {
                field: 'num',
                title: '记录数'
            }, {
                field: 'sum',
                title: '金额'
            }, {
                field: 'userId',
                title: '操作人ID'
            }, {
                field: 'action',
                title: '提交状态'
            }, {
                field: 'isItsm',
                title: '送审资格',
                formatter: function (v) {
                    if (v == '1')
                        return '需要送审';
                    else
                        return '无需送审';
                }
            }, {
                field: 'itsmStatus',
                title: '送审状态',
                formatter: function (v) {
                    switch (v) {
                        case '0':
                            return null;
                        case '1':
                            return '待审核';
                        case '2':
                            return '审核成功';
                        case '3':
                            return '审核失败';
                    }
                }
            }, {
                field: 'itsmOrderNo',
                title: 'ITSM单号'
            }, {
                field: 'itsmUrl',
                title: 'TISM地址',
                formatter: function () {
                    return [
                        '<button type="button" class="view btn btn-primary btn-xs">查看</button>'
                    ].join('');
                },
                events: operateEvents
            }, {
                field: 'remark',
                title: '导入说明'
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

