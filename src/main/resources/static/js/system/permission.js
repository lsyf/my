function initPermission() {
    loadPermission();

    initForm();
}

function initForm() {
    initValidator();
    validatorForm = $('#permission_form').validate({
        rules: {
            name: "required",
            permission: "required",
        },
        messages: {
            name: "权限名不能为空",
            permission: "权限操作符不能为空",
        },
        submitHandler: function (form) {
            var id = $('#permission_id').val();
            $("#btn_save").button("loading");

            if (id == "") {
                $(form).ajaxSubmit({
                    url: hostUrl + 'permission/add',
                    type: 'post',
                    success: function (r) {
                        $("#btn_save").button("reset");

                        if (r.state) {
                            toastr.info('添加成功');

                            showPanel(1);
                            loadPermission();
                        } else {
                            toastr.error('添加失败');
                            toastr.error(r.msg);
                        }
                    },
                    error: function () {
                        $("#btn_save").button("reset");
                        toastr.error("发送请求失败");
                    }
                });
            } else {
                $(form).ajaxSubmit({
                    url: hostUrl + 'permission/update',
                    type: 'post',
                    success: function (r) {
                        $("#btn_save").button("reset");

                        if (r.state) {
                            toastr.info('更新成功');

                            showPanel(1);
                            loadPermission();
                        } else {
                            toastr.info('更新失败');
                            toastr.error(r.msg);
                        }
                    },
                    error: function () {
                        $("#btn_save").button("reset");
                        toastr.error("发送请求失败");
                    }
                });

            }
        }
    });


}


/**
 * 加载菜单数据
 */
function loadPermission() {
    $.post(hostUrl + 'permission/list/')
        .done(function (r) {
            if (r.state) {
                var data = r.data;
                $('#tbody').empty();
                data.forEach(function (permission, i) {
                    drawTabel(permission);
                });

                //将表格转成treegrid
                $('.tree').treegrid();
            } else {
                toastr.error('权限加载失败，请刷新重试');
                toastr.error(r.msg);
            }
        })
        .fail(function (data) {
            toastr.error("发送请求失败");
        });
}

/**
 * 根据permission绘制table
 * @param permission
 * @param i
 */
function drawTabel(permission) {
    var class_id = "treegrid-" + permission.id;
    var json = JSON.stringify(permission);

    var class_parent = " treegrid-parent-" + permission.parentId;
    var operate = "<button type='button' class='btn btn-primary btn-xs' onclick=addPermission('" + json + "') >添加子节点</button> \
                   <button type='button' class='btn btn-info btn-xs'  onclick=updatePermission('" + json + "') >修改</button>\
                    <button type='button' class='btn btn-danger btn-xs' onclick=deletePermission('" + json + "') >删除</button>";
    //如果为根权限
    if (permission.id == 1) {
        class_parent = "";
        operate = "<button type='button' class='btn btn-primary btn-xs' onclick=addPermission('" + json + "') >添加子节点</button>";

    }

    var tr = '<tr class="' + class_id + class_parent + '">\
                        <td>' + permission.name + '</td>\
                        <td>' + permission.permission + '</td>\
                        <td>' + operate + '</td>\
                       </tr>';
    $('#tbody').append(tr);
}


/**
 * 添加子权限
 * @param id
 */
function addPermission(json) {
    showPanel(0);

    var permission = JSON.parse(json);


    //填入表单属性
    $('#permission_parentName').val(permission.name);
    $('#permission_parentPermission').val(permission.permission);
    $('#permission_parentId').val(permission.id);

    $('#div_parentName').show();
    $('#div_parentPermission').show();
}

/**
 * 修改菜单
 * @param id
 */
function updatePermission(json) {
    showPanel(0);

    var permission = JSON.parse(json);

    //填入表单属性
    $('#permission_name').val(permission.name);
    $('#permission_id').val(permission.id);
    $('#permission_permission').val(permission.permission);

    $('#div_parentName').hide();
    $('#div_parentPermission').hide();
}

/**
 * 删除菜单
 * @param id
 */
function deletePermission(json) {
    editAlert('警告', '是否确定删除该条菜单信息', '删除', function () {
        $.ajax({
            url: hostUrl + 'permission/delete',
            type: "POST",
            data: json,
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (r) {
                if (r.state) {
                    toastr.info('删除成功');
                    hideAlert();
                    loadPermission();
                } else {
                    toastr.error('删除失败');
                    toastr.error(r.msg);
                }
            },
            error: function () {
                toastr.error("发送请求失败");
            }
        });
    });
    showAlert();
}




function showPanel(a) {
    if (a) {
        titleContentHeader("权限管理")
        $("#form-panel").hide();
        $("#table-panel").show();
    } else {
        titleContentHeader("权限配置")
        $("#form-panel").show();
        $("#table-panel").hide();

        var form = $("#permission_form");
        form.resetForm();
        resetValidator(form);
    }


}