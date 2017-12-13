var oTable;
var roleZtree;
var orgZtree;

function initUser() {

    oTable = new TableInit();
    oTable.Init();

    roleZtree = new RoleZtree();
    orgZtree = new OrgZtree();

    initTree();
    initEvent();


    initRG();
    initForm();


}

function initEvent() {
//初始化Button的点击事件
    $("#btn_query").click(function () {
        oTable.refresh();
    });

    $("#btn_add").click(function () {
        addUser();
    });
}


function initTree() {
    //加载所有角色信息列表
    $.post(hostUrl + "role/listAll")
        .done(function (r) {
            if (r.state) {
                var data = r.data;
                roleZtree.Init(data);
            } else {
                toastr.error('请求角色信息失败，请重试');
                toastr.error(r.msg);
            }
        })
        .fail(function () {
            toastr.error('发送请求失败');
        });

    //加载所有地市组织信息列表
    $.post(hostUrl + "organization/listOrgs", {type: "local_net"})
        .done(function (r) {
            if (r.state) {
                var data = r.data;
                orgZtree.Init(data);
            } else {
                toastr.error('请求角色信息失败，请重试');
                toastr.error(r.msg);
            }
        })
        .fail(function () {
            toastr.error('发送请求失败');
        });

    //加载所有部门组织信息列表
    $.post(hostUrl + "organization/listOrgs", {type: "user_dept"})
        .done(function (r) {
            if (r.state) {
                var data = r.data;

                var $dept = $('#user_deptId');
                $dept.empty();
                data.forEach(function (d) {
                    var option = '<option value="' + d.id + '">' + d.name + '</option>';
                    $dept.append(option);
                });
            } else {
                toastr.error('请求角色信息失败，请重试');
                toastr.error(r.msg);
            }
        })
        .fail(function () {
            toastr.error('发送请求失败');
        });
}

var rg_locked;
function initRG() {
    rg_locked = new createRadioGroup($('#user_locked'));
    var lockeds = [{value: 1, text: '是'}, {value: 0, text: '否'}];
    rg_locked.init('locked', lockeds);
}


function initForm() {
    initValidator();

    validatorForm = $('#user_form').validate({
        rules: {
            username: {
                required: true
            }
        },
        messages: {
            username: {
                required: "用户名不能为空"
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
    var roles = roleZtree.val();
    var orgs = orgZtree.val();
    var id = $("#user_id").val().trim();
    var username = $("#user_username").val().trim();
    var password = $("#user_password").val().trim();
    var nickname = $("#user_nickname").val().trim();
    var eip = $("#user_eip").val().trim();
    var phone = $("#user_phone").val().trim();
    var email = $("#user_email").val().trim();
    var deptId = $("#user_deptId").val();
    var locked = rg_locked.val();

    if (id == "") {
        var param = {
            username: username,
            password: password,
            nickname: nickname,
            eip: eip,
            phone: phone,
            email: email,
            roles: roles,
            orgs: orgs,
            deptId: deptId
        };
        $.ajax({
            type: "POST",
            url: hostUrl + "user/add",
            data: JSON.stringify(param),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            beforeSend: function () {
                $("#addUser_add").button("loading");
            },
            success: function (r) {
                $("#addUser_add").button("reset");

                if (r.state) {
                    toastr.info('添加成功');

                    showPanel(1);
                    oTable.refresh();
                } else {
                    toastr.error('添加失败');
                    toastr.error(r.msg);
                }
            },
            error: function (result) {
                $("#addUser_add").button("reset");
                toastr.error('发送请求失败');
            }
        });
    } else {
        var param = {
            id: id,
            username: username,
            password: password,
            nickname: nickname,
            eip: eip,
            phone: phone,
            email: email,
            locked: locked,
            roles: roles,
            orgs: orgs,
            deptId: deptId
        };
        $.ajax({
            type: "POST",
            url: hostUrl + "user/update",
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
                    toastr.error(r.msg);
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
 * 添加用户
 */
function addUser() {
    showPanel(0, 'add');
}


/**
 * 查看 或者 修改用户信息
 * @param user
 * @param type
 */
function viewUser(user, type) {

    showPanel(0, type);

    //填入用户信息
    $("#user_id").val(user.id);
    $("#user_username").val(user.username);
    $("#user_password").val('');
    $("#user_nickname").val(user.nickname ? user.nickname : "");
    $("#user_eip").val(user.eip ? user.eip : "");
    $("#user_phone").val(user.phone ? user.phone : "");
    $("#user_email").val(user.email ? user.email : "");
    rg_locked.val(user.locked);


    //获取权限 相关菜单和权限信息
    $.post(hostUrl + "user/info", {"id": user.id})
        .done(function (r) {
            if (r.state) {
                var data = r.data;
                var roleIds = data.roleIds;
                var orgIds = data.orgIds;
                var deptId = data.deptId;

                $("#user_deptId").val(deptId);
                roleZtree.loadData(roleIds);
                orgZtree.loadData(orgIds);
            } else {
                toastr.error('请求用户信息失败，请重试');
            }
        })
        .fail(function () {
            toastr.error('发送请求失败');
        });


}


function deleteUser(id) {
    editAlert('警告', '是否确定删除该条用户信息', '删除', function () {
        $.ajax({
            type: "POST",
            url: hostUrl + "user/delete",
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
        $('#table_user').bootstrapTable({
            url: hostUrl + 'user/list',         //请求后台的URL（*）
            method: 'post',                      //请求方式（*）
            toolbar: '#toolbar',                //工具按钮用哪个容器
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: true,                   //是否显示分页（*）
            sortable: false,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            queryParams: oTableInit.queryParams,//传递参数（*）
            contentType: 'application/x-www-form-urlencoded',
            sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
            pageNumber: 1,                       //初始化加载第一页，默认第一页
            pageSize: 10,                       //每页的记录行数（*）
            pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
            // search: true,                       //是否显示表格搜索
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
                field: 'id',
                title: 'ID',
                width: 80
            }, {
                field: 'username',
                title: '用户名'
            }, {
                field: 'nickname',
                title: '昵称'
            }, {
                field: 'lastLogin',
                title: '上次登录',
                formatter: function (v) {
                    if (v) {
                        v = moment(v).format('YYYY-MM-DD HH:mm:ss');
                    }
                    return v;
                }
            }, {
                field: 'locked',
                title: '冻结',
                formatter: function (value) {
                    return value ? '是' : '否';
                }
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
        'click .view': function (e, value, row, index) {
            viewUser(row, "view");
        },
        'click .edit': function (e, value, row, index) {
            viewUser(row, "edit");
        },
        'click .remove': function (e, value, row, index) {
            deleteUser(row.id);
        }
    };

    //操作显示format
    function operateFormatter(value, row, index) {
        return [
            '<button type="button" class="view btn btn-primary btn-xs">查看</button> \
             <button type="button" class="edit btn btn-info btn-xs">修改</button> \
             <button type="button" class="remove btn btn-danger btn-xs">删除</button>'
        ].join('');
    }

    //刷新数据
    oTableInit.refresh = function () {
        $('#table_user').bootstrapTable('refresh');
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            limit: params.limit,   //页面大小
            offset: params.offset,  //页码
            name: $("#input_search").val()
        };
        return temp;
    };
    return oTableInit;
};

function showPanel(a, b) {
    if (a) {
        titleContentHeader("用户管理");
        $("#form-panel").hide();
        $("#table-panel").show();
    } else {
        $("#form-panel").show();
        $("#table-panel").hide();

        var form = $("#user_form");
        form.resetForm();
        rg_locked.reset();

        roleZtree.reset();
        orgZtree.reset();

        resetValidator(form);

        switch (b) {
            case 'add':
                titleContentHeader("新增用户");
                $('#btn_save').show();
                break;
            case 'view':
                titleContentHeader("查看用户");
                $('#btn_save').hide();
                break;
            case 'edit':
                titleContentHeader("修改用户");
                $('#btn_save').show();
                break;
        }
    }


}

/**
 * 角色ztree
 */
var RoleZtree = function () {
    var oZtree = new Object();
    oZtree.id = "treeRole";//zTree id
    oZtree.div = "menuContent";//容器 id
    oZtree.input = "user_role";//输入框 id

    //初始化数据
    oZtree.Init = function (roles) {
        oZtree.data = roles;
        var nodes = [];
        roles.forEach(function (role) {
            var node = new Object();
            node.id = role.id;
            node.pId = 0;
            node.name = role.name;
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
    oZtree.loadData = function (roleIds) {
        var tree = $.fn.zTree.getZTreeObj(oZtree.id);
        var nodes = tree.getNodes();
        var txt = "";
        nodes.forEach(function (node) {
            if ($.inArray(node.id, roleIds) != -1) {
                node.checked = true;
                txt += node.name + ", "
            } else {
                node.checked = false;
            }
            node.checkedOld = node.checked;
        });

        $.fn.zTree.init($("#" + oZtree.id), treeSetting, nodes);
        $('#' + oZtree.input).val(txt);

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

    //定制获取值
    oZtree.val = function () {
        var tree = $.fn.zTree.getZTreeObj(oZtree.id);

        var changes = tree.getChangeCheckedNodes();
        var roles = [];
        changes.forEach(function (a) {
            var temp = new Object();
            temp.id = a.id;
            temp.add = a.checked;
            roles.push(temp);
        });
        return roles;
    };

    function onBodyDown(event) {
        if (!( event.target.id == oZtree.input
            || event.target.id == oZtree.div
            || $(event.target).parents("#" + oZtree.div).length > 0)) {
            oZtree.hideMenu();
        }
    }

    //菜单ztree配置
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
        },
        callback: {
            onCheck: zTreeOnCheck
        }
    };

    function zTreeOnCheck(event, treeId, treeNode) {
        var tree = $.fn.zTree.getZTreeObj(oZtree.id);
        var nodes = tree.getCheckedNodes(true);
        var txt = "";
        nodes.forEach(function (node) {
            txt += node.name + ", "
        });
        $('#' + oZtree.input).val(txt);
    };

    return oZtree;
}

/**
 * 组织ztree
 */
var OrgZtree = function () {
    var oZtree = new Object();
    oZtree.id = "treeOrg";//zTree id
    oZtree.div = "menuContent2";//容器 id
    oZtree.input = "user_org";//输入框 id

    //初始化数据
    oZtree.Init = function (datas) {
        oZtree.data = datas;
        var nodes = [];
        datas.forEach(function (data) {
            var node = new Object();
            node.id = data.id;
            node.pId = data.parentId;
            node.name = data.name;
            node.open = true;
            node.checkedOld = false;
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
    oZtree.loadData = function (orgIds) {
        var tree = $.fn.zTree.getZTreeObj(oZtree.id);
        var root = tree.getNodes();
        var nodes = tree.transformToArray(root);
        var txt = "";
        nodes.forEach(function (node) {
            if ($.inArray(node.id, orgIds) != -1) {
                node.checked = true;
                txt += node.name + ", "
            } else {
                node.checked = false;
            }
            node.checkedOld = node.checked;
            node.open = true;
        });

        $.fn.zTree.init($("#" + oZtree.id), treeSetting, root);
        $('#' + oZtree.input).val(txt);

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

    //定制获取值
    oZtree.val = function () {
        var tree = $.fn.zTree.getZTreeObj(oZtree.id);

        var changes = tree.getCheckedNodes();
        var datas = [];
        changes.forEach(function (a) {
            var temp = new Object();
            temp.id = a.id;
            temp.add = true;
            datas.push(temp);
        });
        return datas;
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
        check: {
            enable: true,
            chkDisabledInherit: true,
            chkboxType: {"Y": "", "N": ""}
        },
        data: {
            simpleData: {
                enable: true
            }
        },
        callback: {
            onCheck: zTreeOnCheck
        }
    };

    function zTreeOnCheck(event, treeId, treeNode) {
        var tree = $.fn.zTree.getZTreeObj(oZtree.id);
        var nodes = tree.getCheckedNodes(true);
        var txt = "";
        nodes.forEach(function (node) {
            txt += node.name + ", "
        });
        $('#' + oZtree.input).val(txt);
    };

    return oZtree;
}



