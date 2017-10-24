var table_area, table_sale, table_bill;
function init() {
    initDatePicker();

    table_area = new KidTableInit();
    table_area.init($('#table_area'), $('#title_area'), 'area');
    table_sale = new KidTableInit();
    table_sale.init($('#table_sale'), $('#title_sale'), 'sale');
    table_bill = new KidTableInit();
    table_bill.init($('#table_bill'), $('#title_bill'), 'bill');

    // 初始化table为
    activeTable = table_area;

    initEvent();


}

var activeTable;
function showPanel(i) {
    $('#tabs').find('a:eq(' + (i - 1) + ')').trigger('click');
    switch (i) {
        case '1':
            activeTable = table_area;
            break;
        case '2':
            activeTable = table_sale;
            break;
        case '3':
            activeTable = table_bill;
            break;
    }
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
    radio_unit.init('unit', units);
    radio_unit.val(1);
    radio_unit.click(function (a, b) {
        unit = radio_unit.val();
        table_area.table.bootstrapTable('updateByUniqueId', {id: -1});//仅为更新
        table_bill.table.bootstrapTable('updateByUniqueId', {id: -1});//仅为更新
        table_sale.table.bootstrapTable('updateByUniqueId', {id: -1});//仅为更新
    });


}

/**
 * 初始化事件
 */
function initEvent() {
    $('#btn_query').on('click', function () {

        var mode = $('#select_mode').val();//模式
        var month = $('#datepicker').val().trim();//月份

        try {
            var param = {month: month};
            var url = getUrl(mode, 1);
            showPanel(mode);
            $.ajax({
                method: "POST",
                url: hostUrl + url,
                data: param,
                beforeSend: function () {
                    $('#btn_query').button("loading");
                }
            }).done(function (r) {
                activeTable.load(r.data, param);
            }).fail(function () {
                toastr.error('加载失败，请重试');
            }).always(function () {
                $('#btn_query').button('reset');

                //调整表头对齐
                setTimeout(function () {
                    activeTable.table.bootstrapTable("resetView");
                }, 100);
            });

        } catch (e) {
            console.log(e)
        }

    });

    $('#btn_query').trigger('click');


}

var modes = ['area', 'sale', 'bill'];
function getUrl(mode, lvl) {
    var flag = lvl > 1;

    var url;
    switch (modes[mode - 1]) {
        case 'area':
            if (flag) url = 'data/da4_listLatn2';
            else url = 'data/da4_listLatn1';
            break;
        case 'sale':
            if (flag) url = 'data/da4_listDiscount2';
            else url = 'data/da4_listDiscount1';
            break;
        case 'bill':
            url = 'data/da4_listBill';
            break;
    }
    return url;
}


var unit = 1;//单位
//钻取Table配置
var KidTableInit = function () {
    var oTableInit = new Object();
    var $table;
    var $title;

    var type;//类型
    var otherTypes;
    var otherTypeNames = [];

    var area, sale, bill;
    var latnId, discountId, billId;

    var month;//月份


    //初始化Table
    oTableInit.init = function (table, title, mode) {
        oTableInit.table = table;
        $table = table;
        $title = title;
        type = mode;
        otherTypes = modes.slice();
        otherTypes.splice(modes.indexOf(mode), 1);
        otherTypes.forEach(function (a) {
            otherTypeNames.push(getNameByType(a));
        });

        var columns = testColumns(mode);

        $table.bootstrapTable('destroy');
        $table.bootstrapTable({
            classes: "table-condensed table-hover",
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            sortable: false,                     //是否启用排序
            resizable: true,
            sortOrder: "asc",                   //排序方式
            height: 600,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            pagination: true,
            sidePagination: "client",           //分页方式：client客户端分页，server服务端分页（*）
            pageNumber: 1,                       //初始化加载第一页，默认第一页
            pageSize: 20,                       //每页的记录行数（*）
            pageList: [20, 50, 100],        //可供选择的每页的行数（*）
            search: true,                       //是否显示表格搜索
            contentType: 'application/x-www-form-urlencoded',
            rowStyle: testRowStyle,
            columns: columns,
            data: []
        });
    };
    oTableInit.refresh = function () {
        oTableInit.init($table, $title, type);
    };
    oTableInit.load = function (data, param) {

        if (data == null || data.length == 0) {
            toastr.error('无数据');
            return;
        }

        title(param);
        oTableInit.init($table, $title, type);

        data = processData(data, param);
        $table.bootstrapTable('load', data);
    };

    function title(param) {
        month = param.month;
        latnId = param.latnId;
        discountId = param.discountId;
        billId = param.billId;

        area = param.area == null ? '' : '[' + param.area + ']';
        sale = param.sale == null ? '' : '[' + param.sale + ']';
        bill = param.bill == null ? '' : '[' + param.bill + ']';
        var txt;
        switch (type) {
            case 'area':
                area = area == '' ? '所有地区' : area;
                txt = month + ' ' + sale + ' ' + bill + ' ' + area;
                break;
            case 'sale':
                sale = sale == '' ? '所有销售品' : area;
                txt = month + ' ' + area + ' ' + bill + ' ' + sale;
                break;
            case 'bill':
                bill = bill == '' ? '所有账目' : area;
                txt = month + ' ' + area + ' ' + sale + ' ' + bill;
                break;
        }
        $title.text(txt);

    }


    //动态列标题配置
    function testColumns(mode) {
        var title = getNameByType(mode);
        var cols = [
            {
                class: 'table_colum1',
                field: 'name',
                title: title,
                align: 'center',
                halign: 'center',
                // width: '30%',
                cellStyle: testCellStyle
            },
            {
                class: 'table_colum2',
                field: 'amount1',
                title: toMonth(month, -2) + ' 金额',
                formatter: dataRound,
                halign: 'center',
                align: 'right',
                cellStyle: testCellStyle
            },
            {
                class: 'table_colum2',
                field: 'count1',
                title: toMonth(month, -2) + ' 用户数',
                halign: 'center',
                align: 'right',
                cellStyle: testCellStyle
            },
            {
                class: 'table_colum3',
                field: 'amount2',
                title: toMonth(month, -1) + ' 金额',
                formatter: dataRound,
                halign: 'center',
                align: 'right',
                cellStyle: testCellStyle
            },
            {
                class: 'table_colum3',
                field: 'count2',
                title: toMonth(month, -1) + ' 用户数',
                halign: 'center',
                align: 'right',
                cellStyle: testCellStyle
            },
            {
                class: 'table_colum4',
                field: 'amount3',
                title: toMonth(month, 0) + ' 金额',
                formatter: dataRound,
                halign: 'center',
                align: 'right',
                cellStyle: testCellStyle
            },
            {
                class: 'table_colum4',
                field: 'count3',
                title: toMonth(month, 0) + ' 用户数',
                halign: 'center',
                align: 'right',
                cellStyle: testCellStyle
            },
            {
                class: 'table_colum4',
                title: '查看详细',
                align: 'center',
                halign: 'center',
                cellStyle: testCellStyle,
                formatter: testFormatter,
                events: testEvents
            }
        ];


        return cols;
    }

    function showable(mode) {
        var flag = false;
        switch (mode) {
            case 'area':
                flag = latnId == null;
                break;
            case 'sale':
                flag = discountId == null;
                break;
            case 'bill':
                flag = billId == null;
                break;
        }
        return flag;
    }

    //操作显示format
    function testFormatter(value, row, index) {
        var e1 = '<button type="button" class="other1 btn btn-primary btn-xs">' + otherTypeNames[0] + '</button> ';
        var e2 = '<button type="button" class="other2 btn btn-primary btn-xs">' + otherTypeNames[1] + '</button> ';
        var e;
        if (!showable(otherTypes[0])) {
            e1 = '';
        }
        if (!showable(otherTypes[1])) {
            e2 = '';
        }
        e = e1 + e2;
        return [e].join('');
    }

    var state = 0;
    var testEvents = {
        'click .other1': function (e, value, row, index) {
            if (row.type == 'total')
                return;
            if (state == 1) {
                toastr.info('正在处理，请稍等...');
                return;
            }
            state = 1;

            try {
                change(row, 1);
            } catch (e) {
                console.log(e)
                state = 0;
            }
            state = 0;

        },
        'click .other2': function (e, value, row, index) {
            if (row.type == 'total')
                return;
            if (state == 1) {
                toastr.info('正在处理，请稍等...');
                return;
            }
            state = 1;

            try {
                change(row, 2);
            } catch (e) {
                console.log(e)
                state = 0;
            }

        }
    };

    function change(row, i) {
        var mode = otherTypes[i - 1];
        var i = modes.indexOf(mode) + 1;
        var url = getUrl(i, 2);

        var param = getParam(row);
        showPanel(i + '');
        $.ajax({
            method: "POST",
            url: hostUrl + url,
            data: param,
            beforeSend: function () {
                $('#btn_query').button("loading");
            }
        }).done(function (r) {
            activeTable.load(r.data, param);
        }).fail(function () {
            toastr.error('加载失败，请重试');
        }).always(function () {
            $('#btn_query').button('reset');
            state = 0;

            //调整表头对齐
            setTimeout(function () {
                activeTable.table.bootstrapTable("resetView");
            }, 100);
        });
    }

    //行样式
    function testRowStyle(row, index) {//行样式
        return {
            classes: "small tableRow active"
        };
    }


    //表格样式
    var colors = ['#7AB1E7', '#7AD6B8', '#B5DC62', '#E8CF66'];

    function testCellStyle(value, row, index, field) {
        var color;
        var clas;
        if (field == 'name') {
            clas = "column_name";
            color = colors[0];
        } else if (field == 'amount1' || field == 'count1') {
            color = colors[1];
        } else if (field == 'amount2' || field == 'count2') {
            color = colors[2];
        } else {
            color = colors[3];
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

    //高亮数据
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


        return '';
    }


    function getParam(row) {
        var param = new Object();
        param.month = row.month;
        param.latnId = row.latnId;
        param.discountId = row.discountId;
        param.billId = row.billId;

        param.area = row.area;
        param.sale = row.sale;
        param.bill = row.bill;

        switch (type) {
            case 'area':
                param.latnId = row.id;
                param.area = row.name;
                break;
            case 'sale':
                param.discountId = row.id;
                param.sale = row.name;
                break;
            case 'bill':
                param.billId = row.id;
                param.bill = row.name;
                break;
        }
        return param;
    }


    //数据结果处理计算等
    function processData(data, param) {


        var total = {
            name: '合计',
            type: 'total'
        };
        data.forEach(function (d, i) {
            d.orderId = i;

            //计算数据
            var thisMonth = moment(month, 'YYYYMM');
            var m2 = thisMonth.format('MM');
            if (m2 != '01') {
                var m1 = thisMonth.add(-1, 'M').format('MM');
                var _last = d['a' + m1];
                var _this = d['a' + m2];
                d.monthGrowthValue = _this - _last;
                if (_last != null && _last != 0 && _this != 0) {
                    d.monthGrowthRate = d.monthGrowthValue / _last;
                }
            }

            //通过参数增加属性  来源、地区、产品
            d.month = param.month;
            d.latnId = param.latnId;
            d.discountId = param.discountId;
            d.billId = param.billId;
            d.area = param.area;
            d.sale = param.sale;
            d.bill = param.bill;

            //合计
            //首次赋值
            if (i == 0) {
                total.amount1 = d.amount1;
                total.amount2 = d.amount2;
                total.amount3 = d.amount3;
                total.count1 = d.count1;
                total.count2 = d.count2;
                total.count3 = d.count3;

            }
            //分别计算
            total.amount1 += d.amount1;
            total.amount2 += d.amount2;
            total.amount3 += d.amount3;
            total.count1 += d.count1;
            total.count2 += d.count2;
            total.count3 += d.count3;

        });


        //排序(为了之后高亮)
        data.sort(sortBy('monthGrowthRate'));
        data.forEach(function (d, i) {
            d.monthGrowthRateOrder = i + 1;
        });
        data.sort(sortBy('monthGrowthValue'));
        data.forEach(function (d, i) {
            d.monthGrowthValueOrder = i + 1;
        });

        data.sort(sortBy('orderId'));

        data.push(total);

        return data;

    }

    function getNameByType(type) {
        switch (type) {
            case 'area':
                return '地区';
            case 'sale':
                return '销售品';
            case 'bill':
                return '账目';
        }

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
function toMonth(month, v) {
    if (month == null) {
        month = $('#datepicker').val().trim();
    }
    return moment(month, 'YYYYMM').add(v, 'months').format('YYYYMM');
}

