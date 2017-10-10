var oTable;
function init() {

    initDatePicker();

    oTable = new TableInit();
    oTable.Init();


    initEvent();

}

function initDatePicker() {
    $('#datepicker ').datepicker({
        format: "yyyymm",
        startView: 1,
        minViewMode: 1,
        maxViewMode: 2,
        todayBtn: "linked",
        language: "zh-CN",
        todayHighlight: true
    });

    m_this = moment().format('YYYYMM');
    $('#datepicker').val(m_this);
}

function initEvent() {

    $('#btn_query').button("loading");
    $('#btn_query').on('click', function () {
        $('#btn_query').button("loading");

        oTable.refresh()
    });

    $('#table').on('load-success.bs.table', function () {
        $('#btn_query').button("reset");
    });
    $('#table').on('load-error.bs.table', function (a, b, c) {
        $('#btn_query').button("reset");
        console.log(a)
        console.log(b)
        console.log(c)
    });
    $('#table').on('click-cell.bs.table', function (element, field, value, row) {
        console.log(field)
        console.log(value)
        console.log(row.name)
    });
}

//Table初始化
var TableInit = function () {
    var oTableInit = new Object();


    //初始化Table
    oTableInit.Init = function () {
        $('#table').bootstrapTable({
            url: hostUrl + 'da0/report3',         //请求后台的URL（*）
            method: 'post',                      //请求方式（*）
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            sortable: false,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            // height: 700,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            contentType: 'application/x-www-form-urlencoded',
            queryParams: function (params) {
                var temp = {
                    month: $('#datepicker').val().trim()
                };
                return temp;
            },
            columns: [
                [{
                    field: 'name',
                    title: '收入分类',
                    width: 150,
                    rowspan: 2,
                    valign: 'middle',
                    align: 'center'
                }, {
                    title: '本月完成',
                    colspan: 3,
                    align: 'center'
                }, {
                    title: '本月完成',
                    colspan: 5,
                    align: 'center'
                }],
                [{
                    field: 'monthTotal',
                    title: '本月实绩',
                    align: 'right'
                }, {
                    field: 'monthDiff',
                    title: '环比差额',
                    align: 'right'
                }, {
                    field: 'monthGap',
                    title: '预算缺口',
                    align: 'right'
                }, {
                    field: 'yearTotal',
                    title: '本年累计',
                    align: 'right'
                }, {
                    field: 'yearGrowRate',
                    title: '累计同比增长率',
                    formatter: percetFormatter,
                    align: 'right'
                }, {
                    field: 'yearGap',
                    title: '累计预算缺口',
                    align: 'right'
                }, {
                    field: 'yearComRate',
                    title: '年度预算完成率',
                    formatter: percetFormatter,
                    align: 'right'
                }, {
                    field: 'percent',
                    title: '占总收入占比',
                    formatter: percetFormatter,
                    align: 'right'
                }]
            ]
        });


    };

    function percetFormatter(v) {
        if (v == null) {
            return;
        }
        return toDecimal(v * 100, 2) + '%';
    }

    //刷新数据
    oTableInit.refresh = function () {
        $('#table').bootstrapTable('refresh');
    };


    return oTableInit;
};
