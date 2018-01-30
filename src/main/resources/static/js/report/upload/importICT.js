var table;
var orgTree;
function initICT() {
    table = new TableInit();
    table.Init();

    buildSelect('upload_month', months);

    orgTree = new ZtreeSelect("treeOrg", "menuContent", "upload_latnId");
    orgTree.Init(orgs);

    initForm();
}

function queryLog(btn) {
    $.ajax({
        type: "POST",
        url: hostUrl + "importICT/list",
        data: {
            month: $("#upload_month").val()
        },
        dataType: "json",
        beforeSend: function () {
        	$(btn).button("loading");
        },
        success: function (r) {
            if (r.state) {
            	var data = r.data;
            	if(Array.prototype.isPrototypeOf(data) && data.length === 0){
                	toastr.warning('查询数据为空!');
                }
            	table.load(data);
            } else {
                toastrError('查询失败:' + r.msg);
            }
        },
        error: function (result) {
            toastrError('连接服务器请求失败!');
        },
        complete:function(){
        	$(btn).button("reset");
        }
    });

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
                        $('#upload_file').fileinput('clear');
                        toastr.warning('导入成功');

                        queryLog();

                    } else {
                        toastrError('导入失败:' + r.msg);
                    }
                },
                error: function (r) {
                    $('#btn_upload').button("reset");
                    toastrError('导入失败');
                    
                }
            });
        }
    });


    $('#form_upload').on("change", "input[type=text], input[name], select", function () {
        $('#form_upload').validate().element(this);
    });

}

function removeData(row) {
    editAlert('警告', '是否确定删除流水号: ' + row.logId, '删除', function () {
        $.ajax({
            type: "POST",
            url: hostUrl + "importICT/remove",
            data: {logId: row.logId},
            dataType: "json",
            success: function (r) {
                if (r.state) {
                    toastr.warning('删除成功');
                    hideAlert();

                    queryLog();
                } else {
                    toastrError('删除失败:' + r.msg);
                }
            },
            error: function (result) {
            	toastrError('连接服务器请求失败!');
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
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: false,                   //是否显示分页（*）
            sortable: false,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
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
            data: [],
            columns: [{
                field: 'logId',
                title: '流水号'
            }, {
                field: 'fileName',
                title: '导入文件',
                formatter: function (v) {
                    return [
                        '<div title="' + v + '" ' +
                        'style="width:200px; white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
                        + v + '</div>'
                    ].join('');
                }
            }, {
                field: 'num',
                title: '记录数'
            }, {
                field: 'sum',
                title: '金额'
            }, {
                field: 'userName',
                title: '用户名'
            }, {
                field: 'action',
                title: '昵称'
            }, {
                field: 'remark',
                title: '导入说明',
                formatter: function (v) {
                    return [
                        '<div title="' + v + '" ' +
                        'style="width:100px; white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">'
                        + v + '</div>'
                    ].join('');
                }
            }, {
                field: 'operate',
                title: '操作',
                events: operateEvents,
                formatter: operateFormatter
            }]
        });


    };

    //加载数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
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


    return oTableInit;
};

