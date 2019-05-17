<!DOCTYPE html>
<html lang="en">
<head>
    <#include "../inc/resource.ftl">
    <title>域名解析设置</title>
    <style type="text/css">
        #form-add select
        {
            width: 60px;
            height: 30px;
            text-align: center;
            padding-left: 10px;
            margin-right: 4px;
        }
        .alert-warning { margin-bottom: 0px !important; }
        .alert ol { margin-bottom: 0px !important; }
    </style>
</head>
<body>

<#include "../inc/header.ftl">
<div class="container-fluid content">
    <div class="row">
        <#include "../inc/sidebar.ftl">
        <div class="main">
            <div class="row">
                <div class="col-lg-12">
                    <h3 class="page-header"><i class="fa fa-laptop"></i> Dashboard</h3>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <div class="alert alert-warning">
                                <ol>
                                    <li>以下对于解析规则的添加、修改、启用/禁用、删除等操作将立即生效。</li>
                                    <li>解析规则的匹配优先级按照添加的顺序而定，先添加先匹配，如果有包含关系的解析规则（如example.com和test.example.com），请注意添加的次序。</li>
                                </ol>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            域名解析设置
                            <button class="btn btn-primary pull-right" id="btn-add"><i class="fa fa-plus"></i> 添加新条目</button>
                        </div>
                        <div class="panel-body">
                            <div id="rule-table"></div>
                            <ul class="pagination"></ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<#include "../inc/footer.ftl">
<script type="text/html" id="form-add-html">
    <div class="alert alert-info">
        请设置域名所指向的IP，设定将在保存后即刻生效，可使用<strong>nslookup</strong>进行查询验证。
    </div>
    <form method="post" class="form-horizontal" id="form-add">
        <div class="row">
            <div class="col-md-8">
                <div class="form-group">
                    <label class="col-md-3 control-label" for="text-input">来源IP段：</label>
                    <div class="col-md-9">
                        <div class="col-md-5">
                            <input type="text" id="ipFrom" name="ipFrom" class="form-control" placeholder="开始地址，包含">
                        </div>
                        <div class="col-md-1 text-center"><p>至</p></div>
                        <div class="col-md-5">
                            <input type="text" id="ipTo" name="ipTo" class="form-control" placeholder="结束地址，包含">
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-3 control-label" for="text-input">请求时间段：</label>
                    <div class="col-md-9">
                        <div class="col-md-12" id="time-range">
                            <input type="hidden" name="timeFrom" id="timeFrom" />
                            <input type="hidden" name="timeTo" id="timeTo" />
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-3 control-label" for="text-input">匹配模式：</label>
                    <div class="col-md-9">
                        <div class="radio"><label><input type="radio" name="matchMode" id="matchMode" value="prefix" /> 前缀匹配，如www.匹配www.sina.com.cn、www.163.com等</label></div>
                        <div class="radio"><label><input type="radio" name="matchMode" id="matchMode" value="suffix" /> 后缀匹配，如.baidu.com匹配www.baidu.com、tieba.baidu.com等</label></div>
                        <div class="radio"><label><input type="radio" name="matchMode" id="matchMode" value="equals" /> 完全匹配，必须与查询的域名一致</label></div>
                        <div class="radio"><label><input type="radio" name="matchMode" id="matchMode" value="contains" checked /> 包含，只要解析的域名中包含指定的域名即可</label></div>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-3 control-label" for="text-input">匹配域名：</label>
                    <div class="col-md-9">
                        <input type="text" id="name" name="name" class="form-control" placeholder="如.baidu.com" />
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-3 control-label" for="text-input">应答IP分发模式：</label>
                    <div class="col-md-9">
                        <div class="radio"><label><input type="radio" name="dispatchMode" id="dispatchMode" value="round-robin" checked /> 轮循，依次应答</label></div>
                        <div class="radio"><label><input type="radio" name="dispatchMode" id="dispatchMode" value="iphash" /> IP Hash，与请求来源IP绑定</label></div>
                        <div class="radio"><label><input type="radio" name="dispatchMode" id="dispatchMode" value="random" /> 随机</label></div>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <textarea rows="8" class="form-control" name="addresses" id="addresses" placeholder="请输入应答IP，一行一个地址"></textarea>
            </div>
        </div>
    </form>
</script>
<script type="text/javascript">
    $(document).ready(function()
    {
        $('#btn-add').click(function()
        {
            modal({
                title : '添加新转发',
                html : $('#form-add-html').html(),
                width : 1200,
                close : true,
                ok : function(dialog)
                {
                    $.post('${context}/manage/rule/create', $('#form-add').serialize(), function(result)
                    {
                        if (result.error.code != 0) return alert(result.error.reason);
                        $('.modal').modal('hide');
                        $('#rule-table').paginate('reload');
                    });
                    return false;
                }
            });

            setTimeout(setupTimePicker, 0);
        });

        $(document).on('change', '#form-add select', function()
        {
            var slt = $(this);
            var id = slt.attr('id');
            var sltHour = $('#' + id.substring(0, 2) + 'hour');
            var sltMinute = $('#' + id.substring(0, 2) + 'minute');

            var h = sltHour.val(), m = sltMinute.val();
            h = h.length == 1 ? '0' + h : h;
            m = m.length == 1 ? '0' + m : m;

            var xx = id.indexOf('tf') > -1 ? $('#timeFrom') : $('#timeTo');
            xx.val(h + ':' + m + ':00');
        });

        $('#rule-table').paginate({
            url : '${context}/manage/rule/json',
            paginate : $('.pagination'),
            fields : [
                {
                    title : '#',
                    name : 'id',
                    align : 'center',
                },
                {
                    title : 'IP地址段',
                    name : 'id',
                    formatter : function(i, v, r)
                    {
                        var f = r.ipFrom;
                        var t = r.ipTo;
                        if (f == null || t == null) return '--';
                        f = ((f >> 24) & 0xff) + '.' + ((f >> 16) & 0xff) + '.' + ((f >> 8) & 0xff) + '.' + ((f >> 0) & 0xff);
                        t = ((t >> 24) & 0xff) + '.' + ((t >> 16) & 0xff) + '.' + ((t >> 8) & 0xff) + '.' + ((t >> 0) & 0xff);
                        return f + ' - ' + t;
                    }
                },
                {
                    title : '时间段',
                    name : 'id',
                    formatter : function(i, v, r)
                    {
                        var f = r.timeFrom;
                        var t = r.timeTo;
                        if (f == null || t == null) return '--';
                        f = ("000000000" + f).replace(/^0+(\d{2})(\d{2})(\d{2})$/gi, '$1:$2:$3');
                        t = ("000000000" + t).replace(/^0+(\d{2})(\d{2})(\d{2})$/gi, '$1:$2:$3');
                        return f + ' - ' + t;
                    }
                },
                {
                    title : '域名',
                    name : 'name',
                    align : 'left',
                },
                {
                    title : '域名匹配模式',
                    name : 'matchMode',
                    align : 'center',
                    formatter : function(i, v, r)
                    {
                        return v == 'prefix' ? '前缀匹配' : v == 'suffix' ? '后缀匹配' : v == 'equals' ? '完全匹配' : '包含';
                    }
                },
                {
                    title : 'IP分发模式',
                    name : 'dispatchMode',
                    align : 'center',
                    formatter : function(i, v, r)
                    {
                        return v == 'round-robin' ? '轮循' : v == 'iphash' ? 'IP Hash' : '随机';
                    }
                },
                {
                    title : '启用',
                    name : 'enabled',
                    align : 'center',
                    formatter : function(i, v, r)
                    {
                        var shtml = '';
                        shtml += '<label class="switch switch-primary">';
                        shtml += '  <input type="checkbox" x-rule-id="' + r.id + '" class="switch-input" ' + (v ? 'checked' : '') + '>';
                        shtml += '  <span class="switch-label" data-on="启用" data-off="停用"></span>';
                        shtml += '  <span class="switch-handle"></span>';
                        shtml += '</label>';
                        return shtml;
                    }
                },
                {
                    title : '操作',
                    name : 'id',
                    align : 'center',
                    formatter : function(i, v, r)
                    {
                        var shtml = '';
                        shtml += '<div class="btn-group" x-rule-id="' + v + '">';
                        shtml += '  <a href="javascript:edit(' + v + ');" class="btn btn-primary">修改</a>';
                        shtml += '  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">';
                        shtml += '      <span class="caret"></span>';
                        shtml += '      <span class="sr-only">Toggle Dropdown</span>';
                        shtml += '  </button>';
                        shtml += '  <ul class="dropdown-menu" role="menu">';
                        shtml += '      <li><a href="javascript:;" x-action="remove">删除</a></li>';
                        shtml += '  </ul>';
                        shtml += '</div>';
                        return shtml;
                    }
                }
            ]
        });

        $(document).on('click', '.dropdown-menu li a', function()
        {
            var action = $(this).attr('x-action');
            var id = $(this).parents('.btn-group').attr('x-rule-id');
            if (window[action] && typeof(window[action]) == 'function') window[action](id);
        });

        $(document).on('change', '.switch-input', function()
        {
            var checkbox = $(this);
            setEnabled(checkbox.attr('x-rule-id'), checkbox.is(':checked'));
        });
    });

    function setupTimePicker(from, to)
    {
        var fh = typeof(from) == 'undefined' ? null : parseInt(from / 10000);
        var fm = typeof(from) == 'undefined' ? null : (from / 100) % 100;
        var th = typeof(to) == 'undefined' ? null : parseInt(to / 10000);
        var tm = typeof(to) == 'undefined' ? null : (to / 100) % 100;

        // 时间段选择
        var s1 = '', s2 = '';
        var ss1 = '', ss2 = '';
        s1 = '<select id="tfhour"><option value="">时</option>', s2 = '<select id="tfminute"><option value="">分</option>';
        ss1 = '<select id="tthour"><option value="">时</option>', ss2 = '<select id="ttminute"><option value="">分</option>';
        for (var i = 0; i < 24; i++)
        {
            s1 += '<option ' + (i == fh ? 'selected' : '') + '>' + i + '</option>';
            ss1 += '<option ' + (i == th ? 'selected' : '') + '>' + i + '</option>';
        }
        for (var i = 0; i < 60; i++)
        {
            s2 += '<option ' + (i == fm ? 'selected' : '') + '>' + i + '</option>';
            ss2 += '<option ' + (i == tm ? 'selected' : '') + '>' + i + '</option>';
        }
        s1 += '</select>';
        s2 += '</select>';
        ss1 += '</select>';
        ss2 += '</select>';
        $('#time-range').append(s1 + ':' + s2 + ' 至 ' + ss1 + ':' + ss2);
    }

    function setEnabled(ruleId, enabled)
    {
        $.post('${context}/manage/rule/setEnable', { ruleId : ruleId, enabled : enabled }, function(result)
        {
            if (result.error && result.error.code) return alert(result.error.reason);
        });
    }

    function edit(ruleId)
    {
        $.post('./query', { ruleId : ruleId }, function(result)
        {
            if (result.error && result.error.code) return alert(result.error.reason);
            modal({
                title : '修改解析规则',
                html : $('#form-add-html').html(),
                width : 1200,
                close : true,
                ok : function(dialog)
                {
                    $.post('${context}/manage/rule/update', $('#form-add').serialize() + '&ruleId=' + ruleId, function(result)
                    {
                        if (result.error.code != 0) return alert(result.error.reason);
                        $('.modal').modal('hide');
                        $('#rule-table').paginate('reload');
                    });
                    return false;
                }
            });

            setTimeout(function()
            {
                setupTimePicker(result.data.timeFrom, result.data.timeTo);
                $('#ipFrom').val(result.data.fromIP || '');
                $('#ipTo').val(result.data.toIP || '');
                $('input[name=matchMode]').each(function()
                {
                    var radiobox = $(this);
                    if (radiobox.val() == result.data.matchMode)
                    {
                        radiobox.attr('checked', 'checked');
                        return false;
                    }
                });
                $('#name').val(result.data.name);
                $('input[name=dispatchMode]').each(function()
                {
                    var radiobox = $(this);
                    if (radiobox.val() == result.data.dispatchMode)
                    {
                        radiobox.attr('checked', 'checked');
                        return false;
                    }
                });
                var ipList = [];
                for (var i = 0; result.data.addresses && i < result.data.addresses.length; i++) ipList.push(result.data.addresses[i].address);
                $('#addresses').val(ipList.join('\n'));
            }, 0);
        });
    }

    function remove(ruleId)
    {
        if (!confirm('真的要删除此域名解析规则吗？')) return;
        $.post('${context}/manage/rule/remove', { ruleId : ruleId }, function(result)
        {
            if (result.error.code) alert(result.error.reason);
            else alert("操作成功");
            $('#rule-table').paginate('reload');
        });
    }

</script>
</body>
</html>