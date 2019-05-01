<!-- start: Header -->
<div class="navbar" role="navigation">

    <div class="container-fluid">

        <ul class="nav navbar-nav navbar-actions navbar-left">
            <li class="visible-md visible-lg"><a href="#" id="main-menu-toggle"><i class="fa fa-th-large"></i></a></li>
            <li class="visible-xs visible-sm"><a href="#" id="sidebar-menu"><i class="fa fa-navicon"></i></a></li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
            <li class="visible-md visible-lg">
                <a href="javascript:;" class="dropdown-toggle">${loginUser.name}</a>
            </li>
            <li class="visible-md visible-lg">
                <a href="javascript:;" class="dropdown-toggle" id="btn-changepwd">修改密码</a>
            </li>
            <li><a href="${context}/logout"><i class="fa fa-power-off"></i></a></li>
        </ul>
    </div>
</div>
<!-- end: Header -->