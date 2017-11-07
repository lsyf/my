var table;
var orgTree;
var isTree;
function initRptQuery() {
    table = new TableInit();
    table.Init();

    buildSelect('upload_month', months);
    orgTree = new ZtreeSelect("treeOrg", "menuContent", "upload_latnId", 50);
    orgTree.Init(orgs);
    isTree = new ZtreeSelect("treeOrg2", "menuContent2", "upload_incomeSource", 90);
    isTree.Init(incomeSources);


    initForm();

}


function initForm() {
    initValidator();

    validatorForm = $('#form_upload').validate({
        rules: {
            file: 'required',
            latnId: 'checkHidden',
            incomeSource: 'checkHidden'
        },
        messages: {
            file: "必须选择文件",
            incomeSource: "必须输入收入来源",
            latnId: "必须选择本地网"
        },
        ignore: "",
        submitHandler: function (form) {
            $(form).ajaxSubmit({
                url: hostUrl + "importCut/upload",
                type: 'post',
                contentType: 'multipart/form-data',
                beforeSubmit: function () {
                    $('#btn_upload').button("loading");
                },
                success: function (r) {
                    $('#btn_upload').button("reset");
                    if (r.state) {
                        $(form).resetForm();
                        orgTree.reset();
                        isTree.reset();

                        toastr.info('提交成功');
                        table.refresh();
                    } else {
                        toastr.error('提交失败:' + r.msg);
                    }
                },
                error: function (r) {
                    $('#btn_upload').button("reset");
                    toastr.error('提交失败');
                    toastr.error(r);
                }
            });
        }
    });


    $('#form_upload').on("change", "input[type=text], input[name], select", function () {
        $('#form_upload').validate().element(this);
    });

}


//Table初始化
var TableInit = function () {
    var oTableInit = new Object();


    //初始化Table
    oTableInit.Init = function () {
        $('#table_query').bootstrapTable({
            // url: hostUrl + 'importIncomeData/list',         //请求后台的URL（*）
            // method: 'post',                      //请求方式（*）
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
                field: 'city',
                title: '地市'
            }, {
                field: 'fileName',
                title: '导入文件'
            }, {
                field: 'num',
                title: '记录数'
            }, {
                field: 'sum',
                title: '金额'
            }, {
                field: 'userId',
                title: '操作人ID'
            }, {
                field: 'remark',
                title: '导入说明'
            }, {
                field: 'operate',
                title: '操作',
                events: operateEvents,
                formatter: operateFormatter
            }]
        });


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

    //刷新数据
    oTableInit.load = function (data) {
        $('#table_upload').bootstrapTable('load', data);
    };


    return oTableInit;
};
