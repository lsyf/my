//---------------------- 适配父窗口方法 --------------------------
function editAlert(txt_title, txt_body, txt_commit, func_commit) {
    window.parent.editAlert(txt_title, txt_body, txt_commit, func_commit);
}
function showAlert() {
    window.parent.showAlert();
}

function hideAlert() {
    window.parent.hideAlert();
}


//---------------------- 适配父窗口变量 --------------------------
if (window.parent.toastr != null) {
    toastr = window.parent.toastr;
}
//-----------------------------------------------

/**
 *  jquery-validator 初始化，兼容bootstrap 样式
 */
function initValidator() {
    $.validator.setDefaults({
        errorPlacement: function (error, element) {
            //如果为自定义单选框组,则在最上层div中添加 转换成block的error
            if($(element).attr('type')=='radio'){
                var $group = $(element).parent().parent();
                error.css('display', 'block');
                error.appendTo($group);
                return ;
            }

            if($(element).attr('type')=='file'){
                var $group = $(element).parents('.file-input').parent();
                error.css('display', 'block');
                error.appendTo($group);
                return ;
            }

            //其他 如input则正常
            var $group = $(element).parent();
            error.appendTo($group);
        },
        highlight: function (e, a) {
            $(e).closest(".form-group").addClass("has-error");
            // $(e).closest(".form-validation").addClass("has-error");
        },
        success: function (e, a) {
            $(e).closest(".form-group").removeClass("has-error");
            // $(e).closest(".form-validation").removeClass("has-error");
            $(e).remove();
        }
    });

    $.validator.addMethod("checkHidden", function (value, element, params) {
        return  value != "";
    }, "不能为空");
}

var validatorForm;
function resetValidator(form) {
    form.find(".form-group.has-error").removeClass("has-error");
    if (validatorForm != null) {
        validatorForm.resetForm();
        // validatorForm.validate().element( this );
    }
}

function titleContentHeader(txt) {
    $("#content-header-h1").text(txt);
}

function loading() {
    $('#div_loader').show();
}
function loadEnd() {
    $('#div_loader').hide();
}

$(function () {
    //点击标题切换到面板1,第二个参数为了兼容字典、组织等界面
    $("#content-header-h1").click(function () {
        showPanel(1,1);
    });
    
});



