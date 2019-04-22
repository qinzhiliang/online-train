// var addr="http://192.168.0.108:8000";
// var addr="http://10.18.43.3:8000";
var addr="http://127.0.0.1:8000";
var getParam = function (pattern) {
    var page = 1;
    var params = location.href.split("&");
    for (var i = 0; i < params.length; i++) {
        var index = params[i].lastIndexOf(pattern);
        if (index != -1) {
            page = params[i].substring(index + pattern.length);
        }
    }
    return page;
}
$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name]) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
}
var logout=function () {
    $.ajax({
        type: "POST",
        url: addr + "/auth/logout",
        async: true,
        crossDomain: true,
        xhrFields: { withCredentials: true },
        success: function (data) {
            location.href="../login.html";
        },
        error:function (data) {
            new $.zui.Messager('提示消息：登出失败', {
                type: 'danger',
                time:3000
            }).show();
        }
    });
}
$(function () {

    $.ajax({
        type: "GET",
        url: addr + "/api/users/current",
        async: true,
        crossDomain: true,
        xhrFields: { withCredentials: true },
        success: function (data) {
            $("#account").text(data.username);
            if(data.role.id=='1'){
                $(".teacherOpt").show();
                $(".studentOpt").hide();
            }else{
                $(".studentOpt").show();
                $(".teacherOpt").hide();
            }
        },
        error:function (data) {
            new $.zui.Messager('提示消息：请先登录', {
                type: 'danger',
                time:3000
            }).show();
            location.href="../login.html";
        }
    });

    $('input[name="allSelected"]').change(function () {
        var checked = this.checked;
        $('input[name="single"]').each(function (index, el) {
            el.checked = checked;
        });
    });

    $('table').delegate('input[name="single"]', 'click', function () {
        var flag = true
        $('input[name="single"]').each(function (index, el) {
            if (!el.checked) {
                flag = false;
                return;
            }
        });
        $('input[name="allSelected"]').get(0)
            .checked = flag;
    });
});