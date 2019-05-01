<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <#include "./inc/header.ftl">
    <title>管理控制台</title>
</head>
<body>

<#include "./inc/header.ftl">
<div class="container-fluid content">
    <div class="row">
        <#include "./inc/sidebar.ftl">
        <div class="main">
            <div class="row">
                <div class="col-lg-12">
                    <h3 class="page-header"><i class="fa fa-laptop"></i> Dashboard</h3>
                    <ol class="breadcrumb">
                        <li><i class="fa fa-home"></i><a href="${context}/">首页</a></li>
                        <li><i class="fa fa-laptop"></i>Dashboard</li>
                    </ol>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">写点什么吧</div>
                        <div class="panel-body">
                            <h3>给点面子吧</h3>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- end: Content -->
    </div><!--/container-->
</div>
<#include "./inc/footer.ftl">
</body>
</html>