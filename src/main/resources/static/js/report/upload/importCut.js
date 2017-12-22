var table;
var orgTree;
var isTree;
function initCut() {
    table = new TableInit();
    table.Init();

    buildSelect('upload_month', months);
    orgTree = new ZtreeSelect("treeOrg", "menuContent", "upload_latnId", 50);
    orgTree.Init(orgs);
    isTree = new ZtreeSelect("treeOrg2", "menuContent2", "upload_incomeSource", 90);
    isTree.Init(incomeSources);


    initForm();

}

function queryLog() {
    $.ajax({
        type: "POST",
        url: hostUrl + "importCut/listCut",
        data: {
            month: $("#upload_month").val(),
            latnId: orgTree.val(),
            incomeSource: isTree.val(),
            shareType: $("#upload_cutType").val()
        },
        dataType: "json",
        success: function (r) {
            if (r.state) {
                var data = r.data;
                table.load(data);

            } else {
                toastrError('查询失败'+r.msg);
               
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
            latnId: 'checkHidden',
            incomeSource: 'checkHidden'
        },
        messages: {
            file: "必须选择文件",
            incomeSource: "必须输入收入来源",
            latnId: "必须选择本地网"
        },
        ignore: "",
        submitHandler: function (form) {
            $(form).ajaxSubmit({
                url: hostUrl + "importCut/upload",
                type: 'post',
                contentType: 'multipart/form-data',
                beforeSubmit: function () {
                    $('#btn_upload').button("loading");
                },
                success: function (r) {
                    $('#btn_upload').button("reset");
                    if (r.state) {
                        $(form).resetForm();
                        orgTree.reset();
                        isTree.reset();

                        toastr.info('导入成功');
                        queryLog()

                    } else {
                        toastrError('导入失败:' + r.msg);
                    }
                },
                error: function (r) {
                    $('#btn_upload').button("reset");
                    toastrError('导入失败'+r);
                }
            });
        }
    });


    $('#form_upload').on("change", "input[type=text], input[name], select", function () {
        $('#form_upload').validate().element(this);
    });

}


function removeData() {
    var month = $("#upload_month").val();
    var latnId = orgTree.val();
    var incomeSource = isTree.val();
    var shareType = $("#upload_cutType").val();

    editAlert('警告', '是否确定删除:  账期' + month + ", 地市" + latnId
        + ', 收入来源' + incomeSource + ', 切割类型' + shareType, '删除', function () {
        $.ajax({
            type: "POST",
            url: hostUrl + "importCut/remove",
            data: {
                month: month,
                latnId: latnId,
                incomeSource: incomeSource,
                shareType: shareType
            },
            dataType: "json",
            success: function (r) {
                if (r.state) {
                    toastr.info('删除成功');
                    hideAlert();

                    queryLog()
                } else {
                    toastrError('提删除失败'+r.msg);
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
            data: [],
            columns: [{
                field: 'ruleId',
                title: '编号'
            }, {
                field: 'acctMonth',
                title: '账期'
            }, {
                field: 'areaId',
                title: 'c4Id'
            }, {
                field: 'areaName',
                title: 'c4名称'
            }, {
                field: 'bureauId',
                title: 'c5Id'
            }, {
                field: 'bureauName',
                title: 'c5Id'
            }, {
                field: 'incomeSource',
                title: '收入来源'
            }, {
                field: 'codeName',
                title: '收入来源名称'
            }, {
                field: 'activeFlag',
                title: '是否有效'
            }, {
                field: 'shareType',
                title: '切割粒度'
            }, {
                field: 'rate',
                title: '比例'
            }]
        });


    };


    //刷新数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
    };


    return oTableInit;
};

