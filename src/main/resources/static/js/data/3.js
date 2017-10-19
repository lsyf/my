var kidTable;
function init() {
    kidTable = new KidTableInit();

    initDatePicker();
    initEvent();


}

var radio_unit;
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


    radio_unit = createRadioGroup($('#radio_unit'));
    var units = [{text: '元', value: 1}, {text: '万元', value: 10000}];
    radio_unit.init('unit', units, '单位: ');
    radio_unit.val(1);
    radio_unit.click(function (a, b) {
        unit = radio_unit.val();
        $('#table').bootstrapTable('updateByUniqueId', {id: -1});//仅为更新
    });


}

/**
 * 初始化事件
 */
function initEvent() {
    $('#btn_query').on('click', function () {

        var month = $('#datepicker').val().trim();//月份


        //设置标题
        $('#table_title').text(month + ' 数据分析');
        try {
            var param = {month: month};
            $.ajax({
                method: "POST",
                url: hostUrl + '/data/da3',
                data: param,
                beforeSend: function () {
                    $('#btn_query').button("loading");
                }
            }).done(function (r) {
                kidTable.Init(month, 2, r.data);
            }).fail(function () {
                toastr.error('加载失败，请重试');
            }).always(function () {
                $('#btn_query').button('reset');
            });

        } catch (e) {
            console.log(e)
        }

    });
    $('#btn_query').trigger('click');

}


var unit = 1;//单位
//钻取Table配置
var KidTableInit = function () {
    var oTableInit = new Object();
    var $table = $('#table');

    var level;//表格层级
    var kidCache;//缓存子节点数据
    var keyId = 1;//类似主键自增长，保证每条数据id唯一性

    var state = 0;//处理状态，防止加载过程中多次操作
    var month;

    //初始化Table
    oTableInit.Init = function (a, b, data) {
        month = a;
        level = b;
        createColor(level);
        kidCache = new Map();

        var parent = {month: month};
        data = processData(data, 1, true, parent);

        var columns = testColumns();
        $table.bootstrapTable('destroy');
        $table.bootstrapTable({
            classes: "table-condensed table-hover",
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            sortable: false,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            // height: 700,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            contentType: 'application/x-www-form-urlencoded',
            rowStyle: testRowStyle,
            uniqueId: 'keyId',
            columns: columns,
            data: data
        });
    };

    //动态列配置
    function testColumns() {
        var cols = [
            {
                class: 'table_colum1',
                field: 'name',
                title: '地区',
                align: 'left',
                halign: 'center',
                width: '40%',
                cellStyle: testCellStyle,
                formatter: testFormatter,
                events: testEvents
            },
            {
                class: 'table_colum2',
                field: 'lastYearMonth',
                title: toMonth(-12),
                formatter: dataRound,
                halign: 'center',
                align: 'right',
                cellStyle: testCellStyle
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
                class: 'table_colum2',
                field: 'totalLastYear',
                title: '去年累计',
                formatter: dataRound,
                halign: 'center',
                align: 'right',
                cellStyle: testCellStyle
            }, {
                class: 'table_colum2',
                field: 'totalThisYear',
                title: '本年累计',
                formatter: dataRound,
                halign: 'center',
                align: 'right',
                cellStyle: testCellStyle
            },
            {
                class: 'table_colum3',
                field: 'yearTotalGrowthRate',
                title: '年累计同比',
                formatter: dataPercent,
                halign: 'center',
                align: 'right',
                cellStyle: testCellStyle
            },
            {
                class: 'table_colum3',
                field: 'monthGrowthRate',
                title: '月环比',
                formatter: dataPercent,
                halign: 'center',
                align: 'right',
                cellStyle: testCellStyle
            },
            {
                class: 'table_colum4',
                field: 'monthBudget',
                title: '预算',
                formatter: dataRound,
                halign: 'center',
                align: 'right',
                cellStyle: testCellStyle
            },
            {
                class: 'table_colum4',
                field: 'monthBudgetGap',
                title: '预算缺口',
                formatter: dataRound,
                halign: 'center',
                align: 'right',
                cellStyle: testCellStyle
            }
        ];

        return cols;
    }

    function testRowStyle(row, index) {//行样式
        return {
            classes: "small tableRow active"
        };
    }

    var colors1, colors2, colors3, colors4;

    function createColor(lvl) {
        colors1 = gradientColor('#7AB1E7', '#FFFFFF', lvl + 1);
        colors2 = gradientColor('#7AD6B8', '#FFFFFF', lvl + 1);
        colors3 = gradientColor('#B5DC62', '#FFFFFF', lvl + 1);
        colors4 = gradientColor('#E8CF66', '#FFFFFF', lvl + 1);
    }

    function testCellStyle(value, row, index, field) {
        var i = row.lvl - 1;
        var color;
        var clas = {};
        if (field == 'name') {
            clas = "column_name";
            color = colors1[i];
        } else if (field == 'lastMonth'
            || field == 'thisMonth'
            || field == 'lastYearMonth'
            || field == 'totalThisYear'
            || field == 'totalLastYear') {
            color = colors2[i];
        } else if (field == 'yearTotalGrowthRate'
            || field == 'monthGrowthRate') {
            color = colors3[i];
            clas = highlightData(value, row, index, field);
        } else {
            color = colors4[i];
        }

        if (row.type == 'total') {
            clas += ' row_total';
        }

        return {
            classes: clas,
            css: {
                'background-color': color,
                'border-bottom': '1px solid #ddd'
            }
        }
    }

    //高亮数据筛选
    function highlightData(value, row, index, field) {
        if (row.lvl == 1
            &&
            (field == 'yearTotalGrowthRate'
            && row.yearTotalGrowthRateOrder <= 3
            && row.yearTotalGrowthRate < 0)
            ||
            (field == 'monthGrowthRate'
            && row.monthGrowthRateOrder <= 3)
            && row.monthGrowthRate < 0) {
            return "highlightData";
        }

        if (row.lvl > 1
            &&
            (field == 'yearTotalGrowthRate'
            && row.yearTotalGrowthRateOrder <= 3
            && row.yearTotalGrowthRate < 0)
            ||
            (field == 'monthGrowthRate'
            && row.monthGrowthRateOrder <= 3
            && row.monthGrowthRate < 0)) {
            return "highlightData";
        }

        return '';
    }


    //折叠事件监听
    var testEvents = {
        'click .table_expand': function (e, value, row, index) {
            if (row.type == 'total')
                return;
            if (state == 1) {
                toastr.info('正在处理，请稍等...');
                return;
            }
            state = 1;

            try {
                //更改表格数据时，会先触发formatter
                row.open = (row.open + 1) % 2;
                if (row.open == 1) {//如果打开
                    open(row, index);
                } else {
                    close(row);
                    state = 0;
                }
            } catch (e) {
                console.log(e)
                state = 0;
            }
            $(this).toggleClass('fa-minus');
            $(this).toggleClass('fa-plus');


        }
    };

    //显示折叠按钮
    function testFormatter(a, b) {
        var left = b.lvl * 15;
        var style = 'cursor: pointer; margin-left : ' + left + 'px';

        if (b.type == 'total') {//如果为统计数据
            return [
                '<span class="name_total" aria-hidden="true" style="' + style + '"> ' + a + '</span>'
            ].join('');
        }
        if (b.lvl == level) {//如果无子节点
            return [
                '<span class="name_child" aria-hidden="true" style="' + style + '"> ' + a + '</span>'
            ].join('');
        }
        //根据状态显示图标
        var icon = b.open == 1 ? 'fa-minus' : 'fa-plus';
        return ['<i class="table_expand fa ' + icon + '" aria-hidden="true" style="' + style + '"> ' + a + '</i> '
        ].join('');
    }

    // 打开下级
    function open(row, index) {

        //如果有缓存则直接显示
        var data = kidCache.get(row.keyId);
        if (data != null) {
            data.forEach(function (d) {
                $table.bootstrapTable('showRow', {
                    uniqueId: d.keyId
                });
            });
            state = 0;
            return;
        }

        //否则需要请求加载
        var lvl = row.lvl;
        var sortable = false;
        //只有高亮的父类才会进行子类排序
        if (row.yearTotalGrowthRateOrder <= 3 || row.monthGrowthRateOrder <= 3) {
            sortable = true;
        }
        var param = {month: month, latnId: row.id};
        lvl++;

        $.ajax({
            method: "POST",
            url: hostUrl + 'data/da3',
            data: param
        }).done(function (r) {
            var data = r.data;
            data = processData(data, lvl, sortable);
            kidCache.put(row.keyId, data);

            data.forEach(function (d) {
                $table.bootstrapTable('insertRow', {
                    index: ++index,
                    row: d
                });
            });
        }).fail(function () {
            toastr.error('加载失败，请重试');
        }).always(function () {
            state = 0;
        });


    }


    //关闭所有下级
    function close(row) {
        var toRemove = [];
        toRemove = mergeKids(row.keyId, toRemove);
        toRemove.forEach(function (d) {
            d.open = 0;
            $table.bootstrapTable('hideRow', {
                uniqueId: d.keyId
            });
        });
    }

    //获取所有下级id
    function mergeKids(keyId, toRemove) {
        var temp = kidCache.get(keyId);
        if (temp == null) {
            return toRemove;
        }
        toRemove = toRemove.concat(temp);
        temp.forEach(function (d) {
            toRemove = mergeKids(d.keyId, toRemove);
        });
        return toRemove;
    }

    //数据结果处理计算等
    function processData(data, lvl, sortable) {
        if (data == null || data.length == 0) {
            toastr.error('无数据');
            return;
        }

        var total = {
            keyId: getId(),
            name: '合计',
            type: 'total',
            lvl: lvl
        };
        data.forEach(function (d, i) {
            d.keyId = getId();
            d.lvl = lvl;
            d.open = 0;
            d.orderId = i + 1;

            //计算数据
            if (d.totalThisYear != 0 && d.totalLastYear != 0) {
                d.yearTotalGrowthRate = (d.totalThisYear - d.totalLastYear ) / d.totalLastYear;
            }
            if (d.lastMonth != 0 && d.thisMonth != 0) {
                d.monthGrowthRate = (d.thisMonth - d.lastMonth ) / d.lastMonth;
            }

            //合计
            if (i == 0) {
                total.lastYearMonth = d.lastYearMonth;
                total.lastMonth = d.lastMonth;
                total.thisMonth = d.thisMonth;
                total.totalLastYear = d.totalLastYear;
                total.totalThisYear = d.totalThisYear;
                total.monthBudget = d.monthBudget;
                total.monthBudgetGap = d.monthBudgetGap;
            } else {
                total.lastYearMonth += d.lastYearMonth;
                total.lastMonth += d.lastMonth;
                total.thisMonth += d.thisMonth;
                total.totalLastYear += d.totalLastYear;
                total.totalThisYear += d.totalThisYear;
                total.monthBudget += d.monthBudget;
                total.monthBudgetGap += d.monthBudgetGap;
            }

        });
        if (total.totalThisYear != 0 && total.totalLastYear != 0) {
            total.yearTotalGrowthRate = (total.totalThisYear - total.totalLastYear ) / total.totalLastYear;
        }
        if (total.lastMonth != 0 && total.thisMonth != 0) {
            total.monthGrowthRate = (total.thisMonth - total.lastMonth ) / total.lastMonth;
        }


        //判断是否需要排序高亮
        if (sortable) {
            data.sort(sortBy('yearTotalGrowthRate'));
            data.forEach(function (d, i) {
                d.yearTotalGrowthRateOrder = i + 1;
            });
            data.sort(sortBy('monthGrowthRate'));
            data.forEach(function (d, i) {
                d.monthGrowthRateOrder = i + 1;
            });

            data.sort(sortBy('orderId'));
        }
        if (lvl == 1)
            data.push(total);


        return data;

    }


    //主键自增长
    function getId() {
        return ++keyId;
    }

    return oTableInit;
};

/**
 * 数字补0
 * @param num
 * @param fill
 */
function padNumber(num, fill) {
    var len = ('' + num).length;
    return (Array(
        fill > len ? fill - len + 1 || 0 : 0
    ).join(0) + num);
}
/**
 * 保留两位小数
 * @param a
 * @returns {*}
 */
function dataRound(a) {
    a = a / unit;
    return toDecimal(a, 2);
}

/**
 * 加上百分号,保留两位小数
 * @param a
 * @returns {*}
 */
function dataPercent(a) {
    return toPercent(a, 2);
}


/**
 * 根据偏移,生成账期
 * @param v
 */
function toMonth(v) {
    month = $('#datepicker').val().trim();
    return moment(month, 'YYYYMM').add(v, 'months').format('YYYYMM');
}

