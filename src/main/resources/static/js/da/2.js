function initDatePicker() {
    $('#datepicker ').datepicker({
        format: "yyyymm",
        startView: 1,
        minViewMode: 1,
        maxViewMode: 2,
        todayBtn: "linked",
        language: "zh-CN",
        todayHighlight: true,
        toggleActive: true
    });

    m_this = moment().format('YYYYMM');
    $('#datepicker').val(m_this);

}


$.validator.setDefaults({
    errorClass: 'help-block',
    highlight: function (e, a) {
        $(e).closest(".form-group").addClass("has-error");
    },
    success: function (e, a) {
        $(e).closest(".form-group").removeClass("has-error");
        $(e).remove();
    },
    errorPlacement: function (error, element) {
        var $group = $(element).closest(".form-group");
        if ($group.length > 0) {
            var $col = $group.children("div").first();
            if ($col.length > 0 && $col.attr("class").indexOf("col-") >= 0) {
                $col.append(error);
            } else {
                $group.append(error);
            }
        }
    }
});

$('#form_search').validate({
    rules: {
        month: 'required'
    },
    messages: {
        month: '不能为空 '
    },
    submitHandler: function (form) {
        $(form).ajaxSubmit({
            type: "POST",
            url: hostUrl + 'da/productLevel',
            beforeSubmit: function () {
                $('#btn_query').button("loading");
            },
            success: function (r, a, b) {
                $('#btn_query').button("reset");
                if (r == null || r == undefined || r.month == undefined) {
                    toastr.error('该账期无数据');
                } else {
                    initEcharts(r);
                }

            },

        });
    }
});


function initEcharts(r) {
    var legendData = ['移动', '固网'];
    var dataSum = [];
    dataSum.push({value: r.mobile, name: '移动', selected: true})
    dataSum.push({value: r.fixNetwork, name: '固网'})

    var dataProduct = [];


    r.mobileProduct.forEach(function (d, i) {
        legendData.push(d.name)

        var temp = new Object();
        temp.name = d.name;
        temp.value = d.data;
        dataProduct.push(temp);

    });

    r.fixNetworkProduct.forEach(function (d, i) {
        legendData.push(d.name)

        var temp = new Object();
        temp.name = d.name;
        temp.value = d.data;
        dataProduct.push(temp);

    });


    var data = new Object();
    data.legendData = legendData;
    data.dataSum = dataSum;
    data.dataProduct = dataProduct;

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
            tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b}: {c} ({d}%)"
            },
            legend: {
                orient: 'vertical',
                x: 'left',
                data: data.legendData
            },
            series: [
                {
                    name: '汇总',
                    type: 'pie',
                    selectedMode: 'single',
                    radius: [0, '30%'],

                    label: {
                        normal: {
                            position: 'inner'
                        }
                    },
                    labelLine: {
                        normal: {
                            show: false
                        }
                    },
                    data: data.dataSum
                },
                {
                    name: '产品',
                    type: 'pie',
                    radius: ['40%', '55%'],

                    data: data.dataProduct
                }
            ]
        };
        return option;
    },
    initChart: function (dom, option) {
        var myChart = echarts.init(dom);
        myChart.setOption(option);
    }

};
