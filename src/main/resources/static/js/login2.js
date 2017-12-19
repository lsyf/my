function initForm() {
    // 聚焦输入框
    $("input[name='username']").focus();

    // 选中框 配置
    $("input[name='rememberMe']").iCheck({
        checkboxClass: 'icheckbox_square-blue',
        radioClass: 'iradio_square-blue',
        increaseArea: '20%' // optional
    });

    // 表单字段验证初始化
    var validator = $("#form_login").validate({
        rules: {
            username: "required",
            code: "required",
        },
        messages: {
            username: "账号不能为空",
            code: "验证码不能为空",
        },
        submitHandler: function (form) {

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
 
function sendCode(btn) {
    var $btn = $(btn);
    var username = $('#form_username').val().trim();
    if (username == "") {
        toastr.error("账户不能为空")
        return


    }
    $btn.button("loading");
    $.ajax({
        url: hostUrl + 'sendPhoneCode',
        type: 'post',
        data: {
            username: username
        },
        success: function (r, a, b) {
            if (r.state) {
                toastr.info("发送成功");
                forbid($btn);
            } else {
                toastr.info("发送失败:" + r.msg);
                $btn.button("reset");
            }
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
    var timer = setInterval(fun1, 1000); // 设置定时器
    function fun1() {
        time--;
        if (time >= 0) {
            $btn.text(time + "s后重新发送");
        } else {
            $btn.text("发送验证码");
            $btn.enable();
            clearTimeout(timer); // 清除定时器
            time = 60; // 设置循环重新开始条件
        }
    }
}

/**
 * jquery-validator 初始化，兼容bootstrap 样式
 */
function initValidator() {
    $.validator
        .setDefaults({
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
                    if ($col.length > 0
                        && $col.attr("class").indexOf("col-") >= 0) {
                        $col.append(error);
                    } else {
                        $group.append(error);
                    }
                }
            }
        });
}
