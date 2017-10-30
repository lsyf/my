/**
 * 组织ztree
 */
var OrgZtree = function (id, div, input) {
    var oZtree = new Object();
    oZtree.id = id;//zTree id
    oZtree.div = div;//容器 id
    oZtree.input = input;//输入框 id

    //初始化数据
    oZtree.Init = function (datas) {
        oZtree.data = datas;
        var nodes = [];
        datas.forEach(function (data) {
            var node = new Object();
            node.id = data.id;
            node.pId = data.parentId == null ? 0 : data.parentId;
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


    oZtree.val = function () {
        return $('#' + oZtree.input).next().val();
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
