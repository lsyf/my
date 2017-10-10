toastr.options = {
    "closeButton": false,
    "debug": false,
    "newestOnTop": false,
    "progressBar": true,
    "positionClass": "toast-bottom-center",
    "preventDuplicates": false,
    "onclick": null,
    "showDuration": "300",
    "hideDuration": "1000",
    "timeOut": "5000",
    "extendedTimeOut": "1000",
    "showEasing": "swing",
    "hideEasing": "linear",
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
};


//适用于IE6-9
var isIE = function (ver) {
    var b = document.createElement('b')
    b.innerHTML = '<!--[if IE ' + ver + ']><i></i><![endif]-->'
    return b.getElementsByTagName('i').length === 1
}

/**
 * 保留两位小数
 * 功能：将浮点数四舍五入，取小数点后n位
 */
function toDecimal(x, n) {
    var f = parseFloat(x);
    if (isNaN(f)) {
        return;
    }

    //默认保留2位小数
    n = n == null ? 2 : n;

    var temp = Math.pow(10, n);
    f = Math.round(x * temp) / temp;
    return f.toFixed(n);
}

/**
 * 转换百分制
 */
function toPercent(x, n) {
    var temp = toDecimal(x * 100, n);
    if (temp != null) {
        return temp + '%';
    }
}


/**
 * 判断纵横方向是否scroll
 * @param el
 * @returns {{scrollX: boolean, scrollY: boolean}}
 */
var isScroll = function (el) {
    // test targets
    var elems = el ? [el] : [document.documentElement, document.body];
    var scrollX = false, scrollY = false;
    for (var i = 0; i < elems.length; i++) {
        var o = elems[i];
        // test horizontal
        var sl = o.scrollLeft;
        o.scrollLeft += (sl > 0) ? -1 : 1;
        o.scrollLeft !== sl && (scrollX = scrollX || true);
        o.scrollLeft = sl;
        // test vertical
        var st = o.scrollTop;
        o.scrollTop += (st > 0) ? -1 : 1;
        o.scrollTop !== st && (scrollY = scrollY || true);
        o.scrollTop = st;
    }
    // ret
    return {
        scrollX: scrollX,
        scrollY: scrollY
    };
};


//创建 单选组
var createRadioGroup = function (div) {
    var group = new Object();
    group.init = function (name, values) {
        group.name = name;
        group.values = values;
        //首先清空div
        div.empty();

        values.forEach(function (v) {
            var label = $('\
                        <label class="radio-inline">  \
                             <input type="radio" name="' + name + '" value="' + v.value + '">\
                             ' + v.text + '\
                        </label>');
            div.append(label);
        });

        group.reset();
    };
    group.val = function (value) {

        if (arguments.length == 0) {
            var v = div.find("input[type='radio'][name='" + group.name + "']:checked").val();
            return v;
        } else {
            div.find("input[type='radio'][name='" + group.name + "'][value=" + value + "]").prop("checked", true);
        }
    };
    group.reset = function () {
        div.find("input[type='radio']").prop('checked', false)
        div.find("input[type='radio']:first").prop('checked', true)
    };

    group.click = function (click) {
        div.find("input[type='radio']").click(click);
    };
    return group;
}




