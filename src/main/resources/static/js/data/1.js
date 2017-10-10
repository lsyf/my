var oTable;
function init() {


    initEvent();

    oTable = new TableInit();
    oTable.Init();


}

function initChart(data) {
    lastY = data[0];
    thisY = data[1];

    year1 = lastY.year;
    year2 = thisY.year;
    x = [];//x轴
    y1 = [];//y1轴
    y2 = [];//y2轴
    minY = 0;
    maxY = 0;
    for (var i = 1; i < 13; i++) {
        x.push(i + '月');
        y1.push(lastY[i]);
        y2.push(thisY[i]);

        //计算最大值和最小值
        if (i == 1) {//设置初始值
            minY = lastY[i];
            maxY = lastY[i];
        }
        minY = min(minY, lastY[i]);
        minY = min(minY, thisY[i]);
        maxY = max(maxY, lastY[i]);
        maxY = max(maxY, thisY[i]);
    }

    option = {
        color: ['#3398DB'],
        tooltip: {
            trigger: 'axis',
            axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        legend: {
            data: [year1, year2],
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
                min: minY,
                max: maxY
            }
        ],
        series: [
            {
                name: year1,
                type: 'line',
                itemStyle: {
                    normal: {
                        color: '#FF0000'
                    }
                },
                data: y1
            },
            {
                name: year2,
                type: 'line',
                itemStyle: {
                    normal: {
                        color: '#4BACC6'
                    }
                },
                data: y2
            }
        ]
    };

    var myChart = echarts.init(document.getElementById('main'));
    myChart.setOption(option);

}



function initEvent() {

    $('#btn_query').button("loading");
    $('#btn_query').on('click', function () {
        $('#btn_query').button("loading");

        oTable.refresh()
    });

    $('#table').on('load-success.bs.table', function (a, data) {
        $('#btn_query').button("reset");
        initChart(data);

        //合并数据
        // $('#table').bootstrapTable('mergeCells', {
        //     index: 0,
        //     field: 'year',
        //     colspan: 1,
        //     rowspan: 2
        // });

    });
    $('#table').on('load-error.bs.table', function (a, b, c) {
        $('#btn_query').button("reset");
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
            // classes:"table-condensed",
            url: hostUrl + 'data/da1',         //请求后台的URL（*）
            method: 'post',                      //请求方式（*）
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            sortable: false,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            // height: 700,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            contentType: 'application/x-www-form-urlencoded',
            queryParams: function (params) {
                return params;
            },
            responseHandler: function (data) {
                return data;
            },
            rowStyle: function (row, index) {
                console.log(row)
                console.log(index)
                return {classes: "small tableRow"};
            },
            columns: [
                {
                    field: 'year',
                    title: '年份',
                    align: 'center',
                    valign: 'middle',
                    cellStyle: function () {
                        return {
                            css: {"background-color": "#00B1F1"}
                        };
                    }
                }, {
                    field: '1',
                    title: '1月',
                    align: 'center'
                }, {
                    field: '2',
                    title: '2月',
                    align: 'center'
                }, {
                    field: '3',
                    title: '3月',
                    align: 'center'
                }, {
                    field: '4',
                    title: '4月',
                    align: 'center'
                }, {
                    field: '5',
                    title: '5月',
                    align: 'center'
                }, {
                    field: '6',
                    title: '6月',
                    align: 'center'
                }, {
                    field: '7',
                    title: '7月',
                    align: 'center'
                }, {
                    field: '8',
                    title: '8月',
                    align: 'center'
                }, {
                    field: '9',
                    title: '9月',
                    align: 'center'
                }, {
                    field: '10',
                    title: '10月',
                    align: 'center'
                }, {
                    field: '11',
                    title: '11月',
                    align: 'center'
                }, {
                    field: '12',
                    title: '12月',
                    align: 'center'
                }, {
                    field: 'period',
                    title: '同期累计',
                    align: 'center'
                }, {
                    field: "total",
                    title: '总计',
                    align: 'center'
                }
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


function max(a, b) {
    if (b == null) {
        return a;
    }
    return a > b ? a : b;
}

function min(a, b) {
    if (b == null) {
        return a;
    }
    return a < b ? a : b;

}