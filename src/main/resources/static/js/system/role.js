var oTable;
var menuTree;
var permissionTree;
function initRole() {

    oTable = new TableInit();
    oTable.Init();

    menuTree = new MenuTree();
    permissionTree = new PermissionTree();

    //初始化Button的点击事件
    $("#btn_query").click(function () {
        oTable.refresh();
    });

    $("#btn_add").click(function () {
        addRole();
    });


    initForm();
    initTree();
}

function initTree() {
    //获取权限 相关菜单和权限信息
    $.post(hostUrl + "role/mp")
        .done(function (r) {
            if (r.state) {
                var data = r.data;
                menuTree.Init(data.menus);
                permissionTree.Init(data.permissions);
            } else {
                toastr.error('获取信息失败，请重试');
            }
        })
        .fail(function () {
            toastr.error('发送请求失败');
        });

}

function initForm() {
    initValidator();

    $.validator.addMethod("role", function (value, element, params) {
        return this.optional(element) || /^(?!_)[a-zA-Z_]+$/.test(value);
    }, "字母和下划线组合,不能下划线开头");
    validatorForm = $('#role_form').validate({
        rules: {
            name: "required",
            role: {
                required: true,
                role: true
            }
        },
        messages: {
            name: "角色名不能为空",
            role: {
                required: "角色英文名不能为空 (提示:字母和下划线组合)",
                role: "字母和下划线组合,不能下划线开头"
            }
        },
        submitHandler: function (form) {
            doSave();
        }
    });

}


/**
 * 处理 并提交更新
 */
function doSave() {
    var treeMenu = $.fn.zTree.getZTreeObj("treeMenu");
    var treePermission = $.fn.zTree.getZTreeObj("treePermission");

    var menus = treeMenu.getChangeCheckedNodes();
    var permissions = treePermission.getChangeCheckedNodes();
    var menu = [];
    menus.forEach(function (a) {
        var temp = new Object();
        temp.id = a.rId;
        temp.add = a.checked;
        menu.push(temp);
    });
    var permission = [];
    permissions.forEach(function (a) {
        var temp = new Object();
        temp.id = a.rId;
        temp.add = a.checked;
        permission.push(temp);
    });

    var id = $("#role_id").val().trim();
    var name = $("#role_name").val().trim();
    var role = $("#role_role").val().trim();

    //如果id为空，则为新增角色
    if (id == "") {
        var param = {
            name: name,
            role: role,
            menu: menu,
            permission: permission
        };
        $.ajax({
            type: "POST",
            url: hostUrl + "role/add",
            data: JSON.stringify(param),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            beforeSend: function () {
                $("#btn_save").button("loading");
            },
            success: function (r) {
                $("#btn_save").button("reset");

                if (r.state) {
                    toastr.info('添加成功');
                    showPanel(1);
                    oTable.refresh();
                } else {
                    toastr.info('添加失败');
                }
            },
            error: function (result) {
                $("#btn_save").button("reset");
                toastr.error('发送请求失败');
            }
        });
    } else {
        var param = {
            id: id,
            name: name,
            role: role,
            menu: menu,
            permission: permission
        };
        $.ajax({
            type: "POST",
            url: hostUrl + "role/update",
            data: JSON.stringify(param),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            beforeSend: function () {
                $("#btn_save").button("loading");
            },
            success: function (r) {
                $("#btn_save").button("reset");

                if (r.state) {
                    toastr.info('修改成功');
                    showPanel(1);
                    oTable.refresh();
                } else {
                    toastr.error('修改失败');
                }
            },
            error: function (result) {
                $("#btn_save").button("reset");
                toastr.error('发送请求失败');
            }
        });
    }


}


/**
 * 添加角色
 */
function addRole() {

    showPanel(0, 'add');

}


/**
 * 查看 或者 修改角色信息
 * @param role
 * @param type
 */
function viewRole(role, type) {

    showPanel(0, type);

    //填入角色信息
    $("#role_id").val(role.id);
    $("#role_name").val(role.name);
    $("#role_role").val(role.role);


    //获取权限 相关菜单和权限信息
    $.post(hostUrl + "role/info", {id: role.id, name: role.name})
        .done(function (r) {
            if (r.state) {
                var data = r.data;


                menuTree.loadData(data);
                permissionTree.loadData(data);

            } else {
                toastr.error('查看角色信息失败，请重试');
            }
        })
        .fail(function () {
            toastr.error('发送请求失败');
        });


}

function deleteRole(id) {
    editAlert('警告', '是否确定删除该条角色信息', '删除', function () {
        $.ajax({
            type: "POST",
            url: hostUrl + "role/delete",
            data: {"id": id},
            dataType: "json",
            success: function (r) {
                if (r.state) {
                    toastr.info('删除成功');
                    hideAlert();
                    oTable.refresh();
                } else {
                    toastr.error('删除失败');
                    toastr.error(r.msg);
                }
            },
            error: function (r) {
                toastr.error('发送请求失败');
            }
        });
    });
    showAlert();

}

var MenuTree = function () {
    var oZtree = new Object();
    oZtree.id = "treeMenu";//zTree id
    //初始化数据
    oZtree.Init = function (datas) {
        var nodes = [];
        datas.forEach(function (data) {
            var node = new Object();
            node.id = data.id;
            node.pId = data.parentId;
            node.name = data.name;
            node.open = true;
            if (data.alls == 1) {
                node.chkDisabled = true;
                node.checked = true;
            }

            node.checkedOld = node.checked;
            node.rId = data.resourceId;
            nodes.push(node)
        });

        oZtree.data = nodes;
        $.fn.zTree.init($("#" + oZtree.id), treeSetting, nodes);
    };

    //初始化 多选框
    oZtree.loadData = function (resources) {
        var nodes = [];
        $.extend(true, nodes, oZtree.data);

        nodes.forEach(function (node) {
            if ($.inArray(node.rId, resources) != -1) {
                node.checked = true;
            }
        });

        $.fn.zTree.init($("#" + oZtree.id), treeSetting, nodes);
    };

    //初始化 多选框
    oZtree.reset = function () {
        $.fn.zTree.init($("#" + oZtree.id), treeSetting, oZtree.data);
    };

    //定制获取值
    oZtree.val = function () {
        var tree = $.fn.zTree.getZTreeObj(oZtree.id);

        var changes = tree.getChangeCheckedNodes();
        var datas = [];

        changes.forEach(function (a) {
            var temp = new Object();
            temp.id = a.rId;
            temp.add = a.checked;
            datas.push(temp);
        });
        return datas;
    };

    //ztree配置
    var treeSetting = {
        check: {
            enable: true,
            chkDisabledInherit: true,
            chkboxType: {"Y": "p", "N": "s"}
        },
        data: {
            simpleData: {
                enable: true
            }
        }
    };

    return oZtree;
};


var PermissionTree = function () {
    var oZtree = new Object();
    oZtree.id = "treePermission";//zTree id
    //初始化数据
    oZtree.Init = function (datas) {
        var nodes = [];
        datas.forEach(function (data) {
            var node = new Object();
            node.id = data.id;
            node.pId = data.parentId;
            node.name = data.name;
            node.open = true;
            if (data.alls == 1) {
                node.chkDisabled = true;
                node.checked = true;
            }

            node.checkedOld = node.checked;
            node.rId = data.resourceId;
            nodes.push(node)
        });

        oZtree.data = nodes;
        $.fn.zTree.init($("#" + oZtree.id), treeSetting, nodes);
    };

    //初始化 多选框
    oZtree.loadData = function (resources) {
        var nodes = [];
        $.extend(true, nodes, oZtree.data);

        nodes.forEach(function (node) {
            if ($.inArray(node.rId, resources) != -1) {
                node.checked = true;
            }
        });

        $.fn.zTree.init($("#" + oZtree.id), treeSetting, nodes);
    };

    //初始化 多选框
    oZtree.reset = function () {
        $.fn.zTree.init($("#" + oZtree.id), treeSetting, oZtree.data);
    };

    //定制获取值
    oZtree.val = function () {
        var tree = $.fn.zTree.getZTreeObj(oZtree.id);

        var changes = tree.getChangeCheckedNodes();
        var datas = [];

        changes.forEach(function (a) {
            var temp = new Object();
            temp.id = a.rId;
            temp.add = a.checked;
            datas.push(temp);
        });
        return datas;
    };

    //ztree配置
    var treeSetting = {
        check: {
            enable: true,
            chkDisabledInherit: true,
            chkboxType: {"Y": "", "N": ""}
        },
        data: {
            simpleData: {
                enable: true
            }
        }
    };

    return oZtree;
};


//Table初始化
var TableInit = function () {
    var oTableInit = new Object();


    //初始化Table
    oTableInit.Init = function () {
        $('#table_role').bootstrapTable({
            url: hostUrl + 'role/list',         //请求后台的URL（*）
            method: 'post',                      //请求方式（*）
            toolbar: '#toolbar',                //工具按钮用哪个容器
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
            search: true,                       //是否显示表格搜索
            strictSearch: false,                 //设置为 true启用 全匹配搜索，否则为模糊搜索
            showColumns: true,                  //是否显示所有的列
            showRefresh: true,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            height: 600,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "ID",                     //每一行的唯一标识，一般为主键列
            showToggle: true,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            columns: [{
                checkbox: true
            }, {
                field: 'id',
                title: '角色ID',
                width: 80
            }, {
                field: 'name',
                title: '角色名'
            }, {
                field: 'role',
                title: 'role'
            }, {
                field: 'operate',
                title: '操作',
                events: operateEvents,
                formatter: operateFormatter
            },]
        });


    };

    //操作 监听
    window.operateEvents = {
        'click .view': function (e, value, row, index) {
            viewRole(row, "view");
        },
        'click .edit': function (e, value, row, index) {
            viewRole(row, "edit");
        },
        'click .remove': function (e, value, row, index) {
            deleteRole(row.id);
        }
    };

    //操作显示format
    function operateFormatter(value, row, index) {
        //如果是超级管理员，则无法修改和删除
        if (row.role == 'admin')
            return [
                '<button type="button" class="view btn btn-primary btn-xs">查看</button>'
            ].join('');
        else
            return [
                '<button type="button" class="view btn btn-primary btn-xs">查看</button> \
                 <button type="button" class="edit btn btn-info btn-xs">修改</button> \
                 <button type="button" class="remove btn btn-danger btn-xs">删除</button>'
            ].join('');
    }

    //刷新数据
    oTableInit.refresh = function () {
        $('#table_role').bootstrapTable('refresh');
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            limit: params.limit,   //页面大小
            offset: params.offset,  //页码
            name: $("#input_search_role").val()
        };
        return temp;
    };
    return oTableInit;
};


function showPanel(a, b) {
    if (a) {
        titleContentHeader("角色管理");
        $("#form-panel").hide();
        $("#table-panel").show();
    } else {
        $("#form-panel").show();
        $("#table-panel").hide();

        var form = $("#role_form");
        form.resetForm();
        $.fn.zTree.destroy();

        resetValidator(form);

        menuTree.reset();
        permissionTree.reset();

        switch (b) {
            case 'add':
                titleContentHeader("新增角色");
                $('#btn_save').show();
                break;
            case 'view':
                titleContentHeader("查看角色");
                $('#btn_save').hide();
                break;
            case 'edit':
                titleContentHeader("修改角色");
                $('#btn_save').show();
                break;
        }
    }


}



