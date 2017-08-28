function initOrganization() {


    loadList();

    initForm();
}

function initForm() {
    initValidator();

    validatorForm = $('#form-body').validate({
        rules: {
            name: 'required',
            data: 'required'
        },
        messages: {
            name: "节点名不能为空",
            data: "节点值不能为空",
        },
        submitHandler: function (form) {
            doSave();
        }
    });

}

function loadList() {

    $.post(hostUrl + "organization/listRoot")
        .done(function (r) {
            if (r.state) {
                var data = r.data;

                var nodes = [];
                data.forEach(function (org) {
                    var node = org;
                    node.pId = node.parentId;
                    node.isParent = true;
                    node.open = true;
                    nodes.push(node)
                });

                $.fn.zTree.init($("#treeOrganization"), treeSetting, nodes);
            } else {
                toastr.error('请求组织信息失败: ' + r.msg);

                $.fn.zTree.init($("#treeOrganization"), treeSetting, []);
            }
        })
        .fail(function () {
            toastr.error('发送请求失败');
        });
}


/**
 * 添加组织根节点
 */
function addOrgRoot() {

    //ztree添加
    var zTree = $.fn.zTree.getZTreeObj("treeOrganization");
    maxId++;
    zTree.addNodes(null,
        {
            id: maxId,
            name: "新建根节点(未保存)",
            lvl: 1,
            isAdd: true
        });


}

/**
 * 编辑字典/节点
 * @param node
 */
function editOrg(node) {

    showPanel(1);

    //如果是新加节点则不传递id(id由数据库生成)
    if (!node.isAdd) {
        $('#form-id').val(node.id);
    }

    var parentNode = node.getParentNode();
    if (parentNode && parentNode.name) {
        $('#form-parent').val(parentNode.name);
        $('#form-parentId').val(node.parentId);
        $('#form-parentIds').val(node.parentIds);
        $('#div-parent').show();
    } else {//根节点
        $('#div-parent').hide();
    }
    $('#form-name').val(node.name);
    $('#form-data').val(node.data ? node.data : '');
    $('#form-lvl').val(node.lvl);
    $('#form-sorting').val(node.sorting ? node.sorting : '1');
    $('#form-remark').val(node.remark ? node.remark : '');


}

function doSave() {

    var form = $('#form-body');
    var id = $("#form-id").val();
    if (id == "") {
        form.ajaxSubmit({
            url: hostUrl + 'organization/add',
            type: 'post',
            beforeSubmit: function () {
                $("#form_save").button("loading");
            },
            success: function (r) {
                $("#form_save").button("reset");
                if (r.state) {
                    toastr.info('保存成功');

                    afterSave(r.data);
                    showPanel(0);
                } else {
                    toastr.error('保存失败');
                    toastr.error(r.msg);
                }
            },
            error: function () {
                $("#form_save").button("reset");
                toastr.error('网络请求错误，请重试');
            }
        });
    } else {
        form.ajaxSubmit({
            url: hostUrl + 'organization/update',
            type: 'post',
            beforeSubmit: function () {
                $("#form_save").button("loading");
            },
            success: function (r) {
                $("#form_save").button("reset");
                if (r.state) {
                    toastr.info('修改成功');

                    afterSave(r.data);
                    showPanel(0);
                } else {
                    toastr.error('修改失败');
                    toastr.error(r.msg);
                }
            },
            error: function () {
                $("#form_save").button("reset");
                toastr.error('网络请求错误，请重试');
            }
        });
    }


}

function afterSave(data) {
    var zTree = $.fn.zTree.getZTreeObj("treeOrganization");

    var chooseNode = (zTree.getSelectedNodes())[0];

    chooseNode.id = data.id;
    chooseNode.name = data.name;
    chooseNode.data = data.data;
    chooseNode.sorting = data.sorting;
    chooseNode.remark = data.remark;
    chooseNode.parentId = data.parentId;
    chooseNode.parentIds = data.parentIds;
    chooseNode.lvl = data.lvl;

    //chooseNode其他已有的数据不用配置

    //如果有isAdd字段则是 新增
    if (chooseNode.isAdd) {
        chooseNode.isAdd = false;
    }

    zTree.updateNode(chooseNode);
}


function doDelete() {
    var ztree = $.fn.zTree.getZTreeObj("treeOrganization");

    var nodes = ztree.getSelectedNodes();
    if (nodes == null || nodes.length == 0) {
        toastr.info('请先选中再删除');
        return;
    }
    var chooseNode = nodes[0];
    if (chooseNode.isAdd) {
        ztree.removeNode(chooseNode);
        showPanel(0);
        return;
    }

    var id = chooseNode.id;
    var path = chooseNode.parentIds;
    path = path == null ? id + "/" : path + id + "/"
    editAlert('警告', '是否确认删除节点及其子节点: ' + chooseNode.name, '删除', function () {
        $.post(hostUrl + 'organization/delete', {"id": id, "path": path})
            .done(function (r) {
                if (r.state) {
                    toastr.info('删除成功');
                    hideAlert();
                    showPanel(0);

                    //移除节点及其子节点
                    ztree.removeChildNodes(chooseNode);
                    ztree.removeNode(chooseNode);

                } else {
                    toastr.error('删除失败');
                    toastr.error(r.msg);
                }
            })
            .fail(function () {
                toastr.error('网络请求错误，请重试');
            });
    });

    showAlert();
}


var maxId = 1000000000;
var treeSetting = {
    data: {
        simpleData: {
            enable: true
        }
    },
    view: {
        addHoverDom: function (treeId, treeNode) {
            var sObj = $("#" + treeNode.tId + "_span");
            if (treeNode.editNameFlag || $("#addBtn_" + treeNode.tId).length > 0)
                return;
            //如果节点被标记为新增未保存
            if (treeNode.isAdd)
                return;

            var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
                + "' title='add node' onfocus='this.blur();'></span>";
            sObj.after(addStr);
            var btn = $("#addBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                var zTree = $.fn.zTree.getZTreeObj("treeOrganization");

                maxId++;
                var lvl = treeNode.lvl + 1;
                var parentIds = treeNode.parentIds + treeNode.id + '/';

                zTree.addNodes(treeNode,
                    {
                        id: maxId,
                        pId: treeNode.id,
                        name: "新建节点(未保存)",
                        parentId: treeNode.id,
                        parentIds: parentIds,
                        lvl: lvl,
                        isAdd: true
                    });
                return false;
            });
        },
        removeHoverDom: function (treeId, treeNode) {
            $("#addBtn_" + treeNode.tId).unbind().remove();
        },
        selectedMulti: false
    },
    async: {
        enable: true,
        url: hostUrl + "organization/listNodes",
        autoParam: ["id"],
        dataFilter: function (treeId, parentNode, r) {
            if (!r || !r.state) return null;
            if (r.data == null || r.data.length == 0) {
                toastr.info("无子节点");
                return null;
            }
            var childNodes = process(parentNode.id, r.data);
            return childNodes;
        }
    },
    callback: {
        onClick: function (event, treeId, treeNode) {
            //打开编辑节点界面
            editOrg(treeNode);
        },
        onAsyncError: function (treeId, treeNode) {
            toastr.error('加载子节点失败，请刷新重试');
        },
        beforeAsync: function (treeId, treeNode) {
            return (treeNode != null)
                && (treeNode.id != null);
        }
    }
};

function process(id, data) {
    var map = mapOrg(data);
    var temp = new Object();
    temp = processOrg(id, map, temp);
    return temp.children;
}

/**
 * 将数据映射成map结构(id为key,父节点为id的子节点数组为value)
 * @param data
 * @returns {Map}
 */
function mapOrg(data) {

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
 * 将map转换成tree结构
 * @param id 父节点id
 * @param map
 * @param temp
 * @returns {*}
 */
function processOrg(id, map, temp) {
    temp.children = map.get(id);
    if (temp.children != null) {
        temp.children.forEach(function (b) {
            processOrg(b.id, map, b);
        });
    }
    return temp;
}


function showPanel(a, b) {
    if (b) return;
    if (a) {
        $("#form-panel").show();
    } else {
        $("#form-panel").hide();
    }

    //重置隐藏
    $('#div-parent').hide();

    var form = $("#form-body");
    form.resetForm();

    resetValidator(form);
}
