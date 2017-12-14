function initForm() {
    //聚焦输入框
    $("input[name='username']").focus();

    //选中框 配置
    $("input[name='rememberMe']").iCheck({
        checkboxClass: 'icheckbox_square-blue',
        radioClass: 'iradio_square-blue',
        increaseArea: '20%' // optional
    });

    //表单字段验证初始化
    var validator = $("#form_login").validate({
        rules: {
            phone: "required",
            code: "required",
        },
        messages: {
            phone: "手机号码不能为空",
            code: "验证码不能为空",
        },
        submitHandler: function (form) {
            var code = $('#form_code').val().trim();
            if (code != verify_code) {
                toastr.error("验证码不正确");
                return;
            }
            $(form).ajaxSubmit({
                url: hostUrl + 'loginByPhone',
                type: 'post',
                beforeSubmit: function () {
                    $("#btn_login").button("loading");
                },
                success: function (r, a, b) {

                    if (r.state) {
                        window.location.href = r.data;
                    } else {
                        toastr.error(r.msg);
                    }
                    $("#btn_login").button("reset");
                },
                error: function (a, b, c) {
                    $("#btn_login").button("reset");
                    toastr.error("发送请求失败");
                }
            });

        }
    });


}
var verify_code = null;
function sendCode(btn) {
    var $btn = $(btn);
    var phone = $('#form_phone').val().trim();
    if (!(/^1[3|4|5|7|8][0-9]\d{4,8}$/.test(phone))) {
        toastr.error("手机号码格式不正确")
        return
    }
    $.ajax({
        url: hostUrl + 'sendPhoneCode',
        type: 'post',
        data: {phone: phone},
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        beforeSubmit: function () {
            $btn.button("loading");
        },
        success: function (r, a, b) {
            toastr.info("发送成功");
            verify_code = r.data;
            forbid($btn);
        },
        error: function (a, b, c) {
            $btn.button("reset");
            toastr.error("发送请求失败");
        }
    });

}

function forbid($btn) {
    $btn.enable(false)
    var time = 60;
    var timer = setInterval(fun1, 1000);  //设置定时器
    function fun1() {
        time--;
        if (time >= 0) {
            $btn.text(time + "s后重新发送");
        } else {
            $btn.text("发送验证码");
            $btn.enable();
            clearTimeout(timer);  //清除定时器
            time = 60;  //设置循环重新开始条件
        }
    }
}

/**
 *  jquery-validator 初始化，兼容bootstrap 样式
 */
function initValidator() {
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
}
