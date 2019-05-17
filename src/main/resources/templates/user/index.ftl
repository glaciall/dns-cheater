<!DOCTYPE html>
<html lang="en">
<head>
    <#include "../inc/resource.ftl">
    <title>账号管理</title>
    <style type="text/css">
        #form-add select
        {
            width: 60px;
            height: 30px;
            text-align: center;
            padding-left: 10px;
            margin-right: 4px;
        }
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
                        <div class="panel-heading">
                            系统账号管理
                            <button class="btn btn-primary pull-right" id="btn-add"><i class="fa fa-plus"></i> 创建新账号</button>
                        </div>
                        <div class="panel-body">
                            <div id="user-table"></div>
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
    <form method="post" class="form-horizontal" id="form-add">
        <div class="row">
            <div class="col-md-12">
                <div class="form-group">
                    <label class="col-md-3 control-label" for="text-input">账号名称：</label>
                    <div class="col-md-9">
                        <input type="text" id="name" name="name" class="form-control" placeholder="">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-3 control-label" for="text-input">登陆密码：</label>
                    <div class="col-md-9">
                        <input type="password" id="password" name="password" class="form-control" placeholder="6~16个字符">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-3 control-label" for="text-input">确认密码：</label>
                    <div class="col-md-9">
                        <input type="password" id="password2" name="password2" class="form-control" placeholder="请再次输入密码">
                    </div>
                </div>
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
                title : '添加新账号',
                html : $('#form-add-html').html(),
                width : 500,
                close : true,
                ok : function(dialog)
                {
                    $.post('${context}/manage/user/create', $('#form-add').serialize(), function(result)
                    {
                        if (result.error.code != 0) return alert(result.error.reason);
                        $('.modal').modal('hide');
                        $('#user-table').paginate('reload');
                    });
                    return false;
                }
            });

            setTimeout(function()
            {
                // 时间段选择
                var s1 = '', s2 = '';
                s1 = '<select id="tfhour"><option value="">时</option>', s2 = '<select id="tfminute"><option value="">分</option>';
                for (var i = 0; i < 24; i++) s1 += '<option>' + i + '</option>';
                for (var i = 0; i < 60; i++) s2 += '<option>' + i + '</option>';
                s1 += '</select>';
                s2 += '</select>';
                $('#time-range').append(s1 + ':' + s2 + ' 至 ' + s1.replace(/tf(hour|minute)/gi, 'tt$1') + s2.replace(/tf(hour|minute)/gi, 'tt$1'));
            }, 0);
        });

        $('#user-table').paginate({
            url : '${context}/manage/user/json',
            paginate : $('.pagination'),
            fields : [
                {
                    title : '#',
                    name : 'id',
                    align : 'center',
                },
                {
                    title : '用户名',
                    name : 'name',
                },
                {
                    title : '类型',
                    name : 'type',
                    align : 'center',
                    formatter : function(i, v, r)
                    {
                        return v == 'sa' ? '超级管理员' : '管理员';
                    }
                },
                {
                    title : '最后登陆时间',
                    name : 'lastLoginTime',
                    align : 'center',
                    formatter : function(i, v, r)
                    {
                        return v > 0 ? new Date(v).toLocaleString() : '--';
                    }
                },
                {
                    title : '最后登陆IP',
                    name : 'lastLoginIP',
                    align : 'center',
                    formatter : function(i, v, r)
                    {
                        return v || '--';
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
                        shtml += '  <input type="checkbox" x-user-id="' + r.id + '" class="switch-input" ' + (v ? 'checked' : '') + '>';
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
                        shtml += '<div class="btn-group" x-user-id="' + v + '">';
                        shtml += '  <a href="javascript:resetPassword(' + v + ');" class="btn btn-primary">重置密码</a>';
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
            var id = $(this).parents('.btn-group').attr('x-user-id');
            if (window[action] && typeof(window[action]) == 'function') window[action](id);
        });

        $(document).on('change', '.switch-input', function()
        {
            var checkbox = $(this);
            setEnabled(checkbox.attr('x-user-id'), checkbox.is(':checked'));
        });
    });

    function setEnabled(userId, enabled)
    {
        $.post('${context}/manage/user/setEnable', { userId : userId, enabled : enabled }, function(result)
        {
            if (result.error && result.error.code) return alert(result.error.reason);
        });
    }

    function resetPassword(userId)
    {
        $.post('${context}/manage/user/resetPassword', { userId : userId }, function(result)
        {
            if (result.error && result.error.code) return alert(result.error.reason);
            alert('新的登陆密码已经重置为：' + result.data);
        });
    }

    function remove(userId)
    {
        if (!confirm('真的要删除此管理员账号吗？')) return;
        $.post('${context}/manage/user/remove', { userId : userId }, function(result)
        {
            if (result.error.code) alert(result.error.reason);
            else alert("操作成功");
            $('#user-table').paginate('reload');
        });
    }

</script>
</body>
</html>