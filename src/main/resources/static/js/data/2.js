var oTable;
function init() {

    initDatePicker();
    initEvent();

    oTable = new TableInit();
    oTable.Init();


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

    m_this = moment().add(-2, 'M').format('YYYYMM');
    $('#datepicker').val(m_this);
}

function initEvent() {

    $('#btn_query').button("loading");
    $('#btn_query').on('click', function () {
        $('#btn_query').button("loading");

        oTable.refresh()
    });

    //加载成功
    $('#table').on('load-success.bs.table', function (a, data) {
        $('#btn_query').button("reset");
        initChart(data);
    });

    //加载失败
    $('#table').on('load-error.bs.table', function (a, b, c) {
        $('#btn_query').button("reset");
        console.log(a)
        console.log(b)
        console.log(c)
    });

    //表格点击
    $('#table').on('click-cell.bs.table', function (element, field, value, row) {
        console.log(field)
        console.log(value)
        console.log(row.name)
    });
}

function initChart(data) {
    month1 = toMonth(-1);
    month2 = toMonth(0);
    var x = [];
    var y1 = [], y2 = [], y3 = [], y4 = [], y5 = [], y6 = [];
    data.forEach(function (d) {
        x.push(d.latnName);
        y1.push(d.lastMonth);
        y2.push(d.thisMonth);
        y3.push(d.monthGrowthRate * 100);
        y4.push(d.totalLastYear);
        y5.push(d.totalThisYear);
        y6.push(d.yearTotalGrowthRate * 100);
    });


    option1 = {
        color: ['#3398DB'],
        tooltip: {
            trigger: 'axis',
            axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        tooltip: {},
        legend: {
            data: [month1, month2, '月环比'],
            align: 'left',
            left: 10
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                data: x,
                axisTick: {
                    alignWithLabel: true
                }
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '值',
                splitLine: {
                    show: false
                },
            },
            {
                type: 'value',
                name: '增长率',
                splitLine: {
                    show: true,
                    lineStyle: {
                        type: 'dashed',
                        color: ['#ff0000']
                    }
                },
                axisLabel: {
                    formatter: '{value}%'
                }
            }
        ],
        series: [
            {
                name: month1,
                type: 'bar',
                itemStyle: {
                    normal: {
                        color: '#FF0000'
                    }
                },
                data: y1
            },
            {
                name: month2,
                type: 'bar',
                itemStyle: {
                    normal: {
                        color: '#4BACC6'
                    }
                },
                data: y2
            },
            {
                name: '月环比',
                type: 'line',
                itemStyle: {
                    normal: {
                        color: '#46cd34'
                    }
                },
                data: y3,
                yAxisIndex: 1
            }
        ]
    };

    option2 = {
        color: ['#3398DB'],
        tooltip: {
            trigger: 'axis',
            axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        tooltip: {},
        legend: {
            data: ['去年累计', '今年累计', '年累计同比'],
            align: 'left',
            left: 10
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                data: x,
                axisTick: {
                    alignWithLabel: true
                }
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '值',
                splitLine: {
                    show: false
                },
            },
            {
                type: 'value',
                name: '增长率',
                splitLine: {
                    show: true,
                    lineStyle: {
                        type: 'dashed',
                        color: ['#ff0000']
                    }
                },
                axisLabel: {
                    formatter: '{value}%'
                }
            }
        ],
        series: [
            {
                name: '去年累计',
                type: 'bar',
                itemStyle: {
                    normal: {
                        color: '#FF0000'
                    }
                },
                data: y4
            },
            {
                name: '今年累计',
                type: 'bar',
                itemStyle: {
                    normal: {
                        color: '#4BACC6'
                    }
                },
                data: y5
            },
            {
                name: '年累计同比',
                type: 'line',
                itemStyle: {
                    normal: {
                        color: '#46cd34'
                    }
                },
                data: y6,
                yAxisIndex: 1
            }
        ]
    };

    var chart1 = echarts.init(document.getElementById('chart1'));
    chart1.setOption(option1);

    var chart2 = echarts.init(document.getElementById('chart2'));
    chart2.setOption(option2);

}

//Table配置
var TableInit = function () {
    var oTableInit = new Object();
    var $table = $('#table');
    var yearTotalGrowthRateSort;//年同比排序
    var monthGrowthRateSort;//月环比排序
    var monthBudgetGapSort;//预算缺口排序


    //初始化Table
    oTableInit.Init = function () {
        $table.bootstrapTable({
            classes: "table-condensed table-hover",
            url: hostUrl + 'data/da2',         //请求后台的URL（*）
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
            responseHandler: highlightDataHandler,
            rowStyle: function (row, index) {//行样式
                return {classes: "small tableRow"};
            },
            columns: [
                {
                    field: 'latnName',
                    title: '地区',
                    align: 'center',
                    cellStyle: {//列属性
                        classes: "latnName"
                    }
                },
                {
                    field: 'lastYearMonth',
                    title: toMonth(-12),
                    formatter: dataRound,
                    align: 'right'
                },
                {
                    field: 'lastMonth',
                    title: toMonth(-1),
                    formatter: dataRound,
                    align: 'right'
                },
                {
                    field: 'thisMonth',
                    title: toMonth(0),
                    formatter: dataRound,
                    align: 'right'
                },
                {
                    field: 'totalThisYear',
                    title: '本年累计',
                    formatter: dataRound,
                    align: 'right'
                },
                {
                    field: 'totalLastYear',
                    title: '去年累计',
                    formatter: dataRound,
                    align: 'right'
                },
                {
                    field: 'yearTotalGrowthRate',
                    title: '年累计同比',
                    formatter: dataPercent,
                    align: 'right',
                    cellStyle: highlightData
                },
                {
                    field: 'monthGrowthRate',
                    title: '月环比',
                    formatter: dataPercent,
                    align: 'right',
                    cellStyle: highlightData
                },
                {
                    field: 'monthBudget',
                    title: '预算',
                    formatter: dataRound,
                    align: 'right'
                },
                {
                    field: 'monthBudgetGap',
                    title: '预算缺口',
                    formatter: dataRound,
                    align: 'right',
                    cellStyle: highlightData
                }
            ]
        });


    };

    function highlightDataHandler(data) {//加载数据处理

        //进行年同比排序
        var temp = data.slice();
        temp.sort(sortBy('yearTotalGrowthRate'));
        yearTotalGrowthRateSort = temp.slice(0, 3).map(function (d) {
            return d.latnName;
        });

        temp = data.slice();
        temp.sort(sortBy('monthGrowthRate'));
        monthGrowthRateSort = temp.slice(0, 3).map(function (d) {
            return d.latnName;
        });

        temp = data.slice();
        temp.sort(sortBy('monthBudgetGap'));
        monthBudgetGapSort = temp.slice(0, 3).map(function (d) {
            return d.latnName;
        });
        return data;
    }

    function highlightData(value, row, index, field) {
        if ((field == 'yearTotalGrowthRate' && $.inArray(row.latnName, yearTotalGrowthRateSort) != -1)
            || (field == 'monthGrowthRate' && $.inArray(row.latnName, monthGrowthRateSort) != -1)
            || (field == 'monthBudgetGap' && $.inArray(row.latnName, monthBudgetGapSort) != -1))
            return {classes: "highlightData"};

        return {};
    }


    //刷新数据
    oTableInit.refresh = function () {
        $table.bootstrapTable('destroy');
        oTableInit.Init();
    };


    return oTableInit;
};

function dataRound(a) {
    return toDecimal(a, 2);
}

function dataPercent(a) {
    return toPercent(a, 2);
}


function toMonth(v) {
    month = $('#datepicker').val().trim();
    return moment(month, 'YYYYMM').add(v, 'months').format('YYYYMM');
}

