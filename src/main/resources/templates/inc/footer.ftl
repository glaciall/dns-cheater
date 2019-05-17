<!-- start: JavaScript-->
<!--[if !IE]>-->

<script src="${web_resource}/proton/js/jquery-2.1.1.min.js"></script>

<!--<![endif]-->

<!--[if IE]>

<script src="${web_resource}/proton/js/jquery-1.11.1.min.js"></script>

<![endif]-->

<!--[if !IE]>-->

<script type="text/javascript">
    window.jQuery || document.write("<script src='${web_resource}/proton/js/jquery-2.1.1.min.js'>"+"<"+"/script>");
</script>

<!--<![endif]-->

<!--[if IE]>

<script type="text/javascript">
window.jQuery || document.write("<script src='${web_resource}/proton/js/jquery-1.11.1.min.js'>"+"<"+"/script>");
</script>

<![endif]-->
<script src="${web_resource}/proton/js/jquery-migrate-1.2.1.min.js"></script>
<script src="${web_resource}/proton/js/bootstrap.min.js"></script>


<!-- page scripts -->
<script src="${web_resource}/proton/plugins/jquery-ui/js/jquery-ui-1.10.4.min.js"></script>
<script src="${web_resource}/proton/plugins/touchpunch/jquery.ui.touch-punch.min.js"></script>
<script src="${web_resource}/proton/plugins/moment/moment.min.js"></script>
<script src="${web_resource}/proton/plugins/fullcalendar/js/fullcalendar.min.js"></script>
<!--[if lte IE 8]>
<script language="javascript" type="text/javascript" src="${web_resource}/proton/plugins/excanvas/excanvas.min.js"></script>
<![endif]-->
<script src="${web_resource}/proton/plugins/flot/jquery.flot.min.js"></script>
<script src="${web_resource}/proton/plugins/flot/jquery.flot.pie.min.js"></script>
<script src="${web_resource}/proton/plugins/flot/jquery.flot.stack.min.js"></script>
<script src="${web_resource}/proton/plugins/flot/jquery.flot.resize.min.js"></script>
<script src="${web_resource}/proton/plugins/flot/jquery.flot.time.min.js"></script>
<script src="${web_resource}/proton/plugins/flot/jquery.flot.spline.min.js"></script>
<script src="${web_resource}/proton/plugins/xcharts/js/xcharts.min.js"></script>
<script src="${web_resource}/proton/plugins/autosize/jquery.autosize.min.js"></script>
<script src="${web_resource}/proton/plugins/placeholder/jquery.placeholder.min.js"></script>
<script src="${web_resource}/proton/plugins/datatables/js/jquery.dataTables.min.js"></script>
<script src="${web_resource}/proton/plugins/datatables/js/dataTables.bootstrap.min.js"></script>
<script src="${web_resource}/proton/plugins/raphael/raphael.min.js"></script>
<script src="${web_resource}/proton/plugins/morris/js/morris.min.js"></script>
<script src="${web_resource}/proton/plugins/jvectormap/js/jquery-jvectormap-1.2.2.min.js"></script>
<script src="${web_resource}/proton/plugins/jvectormap/js/jquery-jvectormap-world-mill-en.js"></script>
<script src="${web_resource}/proton/plugins/jvectormap/js/gdp-data.js"></script>
<script src="${web_resource}/proton/plugins/gauge/gauge.min.js"></script>


<!-- theme scripts -->
<script src="${web_resource}/proton/js/SmoothScroll.js"></script>
<script src="${web_resource}/proton/js/jquery.mmenu.min.js"></script>
<script src="${web_resource}/proton/js/core.min.js"></script>
<script src="${web_resource}/proton/plugins/d3/d3.min.js"></script>
<script type="text/javascript">
    (function($)
    {
        $.fn.paginate = function(param)
        {
            var paginate = this;
            if (typeof(param) == 'string' && param == 'reload')
            {
                this.paginate(this.get(0).parameters);
                return;
            }
            var concatParam = function(param1, param2)
            {
                if (param1 == '') return param2;
                if (param1.charAt(param1.length - 1) == '&') return param1 + param2;
                else return param1 + '&' + param2;
            }
            param = $.extend({
                form : null,
                pageIndex : 1,
                pageIndexName : 'pageIndex',
                pagination : $('.pagination'),
                loading : '<div class="text-center"><img vspace="50" hspace="10" src="${web_resource}/proton/img/loading.gif" />正在载入，请稍候...</div>',
            }, param);

            if (param.form && typeof(this.get(0).parameters) == 'undefined') param.form.submit(function()
            {
                console.log('bind form submit event');
                paginate.paginate(paginate.get(0).parameters);
                return false;
            });

            this.parameters = param;
            var container = $(this);
            container.get(0).parameters = param;
            var urlParameters = param.form;
            if (param.form && param.form.serialize) urlParameters = param.form.serialize();
            else urlParameters = '';
            urlParameters = concatParam(urlParameters, param.pageIndexName + '=' + param.pageIndex);

            if (param.loading) container.html(param.loading);
            if (param.pagination && param.pagination.html) param.pagination.html('');
            $.post(param.url, urlParameters, function(result)
            {
                if (result.error.code)
                {
                    if (param.error) param.error(result, param.url, urlParameters);
                    else console.log(result, param.url, urlParameters);
                    return;
                }
                var shtml = '<table class="table table-bordered table-striped table-condensed table-hover"><thead><tr>';
                for (var i = 0; i < param.fields.length; i++)
                {
                    var field = param.fields[i];
                    field.align = field.align == null ? 'left' : field.align;
                    shtml += '<th ' + (field.width == null ? '' : 'width="' + field.width + '"') + ' class="text-' + field.align + '">' + field.title + '</th>';
                }
                shtml += '</tr></thead>';

                shtml += '<tbody>';
                for (var i = 0; result.data.list && i < result.data.list.length; i++)
                {
                    var row = result.data.list[i];
                    shtml += '<tr>';
                    for (var k = 0; k < param.fields.length; k++)
                    {
                        var field = param.fields[k];
                        var content = row[field.name];
                        if (typeof(field.formatter) == 'function') content = field.formatter(i, content, row);
                        shtml += '<td valign="middle" align="' + field.align + '">' + content + '</td>';
                    }
                    shtml += '</tr>';
                }

                if (result.data.result)
                {
                    var airTd = result.data.pageSize - result.data.result.length;
                    for (var i = 0; result.data.result && i < airTd; i++)
                    {
                        var row = result.data.result[i];

                        shtml += '<tr>';
                        for (var k = 0; k < param.fields.length; k++)
                        {
                            var field = param.fields[k];
                            if(field.title == '序号')
                                shtml += '<td valign="middle" align="' + field.align + '">' + (result.data.result.length + i + 1) + '</td>';
                            else
                                shtml += '<td valign="middle" align="' + field.align + '"></td>';
                        }
                        shtml += '</tr>';
                    }
                }

                shtml += '</tbody></table>';
                container.html(shtml);

                // 生成分页
                shtml = '<li><a href="javascript:;" x-page="' + Math.max(1, result.data.pageIndex - 1) + '">&lt;</a></li>';
                for (var i = Math.max(1, result.data.pageIndex - 5), k = 0; i <= Math.min(result.data.pageIndex + 5, result.data.pageCount); i++)
                {
                    shtml += '<li class="' + (i == result.data.pageIndex ? 'active' : '') + '"><a x-page="' + i + '" href="javascript:;">' + i + '</a></li>';
                }
                shtml += '<li><a href="javascript:;" x-page="' + Math.min(result.data.pageIndex + 1, result.data.pageCount) + '">&gt;</a></li>';
                param.pagination.html(shtml);

                if (param.load) param.load();

                setTimeout(function()
                {
                    param.pagination.find('a').each(function()
                    {
                        $(this).click(function()
                        {
                            paginate.parameters.pageIndex = $(this).attr('x-page');
                            container.paginate(paginate.parameters);
                        });
                    });
                }, 0);
            });
        }
    })(jQuery);
</script>
<script type="text/javascript">
    Date.prototype.format = function(fmt)
    {
        var o = {
            "M+": this.getMonth() + 1, //月份
            "d+": this.getDate(), //日
            "h+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }

    function addEvent(selector, event, eventFun){
        selector.unbind(event);
        selector.bind(event,eventFun);
    }
</script>
<script type="text/javascript">
    function modal(options)
    {
        options = $.extend({
            title : '',
            text : '',
            html : '',
            close : false,
            width : 600,
            ok : null
        }, options);
        var shtml = '';
        shtml += '<div class="modal fade">';
        shtml += '  <div class="modal-dialog" style="width: ' + options.width + 'px;">';
        shtml += '      <div class="modal-content">';
        shtml += '          <div class="modal-header">';
        if (options.close)
        shtml += '              <button type="button" class="close" data-dismiss="modal">&times;</button>';
        if (options.title)
        shtml += '              <h4 class="modal-title">' + options.title + '</h4>';
        shtml += '          </div>';
        shtml += '          <div class="modal-body">';
        if (options.text)
            shtml += '              <p>' + options.text + '</p>';
        if (options.html)
            shtml += '              ' + options.html;
        shtml += '          </div>';
        shtml += '          <div class="modal-footer">';
        if (options.close)
            shtml += '              <button type="button" class="btn btn-default" id="btn-close" data-dismiss="modal">关闭</button>';
        if (options.ok)
            shtml += '              <button type="button" class="btn btn-primary" id="btn-ok">确定</button>';
        shtml += '          </div>';
        shtml += '      </div>';
        shtml += '  </div>';
        shtml += '</div>';
        $(document.body).append(shtml);
        var dialog = $('.modal');
        dialog.modal('show');
        hideDialog = function()
        {
            dialog.modal('hide');
            setTimeout(function()
            {
                dialog.remove();
            }, 1000);
        }
        dialog.find('#btn-ok').click(function()
        {
            if (options.ok(dialog) === false) return;
            hideDialog();
        });
        dialog.on('hidden.bs.modal', function()
        {
            dialog.remove();
        });
        if (options.timeout) setTimeout(function()
        {
            hideDialog();
        }, options.timeout);
    }

    $.fn.extend({
        animateCss: function (animationName, callback) {
            var animationEnd = 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend';
            this.addClass('animated ' + animationName).one(animationEnd, function() {
                $(this).removeClass('animated ' + animationName);
                if (callback) {
                    callback();
                }
            });
            return this;
        }
    });
    function greeting(text, timeout)
    {
        timeout = timeout || 4000;
        var greeting = $('<div class="greeting">' + text + '</div>');
        $(document.body).append(greeting);
        greeting.show().animateCss('bounceIn', function()
        {
            setTimeout(function()
            {
                greeting.remove();
            }, timeout);
        });
    }

</script>
<script type="text/javascript">
    $(document).ready(function()
    {
        $('#btn-changepwd').click(function()
        {
            var form = '';
            form += '<form action="javascript:;" method="post" class="form-horizontal">';
            form += '   <div class="form-group">';
            form += '       <label class="col-md-3 control-label" for="text-input">旧密码：</label>';
            form += '       <div class="col-md-9">';
            form += '           <input type="password" id="oldPwd" name="oldPwd" class="form-control" >';
            form += '       </div>';
            form += '   </div>';

            form += '   <div class="form-group">';
            form += '       <label class="col-md-3 control-label" for="text-input">新密码：</label>';
            form += '       <div class="col-md-9">';
            form += '           <input type="password" id="password" name="password" class="form-control" >';
            form += '       </div>';
            form += '   </div>';

            form += '   <div class="form-group">';
            form += '       <label class="col-md-3 control-label" for="text-input">再次确认：</label>';
            form += '       <div class="col-md-9">';
            form += '           <input type="password" id="password2" name="password2" class="form-control" placeholder="再次输入新的登陆密码" >';
            form += '       </div>';
            form += '   </div>';

            form += '</form>';
            modal({
                title : '修改登陆密码',
                html : form,
                close : true,
                width : 480,
                ok : function(dialog)
                {
                    $.post(rootPath + '/manage/user/passwd/reset', dialog.find('form').serialize(), function(result)
                    {
                        if (result.error.code) return alert(result.error.reason);
                        alert('修改成功');
                        $('.modal').modal('hide');
                    });
                    return false;
                }
            });
        });
    });
</script>
<!-- end: JavaScript-->
<script type="text/javascript">
    $(document).ready(function()
    {
        $('.nav-sidebar li a').each(function()
        {
            var link = $(this);
            var url = link.attr('href');
            if (location.pathname.indexOf(url) > -1)
            {
                link.parent().addClass('active');
                return false;
            }
        });
    });
</script>