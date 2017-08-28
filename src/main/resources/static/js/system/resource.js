var oTable;
function initResource() {
    oTable = new TableInit();
    oTable.Init();


    $('#select_search_type').on('change', function (e) {
        oTable.refresh();
    });

    $("#btn_edit").click(function () {
        var selections = $('#table_resource').bootstrapTable('getSelections');
        updateResource(selections);
    });

    initRG();
}


var rg_alls;
function initRG() {
    rg_alls = new createRadioGroup($('#resource_alls'));
    var alls = [{value: "1", text: '是'}, {value: "0", text: '否'}];
    rg_alls.init('alls', alls);
}

function updateResource(selections) {

    if (selections.length == 0) {
        toastr.info('请选择数据');
        return;
    }

    showPanel(0);

    var ids = new Array;
    var names = "";
    selections.forEach(function (resource) {
        ids.push(resource.id);
        names += resource.name + ", ";
    });

    //填入表单属性
    $('#resource_id').val(ids);
    $('#resource_name').val(names);


}

function doUpdate() {
    var form = $('#resource_form');


    form.ajaxSubmit({
        url: hostUrl + 'resource/update',
        type: 'post',
        resetForm: true,
        beforeSubmit: function () {
            $("#btn_save").button("loading");
        },
        success: function (r) {
            $("#btn_save").button("reset");

            if (r.state) {
                toastr.info('更新成功');

                showPanel(1);
                oTable.refresh();
            } else {
                toastr.error('更新失败');
                toastr.error(r.msg);
            }
        },
        error: function () {
            $("#btn_save").button("reset");
            toastr.error("发送请求失败");
        }
    });
}

//Table初始化
var TableInit = function () {
    var oTableInit = new Object();
    //初始化Table
    oTableInit.Init = function () {
        $('#table_resource').bootstrapTable({
            url: hostUrl + 'resource/list',         //请求后台的URL（*）
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
                checkbox: true
            }, {
                field: 'id',
                title: '资源ID'
            }, {
                field: 'name',
                title: '资源名称'
            }, {
                field: 'type',
                title: '资源类型'
            }, {
                field: 'alls',
                title: '无需权限',
                formatter: function (value, row, index) {
                    if (value) {
                        return '是';
                    } else {
                        return '权限控制';
                    }
                }
            }, {
                field: 'operate',
                title: '操作',
                events: operateEvents,
                formatter: function (value, row, index) {
                    return [
                        '<button type="button" class="edit btn btn-info btn-xs">修改</button>'
                    ].join('');
                }
            }]
        });
    };

    //操作 监听
    window.operateEvents = {

        'click .edit': function (e, value, row, index) {
            var selections = new Array();
            selections.push(row);
            updateResource(selections);
        }
    };


    //刷新数据
    oTableInit.refresh = function () {
        $('#table_resource').bootstrapTable('refresh');
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            limit: params.limit,   //页面大小
            offset: params.offset,  //页码
            type: $("#select_search_type").val()
        };
        return temp;
    };
    return oTableInit;
};


function showPanel(a) {
    if (a) {
        titleContentHeader("资源管理")
        $("#form-panel").hide();
        $("#table-panel").show();
    } else {
        titleContentHeader("资源配置")
        $("#form-panel").show();
        $("#table-panel").hide();

        var form = $("#resource_form");
        form.resetForm();
        rg_alls.val("0");
    }


}




