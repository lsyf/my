function initMenu() {
    loadMenu();

    initRG();
    initForm();
}

var rg_type;
function initRG() {
    rg_type = new createRadioGroup($('#menu_type'));
    var types = [{value: "menu", text: '菜单'}, {value: "page", text: '页面'}];
    rg_type.init('type', types);
}

function initForm() {
    initValidator();
    validatorForm = $('#menu_form').validate({
        rules: {
            name: "required"
        },
        messages: {
            name: "节点名不能为空"
        },
        submitHandler: function (form) {
            var id = $('#menu_id').val();
            $("#btn_save").button("loading");

            if (id == "") {
                $(form).ajaxSubmit({
                    url: hostUrl + 'menu/add',
                    type: 'post',
                    success: function (r) {
                        $("#btn_save").button("reset");

                        if (r.state) {
                            toastr.info('添加成功');

                            showPanel(1);
                            loadMenu();
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
                    url: hostUrl + 'menu/update',
                    type: 'post',
                    success: function (r) {
                        $("#btn_save").button("reset");

                        if (r.state) {
                            toastr.info('更新成功');

                            showPanel(1);
                            loadMenu();
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
 * 添加子节点
 * @param id
 */
function addMenu(json) {

    showPanel(0);

    var menu = JSON.parse(json);
    if (json == 0) {
        menu = new Object();
        menu.name = "所有菜单";
        menu.id = "0";
    }

    //填入表单属性
    $('#div_parentName').show();
    $('#menu_parentName').val(menu.name);
    $('#menu_parentId').val(menu.id);
    $('#menu_sorting').val('1');
    rg_type.val('menu');

}

/**
 * 修改菜单
 * @param id
 */
function updateMenu(json) {
    showPanel(0);


    var menu = JSON.parse(json);

    //填入表单属性
    $('#div_parentName').hide();
    $('#menu_name').val(menu.name);
    $('#menu_id').val(menu.id);
    rg_type.val(menu.type);
    $('#menu_url').val(menu.url);
    $('#menu_sorting').val(menu.sorting + '');

}


/**
 * 加载菜单数据
 */
function loadMenu() {
    $.post(hostUrl + 'menu/list/')
        .done(function (r) {
            if (r.state) {
                var data = r.data;
                $('#tbody').find('.tr_remove').remove();

                //之所以如此复杂的处理数据模型
                //是因为排序，首先获取为最顶层菜单
                var map = mapMenu(data);
                processMenu(0, map);


            } else {
                toastr.error('菜单加载失败，请刷新重试');
                toastr.error(r.msg);
            }
        })
        .fail(function () {
            toastr.error("发送请求失败");
        });
}

/**
 * 使用map根据父节点 分组
 */
function mapMenu(data) {

    var map = new Map();
    data.forEach(function (a) {
        var id = a.parentId;
        var temp = map.get(id);
        if (temp == undefined) {
            temp = new Array();
            map.set(id, temp);
        }
        temp.push(a);
    });
    return map;
}

/**
 * 处理数据
 * @param id
 * @param map
 */
function processMenu(id, map) {
    var list = map.get(id);
    if (list != null) {
        list.forEach(function (b) {
            drawTabel(b);
            processMenu(b.id, map);
        });
    }

    //将表格转成treegrid
    $('.tree').treegrid();
    $('.tree').treegrid('getRootNodes').treegrid('expand');
}

/**
 * 根据menu绘制table
 * @param menu
 * @param i
 */
function drawTabel(menu) {
    var class_id = "treegrid-" + menu.id;
    //如果parentId为0，添加特定class
    var class_parent = " treegrid-parent-" + menu.parentId;
    var class_remove = " tr_remove";
    var type;//类型
    var operate = '';
    var json = JSON.stringify(menu);

    if (menu.type == 'menu') {
        type = '菜单';
        //菜单可以添加子节点
        operate = "<button type='button' class='btn btn-primary btn-xs' onclick=addMenu('" + json + "') >添加子节点</button> ";
    } else {
        type = '页面';
    }
    operate += "<button type='button' class='btn btn-info btn-xs'  onclick=updateMenu('" + json + "') >修改</button>\
                    <button type='button' class='btn btn-danger btn-xs' onclick=deleteMenu('" + json + "') >删除</button>";

    var tr = '<tr class="' + class_id + class_parent + class_remove + '">\
                        <td>' + menu.name + '</td>\
                        <td>' + type + '</td>\
                        <td>' + menu.sorting + '</td>\
                        <td>' + (menu.url == undefined ? "" : menu.url) + '</td>\
                        <td>' + operate + '</td>\
                       </tr>';
    $('#tbody').append(tr);
}


/**
 * 删除菜单
 * @param id
 */
function deleteMenu(json) {

    editAlert('警告', '是否确定删除该条菜单信息', '删除', function () {
        $.ajax({
            url: hostUrl + 'menu/delete',
            type: "POST",
            data: json,
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (r) {
                if (r.state) {
                    toastr.info('删除成功');
                    hideAlert();
                    loadMenu();
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
        titleContentHeader("菜单管理")
        $("#form-panel").hide();
        $("#table-panel").show();
    } else {
        titleContentHeader("菜单配置")
        $("#form-panel").show();
        $("#table-panel").hide();

        var form = $("#menu_form");
        form.resetForm();
        rg_type.reset();

        resetValidator(form);
    }


}