var orgTree;
function initIncomeStatistics() {


    buildSelect('upload_month', months);
    orgTree = new ZtreeSelect("treeOrg", "menuContent", "upload_latnId");
    orgTree.Init(orgs);

    initForm();
}

function initForm() {
    initValidator();

    validatorForm = $('#form_upload').validate({
        rules: {
            latnId: 'checkHidden'
        },
        messages: {
            latnId: "必须选择本地网"
        },
        ignore: "",
        submitHandler: function (form) {
            $(form).ajaxSubmit({
                url: hostUrl + "incomeStatistics/exec",
                type: 'post',
                timeout: 1800000,
                beforeSubmit: function () {
                    $('#btn_exec').button("loading");
                    log('clear');
                    log('开始汇总...');
                    time(1)
                },
                success: function (r) {
                    $('#btn_exec').button("reset");
                    if (r.state) {
                        log('执行成功!');
                    } else {
                        log('执行失败: ' + r.msg);
                    }
                },
                error: function (r) {
                    $('#btn_exec').button("reset");
                    log('请求失败: ' + r);
                },
                complete: function (req, status) {
                    time(0);
                    i = 0;
                    if (status == 'timeout') {
                        log('请求超时!')
                    }
                }
            });
        }
    });


    $('#form_upload').on("change", "input[type=text], input[name], select", function () {
        $('#form_upload').validate().element(this);
    });

}

var i = 0;
var s = 1;
function time(t) {
    if (t && s) {
        var txt = '已执行 ' + i + '秒';
        if (i == 0) {
            log(txt, 'div_time')
        } else {
            $('#div_time').text(txt);
        }
        i++;

        setTimeout('time(' + s + ')', 1000);
    }
    s = t;

}
function log(txt, id) {
    if (txt == 'clear') {
        $('#div_console').empty();
        return
    }
    var id = id ? 'id="' + id + '"' : '';
    var $p = $('<p ' + id + '>' + txt + '</p>');
    $('#div_console').append($p);
}

