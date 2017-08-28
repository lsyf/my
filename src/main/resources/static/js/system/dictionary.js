function initDictionary() {


    loadList();

    initForm();

    $('#query_name').change(function () {
        loadList();
    });
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

    var name = $('#query_name').val().trim();
    $.post(hostUrl + "dictionary/listRoot", {name: name})
        .done(function (r) {
            if (r.state) {
                var data = r.data;

                var nodes = [];
                data.forEach(function (dic) {
                    var node = dic;
                    node.pId = node.parentId;
                    node.isParent = true;
                    nodes.push(node)
                });

                $.fn.zTree.init($("#treeDic"), treeSetting, nodes);
            } else {
                toastr.error('请求字典列表失败: ' + r.msg);
                $.fn.zTree.init($("#treeDic"), treeSetting, []);
            }
        })
        .fail(function () {
            toastr.error('发送请求失败');
        });
}


/**
 * 添加组织根节点
 */
function addDicRoot() {

    //ztree添加
    var zTree = $.fn.zTree.getZTreeObj("treeDic");
    maxId++;
    zTree.addNodes(null,
        {
            id: maxId,
            name: "新建字典(未保存)",
            isParent: true,
            parentId: 0,
            isAdd: true
        });


}

/**
 * 编辑字典/节点
 * @param node
 */
function editDic(node) {

    showPanel(1);

    //如果是新加节点则不传递id(id由数据库生成)
    if (!node.isAdd) {
        $('#form-id').val(node.id);
    }

    var parentNode = node.getParentNode();
    if (parentNode && parentNode.name) {
        $('#form-parent').val(parentNode.name);
        $('#div-parent').show();
    } else {//根节点
        $('#div-parent').hide();
    }
    $('#form-parentId').val(node.parentId);
    $('#form-name').val(node.name);
    $('#form-data').val(node.data ? node.data : '');
    $('#form-sorting').val(node.sorting ? node.sorting : '1');
    $('#form-remark').val(node.remark ? node.remark : '');


}

function doSave() {

    var form = $('#form-body');
    var id = $("#form-id").val();
    if (id == "") {
        form.ajaxSubmit({
            url: hostUrl + 'dictionary/add',
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
            url: hostUrl + 'dictionary/update',
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
    var zTree = $.fn.zTree.getZTreeObj("treeDic");

    var chooseNode = (zTree.getSelectedNodes())[0];

    chooseNode.id = data.id;
    chooseNode.name = data.name;
    chooseNode.data = data.data;
    chooseNode.sorting = data.sorting;
    chooseNode.remark = data.remark;
    chooseNode.parentId = data.parentId;

    //chooseNode其他已有的数据不用配置

    //如果有isAdd字段则是 新增
    if (chooseNode.isAdd) {
        chooseNode.isAdd = false;
    }

    zTree.updateNode(chooseNode);
}


function doDelete() {
    var ztree = $.fn.zTree.getZTreeObj("treeDic");

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
    editAlert('警告', '是否确认删除节点及其子节点: ' + chooseNode.name, '删除', function () {
        $.post(hostUrl + 'dictionary/delete', {"id": id})
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
            //判断是字典(parentId为0) 则显示添加按钮
            //如果节点被标记为新增
            if (treeNode.parentId != 0 || treeNode.isAdd)
                return;

            var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
                + "' title='add node' onfocus='this.blur();'></span>";
            sObj.after(addStr);
            var btn = $("#addBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                var zTree = $.fn.zTree.getZTreeObj("treeDic");

                maxId++;
                zTree.addNodes(treeNode,
                    {
                        id: maxId,
                        pId: treeNode.id,
                        name: "新建节点(未保存)",
                        parentId: treeNode.id,
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
        url: hostUrl + "dictionary/listNodes",
        autoParam: ["id"],
        dataFilter: function (treeId, parentNode, r) {
            if (!r || !r.state) return null;
            if (r.data == null || r.data.length == 0) {
                toastr.info("无子节点");
                return null;
            }
            return r.data;
        }
    },
    callback: {
        onClick: function (event, treeId, treeNode) {
            //打开编辑节点界面
            editDic(treeNode);
        },
        onAsyncError: function (treeId, treeNode) {
            toastr.error('加载子节点失败，请刷新重试');
        },
        beforeAsync: function (treeId, treeNode) {
            return (treeNode != null)
                && (treeNode.id != null)
                && (treeNode.parentId == 0);
        }
    }
};


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
