var kidTable;
function init() {
    kidTable = new KidTableInit();

    initDatePicker();
    initEvent();


}

var check_detail;
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

    //多选框列表
    check_detail = createCheckboxGroup($('#check_detail'));
    var details = [{text: '地市', value: 1}, {text: '来源', value: 2}];
    check_detail.init('detail', details);
    check_detail.val([1, 2]);

    radio_unit = createRadioGroup($('#radio_unit'));
    var units = [{text: '元', value: 1}, {text: '万元', value: 10000}];
    radio_unit.init('unit', units, '单位: ');
    radio_unit.val(1);
    radio_unit.click(function (a, b) {
        unit = radio_unit.val();
        $('#table').bootstrapTable('updateByUniqueId', {id: -1});//仅为更新
    });

    //模式下拉框加载
    $.post(hostUrl + "dictionary/get", {name: "dataAnalysisMode1"})
        .done(function (r) {
            if (r.state) {
                var data = r.data;

                var $select = $('#select_mode');
                $select.empty();
                data.forEach(function (d) {
                    var option = '<option value="' + d.data + '">' + d.name + '</option>';
                    $select.append(option);
                });

                //加载完成后触发加载数据
                $('#btn_query').trigger('click');

            } else {
                toastr.error('请求模式信息失败，请重试');
                toastr.error(r.msg);
            }
        })
        .fail(function () {
            toastr.error('发送请求失败');
        });
}

/**
 * 初始化事件
 */
function initEvent() {
    $('#btn_query').on('click', function () {

        var detail = check_detail.val();//是否详细
        var title = $('#select_mode').find("option:selected").text();//name
        var mode = $('#select_mode').val();//模式
        var month = $('#datepicker').val().trim();//月份

        var modes = transMode(mode, detail);

        //设置标题
        $('#table_title').text(month + ' ' + title + ' 数据分析');
        try {
            var param = {month: month};
            var url = getUrl(modes, 1);
            $.ajax({
                method: "POST",
                url: hostUrl + url,
                data: param,
                beforeSend: function () {
                    $('#btn_query').button("loading");
                }
            }).done(function (r) {
                kidTable.Init(title, month, detail, mode, modes, r.data);
            }).fail(function () {
                toastr.error('加载失败，请重试');
            }).always(function () {
                $('#btn_query').button('reset');
            });

        } catch (e) {
            console.log(e)
        }

    });

}


function transMode(mode, detail) {
    var detail_area = $.inArray('1', detail) != -1;
    var detail_source = $.inArray('2', detail) != -1;

    //拆分成 area  pd  source
    var modes = mode.split('_');
    if (detail_area) {
        var i = modes.indexOf('area');
        modes.splice(i + 1, 0, 'area2');
    }
    if (detail_source) {
        var i = modes.indexOf('source');
        modes.splice(i + 1, 0, 'source2');
    }
    return modes;
}

function getUrl(modes, lvl) {
    //根据modes和lvl确定接口
    var type = modes[lvl - 1];

    var url;
    switch (type) {
        case 'area':
        case 'area2':
            url = 'data/da2_listLatn';
            break;
        case 'source':
        case 'source2':
            url = 'data/da2_listIncomeSource';
            break;
        case 'pd':
            url = 'data/da2_listProduct';
            break;
    }
    return url;
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
    var month, detail, mode;//月份，详细状态，钻取模式，防止筛选条件更改影响加载数据
    var modes;//分类顺序数组

    //初始化Table
    oTableInit.Init = function (title, a, b, c, d, data) {
        month = a;
        detail = b;
        mode = c;
        modes = d
        level = modes.length;
        createColor(level);
        kidCache = new Map();

        var parent = {month: month};
        data = processData(data, 1, true, parent);

        var columns = testColumns(title);
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
    function testColumns(title) {
        title = title == null ? '分类' : title;
        var cols = [
            {
                class: 'table_colum1',
                field: 'name',
                title: title,
                align: 'left',
                halign: 'center',
                width: '40%',
                cellStyle: testCellStyle,
                formatter: testFormatter,
                events: testEvents
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
        ];
        var m = moment(month, 'YYYYMM').month();//获取月份下标
        for (var i = 0; i <= m; i++) {
            var temp = moment(month, 'YYYYMM').add(-i, 'M')
            var col = {
                class: 'table_colum2',
                field: 'a' + (temp.format('MM')),
                title: temp.format('YYYYMM'),
                formatter: dataRound,
                halign: 'center',
                align: 'right',
                cellStyle: testCellStyle
            };
            cols.splice(1, 0, col);
        }

        return cols;
    }

    function testRowStyle(row, index) {//行样式
        return {
            classes: "small tableRow active"
        };
    }

    var colors1, colors2, colors3;

    function createColor(lvl) {
        colors1 = gradientColor('#7AB1E7', '#FFFFFF', lvl);
        colors2 = gradientColor('#7AD6B8', '#FFFFFF', lvl);
        colors3 = gradientColor('#B5DC62', '#FFFFFF', lvl);
    }

    function testCellStyle(value, row, index, field) {
        var i = row.lvl - 1;
        var color;
        var clas = {};
        if (field == 'name') {
            clas = "column_name";
            color = colors1[i];
        } else if (field.indexOf('a') == 0) {
            color = colors2[i];
        } else {
            color = colors3[i];
            clas = highlightData(value, row, index, field);
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
        if (row.monthGrowthRateOrder <= 3 || row.monthGrowthValueOrder <= 3) {
            sortable = true;
        }
        var parent = getParam(row, lvl);
        lvl++;

        var url = getUrl(modes, lvl);
        $.ajax({
            method: "POST",
            url: hostUrl + url,
            data: parent
        }).done(function (r) {
            var data = r.data;
            data = processData(data, lvl, sortable, parent);
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

    function getParam(row, lvl) {
        var parent = new Object();
        parent.month = row.month;
        parent.latnId = row.latnId;
        parent.latnId2 = row.latnId2;
        parent.productId = row.productId;
        parent.sourceId = row.sourceId;
        parent.sourceId2 = row.sourceId2;

        var type = modes[lvl - 1];
        switch (type) {
            case 'area':
                parent.latnId = row.id;
                break;
            case 'area2':
                parent.latnId2 = row.id;
                break;
            case 'source':
                parent.sourceId = row.id;
                break;
            case 'source2':
                parent.sourceId2 = row.id;
                break;
            case 'pd':
                parent.productId = row.id;
                break;
        }
        return parent;
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
    function processData(data, lvl, sortable, parent) {
        if (data == null || data.length == 0) {
            toastr.error('无数据');
            return;
        }

        var total = {
                keyId: getId(),
                name: '合计',
                type: 'total',
                lvl: lvl
            },
            total1 = {
                keyId: getId(),
                name: '移动合计',
                type: 'total',
                lvl: lvl
            },
            total2 = {
                keyId: getId(),
                name: '固网合计',
                type: 'total',
                lvl: lvl
            };
        var totals = [total, total1, total2];
        var offset1 = 0,
            offset2 = 0;
        data.forEach(function (d, i) {
            d.keyId = getId();
            d.lvl = lvl;
            d.open = 0;
            d.orderId = i + 1;

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

            //通过父类增加属性  来源、地区、产品
            d.month = parent.month;
            d.latnId = parent.latnId;
            d.latnId2 = parent.latnId2;
            d.productId = parent.productId;
            d.sourceId = parent.sourceId;
            d.sourceId2 = parent.sourceId2;

            //合计
            for (var x = 1; x <= 12; x++) {
                var feild = 'a' + padNumber(x, 2);
                //首次赋值
                if (i == 0) {
                    total[feild] = 0;
                    total1[feild] = 0;
                    total2[feild] = 0;
                }
                //分别计算
                total[feild] += d[feild];
                if (d.type == '1') {
                    total1[feild] += d[feild];
                } else if (d.type == '2') {
                    total2[feild] += d[feild];
                    if (offset2 == 0) {
                        offset2 = i;
                    }
                }
            }
        });

        totals.forEach(function (d, i) {
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
        });


        //判断是否需要排序高亮
        if (sortable) {
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


        if (data[0].type == '1' || data[0].type == '2') {
            data.splice(offset1, 0, total1);
            data.splice(offset2 + 1, 0, total2);
        }
        if (lvl == 1) {
            data.push(total);
        }

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

