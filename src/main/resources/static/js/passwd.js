function initForm() {

    //表单字段验证初始化
    var validator = $("#form_passwd").validate({
        rules: {
            username: "required",
            old: "required",
            password: "required",
            //TODO 校验非法字符
            password2: {
                equalTo: '#password'
            }
        },
        messages: {
            username: "用户名不能为空",
            old: "密码不能为空",
            password: "密码不能为空",
            password2: {
                equalTo: '密码不一致'
            }
        },
        submitHandler: function (form) {
            $(form).ajaxSubmit({
                url: hostUrl + 'passwd',
                type: 'post',
                beforeSubmit: function () {
                    $("#btn_passwd").button("loading");
                },
                success: function (r, a, b) {
                    $("#btn_passwd").button("reset");

                    if (r.state) {
                        toastr.info("修改成功,请重新登录");
                        window.location.href = hostUrl + 'logout';
                    } else {
                        toastr.error(r.msg);
                    }
                },
                error: function (a, b, c) {
                    $("#btn_passwd").button("reset");
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
