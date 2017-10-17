var oTable;
var kidTable;
function init() {

    initDatePicker();
    initEvent();

    oTable = new TableInit();
    oTable.Init();

    kidTable = new KidTableInit();
    kidTable.Init();


}

/**
 * 初始化日期
 */
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

/**
 * 初始化事件
 */
function initEvent() {

    $('#btn_query').button("loading");
    $('#btn_query').on('click', function () {
        $('#btn_query').button("loading");

        showPanel(1);
        oTable.refresh()
    });

    $('#btn_export').on('click', function () {

        var dom = document.getElementById("panel2");
        html2canvas(dom, {
            onrendered: function (canvas) {

                var contentWidth = canvas.width;
                var contentHeight = canvas.height;

                //一页pdf显示html页面生成的canvas高度;
                var pageHeight = contentWidth / 592.28 * 841.89;
                //未生成pdf的html页面高度
                var leftHeight = contentHeight;
                //页面偏移
                var position = 0;
                //a4纸的尺寸[595.28,841.89]，html页面生成的canvas在pdf中图片的宽高
                var imgWidth = 595.28;
                var imgHeight = 592.28 / contentWidth * contentHeight;

                var pageData = canvas.toDataURL('image/jpeg', 1.0);

                var pdf = new jsPDF('', 'pt', 'a4');

                //有两个高度需要区分，一个是html页面的实际高度，和生成pdf的页面高度(841.89)
                //当内容未超过pdf一页显示的范围，无需分页
                if (leftHeight < pageHeight) {
                    pdf.addImage(pageData, 'JPEG', 0, 0, imgWidth, imgHeight);
                } else {
                    while (leftHeight > 0) {
                        pdf.addImage(pageData, 'JPEG', 0, position, imgWidth, imgHeight)
                        leftHeight -= pageHeight;
                        position -= 841.89;
                        //避免添加空白页
                        if (leftHeight > 0) {
                            pdf.addPage();
                        }
                    }
                }

                pdf.save('1.pdf');

            }
        })
    });
}

var html2pdf = function (dom, domWidth, domHeight, windowWidth, windowHeight, fileName) {
    var pageNum = Math.ceil(domHeight / windowHeight);
    var a4Width = 592.28;
    var a4Height = 841.89;
    var imgWidth = domWidth / windowWidth * a4Width;
    var imgHeight = domHeight / windowHeight * a4Height;
    var pdf = new jsPDF();
    for (var i = 0; i < pageNum; i++) {
        dom.style.top = (-i * windowHeight) + 'px';
        html2canvas(dom, {
            onrendered: function (canvas) {
                var pageData = canvas.toDataURL('image/jpeg', 1.0);
                pdf.addImage(pageData, 'JPEG', 0, 0, domWidth, domHeight);
                if (i == 1) {
                    pdf.save(fileName)
                } else {
                    pdf.addPage();
                }
            }
        })
    }
    dom.style.top = 0;
}

/**
 * 加载图表
 * @param data
 */
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
                    halign: 'center',
                    cellStyle: {//列属性
                        classes: "latnName"
                    }
                },
                {
                    field: 'lastYearMonth',
                    title: toMonth(-12),
                    formatter: dataRound,
                    halign: 'center',
                    align: 'right'
                },
                {
                    field: 'lastMonth',
                    title: toMonth(-1),
                    formatter: dataRound,
                    halign: 'center',
                    align: 'right'
                },
                {
                    field: 'thisMonth',
                    title: toMonth(0),
                    formatter: dataRound,
                    halign: 'center',
                    align: 'right'
                },
                {
                    field: 'totalThisYear',
                    title: '本年累计',
                    formatter: dataRound,
                    halign: 'center',
                    align: 'right'
                },
                {
                    field: 'totalLastYear',
                    title: '去年累计',
                    formatter: dataRound,
                    halign: 'center',
                    align: 'right'
                },
                {
                    field: 'yearTotalGrowthRate',
                    title: '年累计同比',
                    formatter: dataPercent,
                    halign: 'center',
                    align: 'right',
                    cellStyle: highlightData
                },
                {
                    field: 'monthGrowthRate',
                    title: '月环比',
                    formatter: dataPercent,
                    halign: 'center',
                    align: 'right',
                    cellStyle: highlightData
                },
                {
                    field: 'monthBudget',
                    title: '预算',
                    formatter: dataRound,
                    halign: 'center',
                    align: 'right'
                },
                {
                    field: 'monthBudgetGap',
                    title: '预算缺口',
                    formatter: dataRound,
                    halign: 'center',
                    align: 'right',
                    cellStyle: highlightData
                }
            ]
        });
        //配置事件
        oTableInit.configEvent();
    };
    //加载数据处理
    function highlightDataHandler(data) {

        data.sort(sortBy('yearTotalGrowthRate'));
        data.forEach(function (d, i) {
            d.yearTotalGrowthRateOrder = i + 1;
        });
        data.sort(sortBy('monthGrowthRate'));
        data.forEach(function (d, i) {
            d.monthGrowthRateOrder = i + 1;
        });
        data.sort(sortBy('monthBudgetGap'));
        data.forEach(function (d, i) {
            d.monthBudgetGapOrder = i + 1;
        });

        data.sort(sortBy('orderId'));

        return data;
    }

    function highlightData(value, row, index, field) {
        if ((field == 'yearTotalGrowthRate' && row.yearTotalGrowthRateOrder <= 3)
            || (field == 'monthGrowthRate' && row.monthGrowthRateOrder <= 3)
            || (field == 'monthBudgetGap' && row.monthBudgetGapOrder <= 3))
            return {classes: "highlightData"};

        return {};
    }

    var state = 1;//钻取状态
    oTableInit.configEvent = function () {
        //加载成功
        $table.on('load-success.bs.table', function (a, data) {
            $('#btn_query').button("reset");
            initChart(data);
        });

        //加载失败
        $table.on('load-error.bs.table', function (a, b, c) {
            $('#btn_query').button("reset");
            toastr.error('数据请求失败，请重试');
        });

        //表格点击
        $table.on('click-cell.bs.table', function (element, field, value, row) {

            if (state != 1) {
                toastr.info('正在加载，请稍等...');
                return;
            }
            var month = $('#datepicker').val().trim()
            var latnId = row.latnId;

            state = 0;
            $.post(hostUrl + 'data/da2_2', {month: month, latnId: latnId})
                .done(function (a) {
                    if (a == null || a.length == 0) {
                        toastr.error('无数据');
                        return;
                    }
                    showPanel(2);
                    kidTable.load(month, latnId, a);
                })
                .fail(function () {
                    toastr.error('加载失败，请重试');
                })
                .always(function () {
                    state = 1;
                });

        });
    };

    oTableInit.refresh = function () {
        $table.bootstrapTable('refresh');
    };


    return oTableInit;
};

//钻取Table配置
var KidTableInit = function () {
    var oTableInit = new Object();
    var $table = $('#table2');
    var lvl = 3;//表格层级
    var cache;//缓存子节点数据
    var num = 1;//类似主键自增长，保证每条数据id唯一性

    //初始化Table
    oTableInit.Init = function () {
        $table.bootstrapTable({
            classes: "table-condensed table-hover",
            method: 'post',                      //请求方式（*）
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            sortable: false,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            // height: 700,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            contentType: 'application/x-www-form-urlencoded',
            rowStyle: testRowStyle,
            uniqueId: 'keyId',
            columns: [
                {
                    class: 'table_colum1',
                    field: 'incomeSourceName',
                    title: '收入来源分类',
                    align: 'left',
                    halign: 'center',
                    width: '40%',
                    cellStyle: testCellStyle,
                    formatter: testFormatter,
                    events: testEvents
                },
                {
                    class: 'table_colum2',
                    field: 'lastMonth',
                    title: toMonth(-1),
                    formatter: dataRound,
                    halign: 'center',
                    align: 'right',
                    cellStyle: testCellStyle
                },
                {
                    class: 'table_colum2',
                    field: 'thisMonth',
                    title: toMonth(0),
                    formatter: dataRound,
                    halign: 'center',
                    align: 'right',
                    cellStyle: testCellStyle
                },
                {
                    class: 'table_colum3',
                    field: 'monthGrowthValue',
                    title: '环比增加额',
                    formatter: dataRound,
                    halign: 'center',
                    align: 'right',
                    cellStyle: testCellStyle
                },
                {
                    class: 'table_colum3',
                    field: 'monthGrowthRate',
                    title: '环比增加率',
                    formatter: dataPercent,
                    halign: 'center',
                    align: 'right',
                    cellStyle: testCellStyle
                }
            ]
        });

    };


    function testRowStyle(row, index) {//行样式
        return {
            classes: "small tableRow active"
        };
    }

    var colors1 = gradientColor('#7AB1E7', '#FFFFFF', lvl + 1);
    var colors2 = gradientColor('#7AD6B8', '#FFFFFF', lvl + 1);
    var colors3 = gradientColor('#B5DC62', '#FFFFFF', lvl + 1);

    function testCellStyle(value, row, index, field) {
        var i = row.lvl - 1;
        var color;
        var clas = {};
        if (field == 'incomeSourceName') {
            clas = "latnName";
            color = colors1[i];
        } else if (field == 'lastMonth' || field == 'thisMonth') {
            color = colors2[i];
        } else {
            color = colors3[i];
            clas = highlightData(value, row, index, field);
        }
        return {
            classes: clas,
            css: {
                'background-color': color,
                'border-bottom': '1px solid #ddd'
            }
        }
    }

    function highlightData(value, row, index, field) {
        if (row.lvl == 1
            &&
            (field == 'monthGrowthValue'
            && row.monthGrowthValueOrder <= 3
            && row.monthGrowthValue < 0)
            ||
            (field == 'monthGrowthRate'
            && row.monthGrowthRateOrder <= 3)
            && row.monthGrowthRate < 0) {
            return "highlightData";
        }

        if (row.lvl > 1
            &&
            (field == 'monthGrowthValue'
            && row.monthGrowthValueOrder <= 3
            && row.monthGrowthValue < 0)
            ||
            (field == 'monthGrowthRate'
            && row.monthGrowthRateOrder <= 3
            && row.monthGrowthRate < 0)) {
            return "highlightData";
        }

        return '';
    }

    var state = 1;//处理状态，防止加载过程中多次操作
    var testEvents = {
        'click .table_expand': function (e, value, row, index) {
            if (state != 1) {
                toastr.info('正在处理，请稍等...');
                return;
            }
            state = 0;

            //更改表格数据时，会先触发formatter
            row.open = (row.open + 1) % 2;
            if (row.open == 1) {//如果打开
                open(row, index);
            } else {
                close(row);
            }
            $(this).toggleClass('fa-minus');
            $(this).toggleClass('fa-plus');

            // console.log('testEvents: ' + value + ' - ' + row.open);
        }
    };


    function open(row, index) {

        var data = cache.get(row.keyId);
        if (data != null) {
            data.forEach(function (d) {
                $table.bootstrapTable('showRow', {
                    uniqueId: d.keyId
                });
            });
            state = 1;
            return;
        }
        var url;
        var lvl = row.lvl;
        if (lvl == 1) {
            url = 'data/da2_3';
        } else if (lvl == 2) {
            url = 'data/da2_4';
        }
        $.post(hostUrl + url, {
            month: oTableInit.month,
            latnId: oTableInit.latnId,
            parentId: row.incomeSourceId
        })
            .done(function (data) {
                if (data == null || data.length == 0) {
                    toastr.error('无数据');
                    return;
                }
                lvl++;
                data.forEach(function (d, i) {
                    d.keyId = getId();
                    d.lvl = lvl;
                    d.open = 0;
                    d.orderId = i + 1;
                });
                cache.put(row.keyId, data);

                //只有高亮的父类才会进行子类排序
                if (row.monthGrowthRateOrder <= 3 || row.monthGrowthValueOrder <= 3) {
                    data.sort(sortBy('monthGrowthRate'));
                    data.forEach(function (d, i) {
                        d.monthGrowthRateOrder = i + 1;
                    });
                    data.sort(sortBy('monthGrowthValue'));
                    data.forEach(function (d, i) {
                        d.monthGrowthValueOrder = i + 1;
                    });

                    data.sort(sortBy('orderId'));
                }
                data.forEach(function (d) {
                    $table.bootstrapTable('insertRow', {
                        index: ++index,
                        row: d
                    });
                });
            })
            .fail(function () {
                toastr.error('加载失败，请重试');
            })
            .always(function () {
                state = 1;
            });
    }

    function close(row) {
        var toRemove = [];
        toRemove = mergeKids(row.keyId, toRemove);
        toRemove.forEach(function (d) {
            d.open = 0;
            $table.bootstrapTable('hideRow', {
                uniqueId: d.keyId
            });
        });
        state = 1;
    }

    function mergeKids(keyId, toRemove) {
        var temp = cache.get(keyId);
        if (temp == null) {
            return toRemove;
        }
        toRemove = toRemove.concat(temp);
        temp.forEach(function (d) {
            toRemove = mergeKids(d.keyId, toRemove);
        });
        return toRemove;
    }

    function testFormatter(a, b) {
        var left = b.lvl * 15;
        var style = 'margin-left : ' + left + 'px';

        // console.log('testFormatter: ' + a + ' - ' + b.open);
        if (b.lvl == lvl) {//如果无子节点
            return [
                '<span class="latnName_child" aria-hidden="true" style="' + style + '"> ' + a + '</span>'
            ].join('');
        }
        //根据状态显示图标
        var icon = b.open == 1 ? 'fa-minus' : 'fa-plus';
        return ['<i class="table_expand fa ' + icon + '" aria-hidden="true" style="' + style + '"> ' + a + '</i> '
        ].join('');
    }

    oTableInit.load = function (month, latnId, data) {

        data.forEach(function (d, i) {
            d.keyId = getId();
            d.lvl = 1;
            d.open = 0;
            d.orderId = i + 1;
        });

        data.sort(sortBy('monthGrowthRate'));
        data.forEach(function (d, i) {
            d.monthGrowthRateOrder = i + 1;
        });
        data.sort(sortBy('monthGrowthValue'));
        data.forEach(function (d, i) {
            d.monthGrowthValueOrder = i + 1;
        });

        data.sort(sortBy('orderId'));

        $table.bootstrapTable('load', data);

        cache = new Map();
        oTableInit.month = month;
        oTableInit.latnId = latnId;
    };

    function getId() {
        return ++num;
    }

    return oTableInit;
};

function showPanel(a, b) {
    if (a != 1) {
        $("#panel1").hide();
        $("#panel2").show();
    } else {
        $("#panel1").show();
        $("#panel2").hide();
    }
}

/**
 * 保留小数
 * @param a
 * @returns {*}
 */
function dataRound(a) {
    return toDecimal(a, 2);
}

/**
 * 加上百分号
 * @param a
 * @returns {*}
 */
function dataPercent(a) {
    return toPercent(a, 2);
}


/**
 * 生成账期
 * @param v
 */
function toMonth(v) {
    month = $('#datepicker').val().trim();
    return moment(month, 'YYYYMM').add(v, 'months').format('YYYYMM');
}

