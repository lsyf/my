var table;
var orgTree;
function initIncomeData() {
    table = new TableInit();
    table.Init();

    orgTree = new OrgZtree();


    initSelect();
    initForm();
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
                url: hostUrl + "importICT/upload",
                type: 'post',
                contentType: 'multipart/form-data',
                beforeSubmit: function () {
                    $('#btn_upload').button("loading");
                },
                success: function (r) {
                    $('#btn_upload').button("reset");
                    if (r.state) {
                        $(form).resetForm();
                        toastr.info('提交成功');
                        table.refresh();
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

function queryLog() {
    table.refresh();
}

function initSelect() {
    $.post(hostUrl + "date/aroundMonths", {num: 5})
        .done(function (r) {
            if (r.state) {
                $('#upload_month').empty();
                r.data.forEach(function (d) {
                    var option = '<option value="' + d.data + '">' + d.name + '</option>';
                    $('#upload_month').append(option);
                });
            } else {
                toastr.error('月份加载失败');
                toastr.error(r.msg);
            }
        })
        .fail(function () {
            toastr.error('发送请求失败');
        });

    //本地网加载
    $.post(hostUrl + "localNet/listByUser", {lvl: 3})
        .done(function (r) {
            if (r.state) {
                console.log(r.data)
                orgTree.Init(r.data);
            } else {
                toastr.error('本地网加载失败');
                toastr.error(r.msg);
            }
        })
        .fail(function () {
            toastr.error('发送请求失败');
        });
}


function removeData(row) {
    editAlert('警告', '是否确定删除流水号: ' + row.logId, '删除', function () {
        $.ajax({
            type: "POST",
            url: hostUrl + "importICT/remove",
            data: {"logId": row.logId},
            dataType: "json",
            success: function (r) {
                if (r.state) {
                    toastr.info('删除成功');
                    hideAlert();
                    table.refresh();
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

//Table初始化
var TableInit = function () {
    var oTableInit = new Object();


    //初始化Table
    oTableInit.Init = function () {
        $('#table_upload').bootstrapTable({
            url: hostUrl + 'importICT/list',         //请求后台的URL（*）
            method: 'post',                      //请求方式（*）
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: false,                   //是否显示分页（*）
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
    oTableInit.refresh = function () {
        $('#table_upload').bootstrapTable('refresh');
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            month: $("#upload_month").val()
        };
        return temp;
    };
    return oTableInit;
};

/**
 * 组织ztree
 */
var OrgZtree = function () {
    var oZtree = new Object();
    oZtree.id = "treeOrg";//zTree id
    oZtree.div = "menuContent";//容器 id
    oZtree.input = "upload_latnId";//输入框 id

    //初始化数据
    oZtree.Init = function (datas) {
        oZtree.data = datas;
        var nodes = [];
        datas.forEach(function (data) {
            var node = new Object();
            node.id = data.id;
            node.pId = data.pId == null ? 0 : data.pId;
            node.name = data.name;
            node.data = data.data;
            node.open = true;
            nodes.push(node)
        });

        $.fn.zTree.init($("#" + oZtree.id), treeSetting, nodes);

        //添加监听
        $('#' + oZtree.input).unbind('focus');
        $('#' + oZtree.input).val('');
        $('#' + oZtree.input).on('focus', function () {
            oZtree.showMenu($('#' + oZtree.input));
        });

    };


    //初始化 多选框
    oZtree.reset = function () {
        oZtree.Init(oZtree.data);
    };

    //显示下拉菜单
    oZtree.showMenu = function (input) {
        var inputOffset = input.offset();
        //设置ztree宽度
        $("#" + oZtree.id).css({width: input.outerWidth() + "px"});
        //设置下拉框位置
        $("#" + oZtree.div).css({
            left: inputOffset.left + "px",
            top: inputOffset.top + input.outerHeight() + "px"
        }).slideDown("fast");

        $("body").bind("mousedown", onBodyDown);
    };

    oZtree.hideMenu = function () {
        $("#" + oZtree.div).fadeOut("fast");
        $("body").unbind("mousedown", onBodyDown);
    };

    //全部展开下拉列表
    oZtree.expand = function (flag) {
        var treeObj = $.fn.zTree.getZTreeObj(oZtree.id);
        treeObj.expandAll(flag == null ? false : flag);
    };


    function onBodyDown(event) {
        if (!( event.target.id == oZtree.input
            || event.target.id == oZtree.div
            || $(event.target).parents("#" + oZtree.div).length > 0)) {
            oZtree.hideMenu();
        }
    }

    //ztree配置
    var treeSetting = {

        data: {
            simpleData: {
                enable: true,
                idKey: "id",
                pIdKey: "pId",
                rootPId: 0
            }
        },
        callback: {
            onClick: zTreeOnClick
        }
    };

    function zTreeOnClick(event, treeId, treeNode) {
        $('#' + oZtree.input).val(treeNode.name);
        $('#' + oZtree.input).next().val(treeNode.data);
        $('#' + oZtree.input).next().change();
    };

    return oZtree;
}
