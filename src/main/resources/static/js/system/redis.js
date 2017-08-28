var oTable;
function initRedis() {
    oTable = new TableInit();
    oTable.Init();

    initEvent();
}

function initEvent() {
    $('#btn_query').on('click', function () {
        oTable.refresh();
    });

    $('#btn_delete').on('click', function () {
        var selections = $('#table_redis').bootstrapTable('getSelections');
        deleteKey(selections);
    });
    $('#btn_form_delete').on('click', function () {
        var key = $('#redis_key').val().trim();
        deleteKey([{key: key}]);
    });
}

function deleteKey(rows) {
    if (rows.length == 0) {
        toastr.info('请选择数据');
        return;
    }
    var keys = new Array();
    rows.forEach(function (row) {
        keys.push(row.key);
    });

    editAlert('警告', '是否确认删除key: ' + JSON.stringify(keys), '删除', function () {
        $.post(hostUrl + 'redis/delete', {"keys": keys})
            .done(function (r) {
                if (r.state) {
                    toastr.info('删除成功');
                    hideAlert();
                    oTable.refresh();
                } else {
                    toastr.error('删除失败');
                    toastr.error(r.msg);
                }
            })
            .fail(function (a) {
                toastr.error('网络请求错误，请重试');
                console.log(a);
            });
    });

    showAlert();
}

function viewKey(key) {
    $.post(hostUrl + 'redis/info', {"key": key})
        .done(function (r) {
            if (r.state) {
                var data = r.data;
                showPanel(0);

                $('#redis_key').val(data.key);
                $('#redis_type').val(data.type);
                $('#redis_time').val(data.time);

            } else {
                toastr.error('查询失败');
                toastr.error(r.msg);
            }
        })
        .fail(function () {
            toastr.error('网络请求错误，请重试');
        });
}

//Table初始化
var TableInit = function () {
    var oTableInit = new Object();

    //初始化Table
    oTableInit.Init = function () {
        $('#table_redis').bootstrapTable({
            url: hostUrl + 'redis/list',         //请求后台的URL（*）
            method: 'post',                      //请求方式（*）
            toolbar: '#toolbar',                //工具按钮用哪个容器
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: true,                   //是否显示分页（*）
            sortable: false,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            sortName: "key",                   //排序方式
            queryParams: oTableInit.queryParams,//传递参数（*）
            contentType: 'application/x-www-form-urlencoded',
            sidePagination: "client",           //分页方式：client客户端分页，server服务端分页（*）
            pageNumber: 1,                       //初始化加载第一页，默认第一页
            pageSize: 10,                       //每页的记录行数（*）
            pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
            search: true,                       //是否显示表格搜索
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
                field: 'key',
                title: 'key'
            }, {
                field: 'operate',
                title: '操作',
                events: operateEvents,
                formatter: operateFormatter
            }]
        });


    };

    //刷新数据
    oTableInit.refresh = function () {
        $('#table_redis').bootstrapTable('refresh');
    };

    //操作 监听
    window.operateEvents = {
        'click .view': function (e, value, row, index) {
            viewKey(row.key);
        },
        'click .remove': function (e, value, row, index) {
            deleteKey([row]);
        }
    };

    //操作显示format
    function operateFormatter(value, row, index) {
        return [
            '<button type="button" class="view btn btn-info btn-xs">查看</button> \
             <button type="button" class="remove btn btn-danger btn-xs">删除</button>'
        ].join('');
    }

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            key: $("#input_key").val().trim()  //搜索
        };
        return temp;
    };

    return oTableInit;
};

function showPanel(a) {
    if (a) {
        titleContentHeader("缓存管理")
        $("#form-panel").hide();
        $("#table-panel").show();
    } else {
        titleContentHeader("缓存信息")
        $("#form-panel").show();
        $("#table-panel").hide();
    }

    var form = $("#redis_form");
    form.resetForm();
}

