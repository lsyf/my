/**
 * 保留两位小数
 * 功能：将浮点数四舍五入，取小数点后2位
 */
function toDecimal(x, n) {
    var f = parseFloat(x);
    if (isNaN(f)) {
        return;
    }
    var temp = Math.pow(10,n);
    f = Math.round(x * temp) / temp;
    return f;
}