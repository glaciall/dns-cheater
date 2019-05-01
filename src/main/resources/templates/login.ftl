<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>登陆到控制台</title>

    <!-- Favicon and touch icons -->
    <link rel="shortcut icon" href="${web_resource}proton/ico/favicon.ico" type="image/x-icon" />

    <!-- Css files -->
    <link href="${web_resource}proton/css/bootstrap.min.css" rel="stylesheet">
    <link href="${web_resource}proton/css/jquery.mmenu.css" rel="stylesheet">
    <link href="${web_resource}proton/css/font-awesome.min.css" rel="stylesheet">
    <link href="${web_resource}proton/plugins/jquery-ui/css/jquery-ui-1.10.4.min.css" rel="stylesheet">
    <link href="${web_resource}proton/css/style.min.css" rel="stylesheet">
    <link href="${web_resource}proton/css/add-ons.min.css" rel="stylesheet">
    <style>
        footer {
            display: none;
        }
    </style>

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>
<div class="container-fluid content">
    <div class="row">
        <div id="content" class="col-sm-12 full">
            <div class="row">
                <div class="login-box">
                    <div class="header">
                        登陆到管理控制台
                    </div>
                    <form class="form-horizontal login" action="javascript:;" method="post">
                        <fieldset class="col-sm-12">
                            <div class="form-group">
                                <div class="controls row">
                                    <div class="input-group col-sm-12">
                                        <input type="text" class="form-control" id="name" placeholder="账号"/>
                                        <span class="input-group-addon"><i class="fa fa-user"></i></span>
                                    </div>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="controls row">
                                    <div class="input-group col-sm-12">
                                        <input type="password" class="form-control" id="password" placeholder="登陆密码"/>
                                        <span class="input-group-addon"><i class="fa fa-key"></i></span>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <button type="submit" class="btn btn-lg btn-primary col-xs-12">Login</button>
                            </div>
                        </fieldset>
                    </form>
                    <div class="clearfix"></div>
                </div>
            </div><!--/row-->
        </div>
    </div><!--/row-->

</div><!--/container-->


<!-- start: JavaScript-->
<!--[if !IE]>-->

<script src="${web_resource}proton/js/jquery-2.1.1.min.js"></script>

<!--<![endif]-->

<!--[if IE]>

<script src="${web_resource}proton/js/jquery-1.11.1.min.js"></script>

<![endif]-->

<!--[if !IE]>-->

<script type="text/javascript">
    window.jQuery || document.write("<script src='${web_resource}proton/js/jquery-2.1.1.min.js'>"+"<"+"/script>");
</script>

<!--<![endif]-->

<!--[if IE]>

<script type="text/javascript">
    window.jQuery || document.write("<script src='${web_resource}proton/js/jquery-1.11.1.min.js'>"+"<"+"/script>");
</script>

<![endif]-->
<script src="${web_resource}proton/js/jquery-migrate-1.2.1.min.js"></script>
<script src="${web_resource}proton/js/bootstrap.min.js"></script>


<!-- page scripts -->

<!-- theme scripts -->
<script src="${web_resource}proton/js/SmoothScroll.js"></script>
<script src="${web_resource}proton/js/jquery.mmenu.min.js"></script>

<!-- end: JavaScript-->
<script type="text/javascript">
    $(document).ready(function()
    {
        $('.login').submit(function()
        {
            var name = $.trim($('#name').val());
            var password = $.trim($('#password').val());
            if (name == '' || password == '') return alert('请输入用户名及密码进行登陆'), false;

            $.post('${context}/login', { name : name, password : password }, function(result)
            {
                if (result.error.code) return alert(result.error.reason);
                location.href = '${context}/manage/';
            });
        });
    });
</script>

</body>
</html>