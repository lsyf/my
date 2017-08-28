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
            username: "required",
            password: "required"
        },
        messages: {
            username: "用户名不能为空",
            password: "密码不能为空"
        },
        submitHandler: function (form) {
            $(form).ajaxSubmit({
                url: hostUrl + 'login',
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
