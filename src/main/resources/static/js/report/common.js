/**
 * ztree下拉框
 */
var ZtreeSelect = function(a, b, c, d) {
	var oZtree = new Object();
	var ztreeId = a;// zTree id
	var divId = b;// 容器 id
	var inputId = c;// 输入框 id

	var data = null;// 缓存
	var first = null;// 第一个值

	var xWidth = d == null ? 0 : d;// 额外宽度

	// 初始化数据
	oZtree.Init = function(datas, hasAll) {
		data = datas;
		var nodes = [];
		datas.forEach(function(data, i) {
			if (i == 0) {
				first = data;
			}
			var node = new Object();
			node.id = data.id;
			node.pId = data.parentId == null ? 0 : data.parentId;
			node.name = data.name;
			node.data = data.data;
			node.open = data.lvl > first.lvl ? false : true;
			nodes.push(node)
		});

		$.fn.zTree.init($("#" + ztreeId), treeSetting, nodes);

		// 添加监听
		$('#' + inputId).unbind('focus');
		$('#' + inputId).val(first.name);
		$('#' + inputId).next().val(first.data);
		$('#' + inputId).on('focus', function() {
			oZtree.showMenu($('#' + inputId));
		});

	};

	// 初始化 多选框
	oZtree.reset = function() {
		oZtree.Init(data);
	};

	// 显示下拉菜单
	oZtree.showMenu = function(input) {
		var inputOffset = input.offset();
		// 设置ztree宽度
		$("#" + ztreeId).css({
			width : (input.outerWidth() + xWidth) + "px"
		});
		// 设置下拉框位置
		$("#" + divId).css({
			left : inputOffset.left + "px",
			top : inputOffset.top + input.outerHeight() + "px"
		}).slideDown("fast");

		$("body").bind("mousedown", onBodyDown);
	};

	// 隐藏下拉菜单
	oZtree.hideMenu = function() {
		$("#" + divId).fadeOut("fast");
		$("body").unbind("mousedown", onBodyDown);
	};

	// 全部展开下拉列表
	oZtree.expand = function(flag) {
		var treeObj = $.fn.zTree.getZTreeObj(ztreeId);
		treeObj.expandAll(flag == null ? true : flag);
	};

	// 获取值
	oZtree.val = function(value) {
		if (value != null) {
			$('#' + inputId).next().val(value);
			return

		}
		return $('#' + inputId).next().val();
	};

	// 获取名字
	oZtree.txt = function(name) {
		if (name != null) {
			$('#' + inputId).val(name);
			return

		}
		return $('#' + inputId).val();
	};

	function onBodyDown(event) {
		if (!(event.target.id == inputId || event.target.id == divId || $(
				event.target).parents("#" + divId).length > 0)) {
			oZtree.hideMenu();
		}
	}

	// ztree配置
	var treeSetting = {

		data : {
			simpleData : {
				enable : true,
				idKey : "id",
				pIdKey : "pId",
				rootPId : 0
			}
		},
		callback : {
			onClick : zTreeOnClick
		}
	};

	function zTreeOnClick(event, treeId, treeNode) {
		$('#' + inputId).val(treeNode.name);
		$('#' + inputId).next().val(treeNode.data);
		$('#' + inputId).next().change();
		oZtree.hideMenu();
	}

	return oZtree;
}

/**
 * 配置下拉框,填充option
 * 
 * @param id
 * @param data
 */
var buildSelect = function(id, data, allId, allName) {
	var $select = $('#' + id);
	$select.empty();
	if (allId != null) {
		allName = allName == null ? "全部" : allName;
		var option = '<option value="' + allId + '">' + allName + '</option>';
		$select.append(option);
	}
	data = data == null ? [] : data;
	data.forEach(function(d,i) {
		var selected ='';
		if(i==2){
			selected= ' selected = "selected" ';
		}
		var option = '<option value="' + d.data+'"' + selected+ '>' + d.name +'</option>';
		$select.append(option);
	});
};

var CommSelect = function(id, data, allId, allName) {
	var $select = $('#' + id);
	$select.empty();
	if (allId != null) {
		allName = allName == null ? "全部" : allName;
		var option = '<option value="' + allId + '">' + allName + '</option>';
		$select.append(option);
	}
	data = data == null ? [] : data;
	data.forEach(function(d,i) {
		
		var option = '<option value="' + d.data+'">' + d.name +'</option>';
		$select.append(option);
	});
};
