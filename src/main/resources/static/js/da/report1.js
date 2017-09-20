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
    m_last = moment().add(-1, 'y').format('YYYYMM');
    $('#datepicker').find("input[name='start']").val(m_last);
    $('#datepicker').find("input[name='end']").val(m_this);

}

//添加时间校验
$.validator.addMethod("dateCheck", function (v, e) {
    var start = $('#datepicker').find("input[name='start']").val();
    var end = $('#datepicker').find("input[name='end']").val();
    if (start == '' || end == '') {
        return false;
    } else {
        return true;
    }

}, "时间选择错误");

$('#form_search').validate({
    rules: {
        end: 'dateCheck'
    },
    errorPlacement: function (error, element) {
        error.css('color', 'red');
        $('#form_search').append(error);
    },
    submitHandler: function (form) {
        $(form).ajaxSubmit({
            type: "POST",
            url: hostUrl + 'da0/provinceMFBg',
            beforeSubmit: function () {
                $('#btn_query').button("loading");
            },
            success: function (r, a, b) {
                $('#btn_query').button("reset");
                if (r == null || r == undefined || r.length == 0) {
                    toastr.error('该账期无数据');
                } else {
                    initEcharts(r);
                }

            }
        });
    }
});

function initEcharts(r) {
    var xAxisData = [];
    var data1 = [];
    var data2 = [];
    var data3 = [];

    r.forEach(function (d, i) {
        xAxisData.push(d.month != undefined ? d.month : '无数据');
        data1.push(d.mobile != undefined ? d.mobile : 0);
        data2.push(d.fixNetwork != undefined ? d.fixNetwork : 0);
        data3.push(d.budgetGap != undefined ? d.budgetGap : 0);
    });


    var data = new Object();
    data.xAxisData = xAxisData;
    data.data1 = data1;
    data.data2 = data2;
    data.data3 = data3;
    option = drillDown.getOption(data);


    var dom = document.getElementById('main');
    echarts.dispose(dom);

    drillDown.initChart(dom, option);

    // $('#return-button').on('click', function () {
    //     $('#form_search').ajaxForm();
    // });
}


var drillDown = {
    getOption: function (data) {
        option = {
            backgroundColor: '#eee',
            legend: {
                data: ['移动', '固网', '总的预算缺口'],
                align: 'left',
                left: 10
            },
            brush: {
                toolbox: ['rect', 'polygon', 'lineX', 'lineY', 'keep', 'clear'],
                xAxisIndex: 0
            },
            toolbox: {
                feature: {
                    magicType: {
                        type: ['stack', 'tiled']
                    },
                    dataView: {}
                }
            },
            tooltip: {},
            xAxis: {
                data: data.xAxisData,
                name: 'X Axis',
                silent: false,
                axisLine: {
                    onZero: true,
                },
                axisLabel: {
                    interval: 0,//横轴信息全部显示
                    rotate: 60//60度角倾斜显示
                },
                splitLine: {show: false},
                splitArea: {show: false}
            },
            yAxis: {
                inverse: false,
                interval: 1,
                splitArea: {show: false}
            },
            grid: {
                left: 100
            },
            visualMap: {
                type: 'continuous',
                dimension: 1,
                text: ['High', 'Low'],
                inverse: true,
                itemHeight: 200,
                calculable: true,
                min: -2,
                max: 6,
                top: 60,
                left: 10,
                inRange: {
                    colorLightness: [0.4, 0.8]
                },
                outOfRange: {
                    color: '#bbb'
                },
                controller: {
                    inRange: {
                        color: '#2f4554'
                    }
                }
            },
            series: [
                {
                    name: '移动',
                    type: 'bar',
                    stack: 'one',
                    barMaxWidth: '50%',
                    itemStyle: this.toItemStyle('#3C8DA3'),
                    label: this.toLabel(),
                    data: data.data1
                },
                {
                    name: '固网',
                    type: 'bar',
                    stack: 'one',
                    barMaxWidth: '50%',
                    itemStyle: this.toItemStyle('#4BACC6'),
                    label: this.toLabel(),
                    data: data.data2
                },
                {
                    name: '总的预算缺口',
                    type: 'bar',
                    stack: 'one',
                    barMaxWidth: '50%',
                    barMinHeight: '10',//防止高度过低无法放入数值
                    itemStyle: this.toItemStyle('#FF0000'),
                    label: this.toLabel(),
                    data: data.data3
                }
            ]
        };
        return option;
    },
    initChart: function (dom, option) {
        var myChart = echarts.init(dom);
        myChart.setOption(option);
        myChart.on('click', function (object) {
            // 销毁之前的echarts实例
            echarts.dispose(dom);
            // 初始化一个新的实例
            var myChart = echarts.init(dom);

            option.series[1] = null;
            option.series[2] = null;

            // 我这里就模拟一个测试数据，做为demo演示
            option.xAxis.data = [
                '2016-09-01', '2016-09-02', '2016-09-03', '2016-09-04', '2016-09-05', '2016-09-06', '2016-09-07', '2016-09-08',
                '2016-09-09', '2016-09-10', '2016-09-11', '2016-09-12', '2016-09-13', '2016-09-14', '2016-09-15', '2016-09-16',
                '2016-09-17', '2016-09-18', '2016-09-19', '2016-09-20', '2016-09-21', '2016-09-22', '2016-09-23', '2016-09-24',
                '2016-09-25', '2016-09-26', '2016-09-27', '2016-09-28', '2016-09-29', '2016-09-30'
            ];
            option.series[0].data = [
                3, 4, 5, 6, 5, 6, 7, 8, 8, 9,
                12, 13, 15, 16, 20, 12, 30, 21, 22, 29,
                30, 31, 33, 34, 35, 36, 20, 29, 33, 40
            ];
            myChart.setOption(option, true);
        });

    },
    toItemStyle: function (color) {
        var itemStyle = {
            normal: {
                color: color
            },
            emphasis: {
                barBorderWidth: 1,
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowOffsetY: 0,
                shadowColor: 'rgba(0,0,0,0.5)'
            }
        };
        return itemStyle;
    },
    toLabel: function () {
        var label = {
            normal: {
                show: true,
                // formatter: function (data) {
                //     // console.log(data);
                //     return toDecimal(data.value, 2);
                // },
                textStyle: {
                    color: '#000'
                }
            }
        };
        return label;
    }

};
