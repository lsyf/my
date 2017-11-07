var table;
var orgTree;
function initGroup() {
    table = new TableInit();
    table.Init();


    orgTree = new ZtreeSelect("treeOrg", "menuContent", "upload_latnId");
    orgTree.Init(orgs);


    initForm();

}

function queryLog() {
    $.ajax({
        type: "POST",
        url: hostUrl + "importGroup/list",
        data: {

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
                toastr.error('查询失败');
                toastr.error(r.msg);
            }
        },
        error: function (result) {
            toastr.error('发送请求失败');
        }
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
            url: hostUrl + "importGroup/remove",
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
                    toastr.error('提删除失败');
                    toastr.error(r.msg);
                }
            },
            error: function (result) {
                toastr.error('发送请求失败');
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
                url: hostUrl + "importGroup/upload",
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


                        toastr.info('提交成功');
                       queryLog()
                    } else {
                        toastr.error('提交失败:' + r.msg);
                    }
                },
                error: function (r) {
                    $('#btn_upload').button("reset");
                    toastr.error('提交失败');
                    toastr.error(r);
                }
            });
        }
    });


    $('#form_upload').on("change", "input[type=text], input[name], select", function () {
        $('#form_upload').validate().element(this);
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
            rowStyle: function () {
                return 'table-row';
            },
            data: [],
            columns: [{
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
                field: 'remark',
                title: '导入说明'
            }, {
                field: 'operate',
                title: '操作',
                events: operateEvents,
                formatter: operateFormatter
            }]
        });


    };



    //刷新数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
    };


    return oTableInit;
};

