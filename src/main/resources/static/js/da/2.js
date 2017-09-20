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
            url: hostUrl + 'da/da2',         //请求后台的URL（*）
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
                {
                    field: 'latnName',
                    title: '地区',
                    align: 'center'
                },
                {
                    field: 'lastYearMonth',
                    title: toMonth(-12),
                    formatter: dataRound,
                    align: 'center'
                },
                {
                    field: 'lastMonth',
                    title: toMonth(-1),
                    formatter: dataRound,
                    align: 'center'
                },
                {
                    field: 'thisMonth',
                    title: toMonth(0),
                    formatter: dataRound,
                    align: 'center'
                },
                {
                    field: 'totalThisYear',
                    title: '本年累计',
                    formatter: dataRound,
                    align: 'center'
                },
                {
                    field: 'totalLastYear',
                    title: '去年累计',
                    formatter: dataRound,
                    align: 'center'
                },
                {
                    field: 'yearTotalGrowthRate',
                    title: '年同比',
                    formatter: dataPercent,
                    align: 'center'
                },
                {
                    field: 'monthGrowthRate',
                    title: '月环比',
                    formatter: dataPercent,
                    align: 'center'
                },
                {
                    field: 'monthBudget',
                    title: '预算',
                    formatter: dataRound,
                    align: 'center'
                },
                {
                    field: 'monthBudgetGap',
                    title: '预算缺口',
                    formatter: dataRound,
                    align: 'center'
                }
            ]
        });


    };

    function dataRound(a) {
        return toDecimal(a, 2);
    }

    function dataPercent(a) {
        return toPercent(a, 2);
    }


    function toMonth(v) {
       month =  $('#datepicker').val().trim();
        return moment(month,'YYYYMM').add(v, 'months').format('YYYYMM');
    }


    //刷新数据
    oTableInit.refresh = function () {
        $('#table').bootstrapTable('destroy');
        oTableInit.Init();
    };


    return oTableInit;
};
