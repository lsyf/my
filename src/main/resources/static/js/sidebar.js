function loadSidebar() {
    $.post(hostUrl + 'menu/sidebar')
        .done(function (r) {

            if (r.state) {
                //加载用户信息
                userInfo(r);

                var data = r.data.menus;
                var map = toMap(data);
                var menu = new Object();
                menu = toMenu(0, map, menu);

                var root = 'sidebar-menu';
                createMenu(root, menu, 0);


            } else {
                toastr.error('菜单加载失败，请刷新重试');
                toastr.error(r.msg);
            }
        })
        .fail(function (r) {
            toastr.error("发送请求失败");
        });
}

/**
 * 加载用户信息
 * @param r
 */
function userInfo(r) {
    user.id = r.data.id;
    user.username = r.data.username;

    $('.text_username').text(user.username);
}

/**
 *  以父节点 分组转换成 map模型
 */
function toMap(data) {

    var map = new Map();
    data.forEach(function (a) {
        //如果url以'/'开头，则截取
        if (a.url != null && a.url.indexOf('/') == 0) {
            a.url = a.url.substring(1);
        }
        a.url = hostUrl + a.url;

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
 * 使用递归创建 菜单树结构
 * @param i
 * @param map
 * @param a
 * @returns {*}
 */
function toMenu(i, map, a) {
    a.kids = map.get(i);
    if (a.kids != undefined) {
        a.kids.forEach(function (b) {
            toMenu(b.id, map, b);
        });
    }
    return a;
}


/**
 * 创建菜单目录
 * @param id 父元素ID
 * @param menu 当前菜单对象
 * @param i 当前层级
 * @param paths 当前菜单路径
 */
function createMenu(id, menu, i, paths) {
    if (menu.name == undefined) {//进入菜单
        //console.log(i + '所有:' + menu.id + '-' + menu.name);
        menu.paths = new Array();
        menu.kids.forEach(function (kid) {
            createMenu(id, kid, i, menu.paths);
        });
    } else {
        if (menu.type == 'menu') {//菜单目录
            //console.log(i + '菜单:' + menu.id + '-' + menu.name);

            //定制id
            var id_li = 'siderbar-li-' + menu.id;
            var id_ul = 'siderbar-ul-' + menu.id;

            //控制图标
            var class_icon = "fa fa-folder";
            if (i != 0) {
                class_icon = "fa fa-files-o";
            }

            var temp = $(
                '<li class="treeview" id="' + id_li + '">\
                    <a href="#">\
                         <i class="' + class_icon + '"></i>\
                             <span>' + menu.name + '</span>\
                             <span class="pull-right-container">\
                                <i class="fa fa-angle-left pull-right"></i>\
                             </span>\
                     </a>\
                     <ul class="treeview-menu" id="' + id_ul + '">\
                     </ul>\
                 </li>');
            $('#' + id).append(temp);

            i++;//深度加1
            var path = [menu.name];
            menu.paths = paths.concat(path);
            if (menu.kids != undefined && menu.kids != null && menu.kids.length != 0) {
                menu.kids.forEach(function (kid) {
                    createMenu(id_ul, kid, i, menu.paths);
                });
            }

        } else if (menu.type == 'page') {//页面
            //console.log(i + '根子菜单:' + menu.id + '-' + menu.name);

            //控制图标
            var class_icon = "fa fa-link";
            if (i != 0) {
                class_icon = "fa fa-circle-o";
            }

            //路径深度
            var path = [menu.name];
            menu.paths = paths.concat(path);


            //定制id
            var id_li = 'siderbar-li-' + menu.id;


            var temp = '<li id="' + id_li + '"> \
                            <a class="sidebar-menu-li" data-url="' + menu.url + '" \
                               data-addtab="' + menu.id + '" \
                               data-title="' + menu.name + '" >  \
                                <i class="' + class_icon + '"></i> \
                                <span>' + menu.name + '</span> \
                            </a>\
                        </li>';
            $('#' + id).append(temp);


        }

    }
}


