<!DOCTYPE html>
<html lang="en">
<head>
<#include "../inc/resource.ftl">
    <title>实时监控</title>
    <style type="text/css">
        .info-box { height: 120px !important; }
        .info-box .count { margin-top: 10px !important; margin-bottom: 10px !important; }
        #everyMinuteQueries { height: 340px; }
        #topQueryClients, #topQueryNames { min-height: 408px; }
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
                    <h3 class="page-header"><i class="fa fa-laptop"></i> 今日实时统计</h3>
                </div>
            </div>

            <div class="row">

                <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
                    <div class="info-box red-bg">
                        <i class="fa fa-search"></i>
                        <div class="count" id="totalQueryCount"></div>
                        <div class="title">总查询量</div>
                    </div>
                </div>

                <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
                    <div class="info-box green-bg">
                        <i class="fa fa-cubes"></i>
                        <div class="count" id="totalAnswerCount">-</div>
                        <div class="title">总应答量</div>
                    </div>
                </div>

                <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
                    <div class="info-box blue-bg">
                        <i class="fa fa-level-up"></i>
                        <div class="count" id="totalQueryUpstreamCount">-</div>
                        <div class="title">查询上游次数</div>
                    </div>
                </div>

                <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
                    <div class="info-box magenta-bg">
                        <i class="fa fa-cloud"></i>
                        <div class="count" id="totalCachedCount">-</div>
                        <div class="title">总缓存量</div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-lg-12 col-md-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-signal"></i>
                            <h2>今日每分钟查询量</h2>
                        </div>
                        <div class="panel-body" id="everyMinuteQueries">
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-user"></i>
                            <h2>今日查询来源IP TOP10</h2>
                        </div>
                        <div class="panel-body" id="topQueryClients">
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <i class="fa fa-star-half-full"></i>
                            <h2>今日查询域名 TOP10</h2>
                        </div>
                        <div class="panel-body" id="topQueryNames">
                        </div>
                    </div>
                </div>

            </div>

        </div>
    </div>
</div>
<#include "../inc/footer.ftl">
<!--<script type="text/javascript" src="${web_resource}/echarts-4.2.1/echarts.common.min.js"></script>-->
<script type="text/javascript" src="${web_resource}/echarts-4.2.1/echarts.min.js"></script>
<script type="text/javascript">
    $(document).ready(function()
    {
        loadSummary();
        loadEveryMinuteQueries();
        loadTop10QueryClients();
        loadTop10QueryNames();
    });

    // 今日每分钟查询量
    var firstLoad = true;
    function loadEveryMinuteQueries()
    {
        $.post('./everyMinuteQueries', {}, function(result)
        {
            if (result.error && result.error.code) return setTimeout(loadEveryMinuteQueries, 30000);
            var timeList = [], dataList = [];
            for (var i = 0; i < result.data.length; i++)
            {
                var m = (i % 60);
                var time = parseInt(i / 60) + ':' + (m < 10 ? '0' + m : m);
                timeList.push(time);
                dataList.push(result.data[i]);
            }
            var option = {
                grid: {
                    left: '40px',
                    right: '40px',
                    bottom: '80px',
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        animation: true
                    }
                },
                xAxis: {
                    type: 'category',
                    data: timeList
                },
                yAxis: {
                    type: 'value'
                },
                series: [{
                    data: dataList,
                    type: 'line',
                    smooth: true
                }]
            };
            if (firstLoad) option.dataZoom = [
                {
                    type: 'inside',
                    start: 80,
                    end: 100
                },
                {
                    show: true,
                    type: 'slider',
                    y: '90%',
                    start: 80,
                    end: 100
                }
            ];
            firstLoad = false;
            echarts.init(document.getElementById('everyMinuteQueries')).setOption(option);
            setTimeout(loadEveryMinuteQueries, 10000);
        });
    }

    // 今日TOP 10 查询来源IP
    function loadTop10QueryClients()
    {
        $.post('./topQueryClients', {}, function(result)
        {
            if (result.error && result.error.code) return setTimeout(loadTop10QueryClients, 10000);
            var shtml = '';
            shtml += '<table class="table bootstrap-datatable datatable small-font dataTable no-footer">';
            shtml += '  <thead>';
            shtml += '      <tr>';
            shtml += '          <th class="text-center" width="60">#</th>';
            shtml += '          <th class="text-left">IP</th>';
            shtml += '          <th class="text-center" width="120">查询次数</th>';
            shtml += '      </tr>';
            shtml += '  </thead>';
            shtml += '  <tbody>';
            for (var i = 0; i < result.data.length; i++)
            {
                var item = result.data[i];
                shtml += '<tr>';
                shtml += '  <td class="text-center">' + (i + 1) + '</td>';
                shtml += '  <td class="text-left">' + item.ip + '</td>';
                shtml += '  <td class="text-center">' + item.queryCount + '</td>';
                shtml += '</tr>';
            }
            shtml += '</table>';
            $('#topQueryClients').html(shtml);

            setTimeout(loadTop10QueryClients, 10000);
        });
    }

    // 今日TOP 10 被查询域名
    function loadTop10QueryNames()
    {
        $.post('./topQueryNames', {}, function(result)
        {
            if (result.error && result.error.code) return setTimeout(loadTop10QueryNames, 10000);
            var shtml = '';
            shtml += '<table class="table bootstrap-datatable datatable small-font dataTable no-footer">';
            shtml += '  <thead>';
            shtml += '      <tr>';
            shtml += '          <th class="text-center" width="60">#</th>';
            shtml += '          <th class="text-left">域名</th>';
            shtml += '          <th class="text-center" width="120">查询次数</th>';
            shtml += '      </tr>';
            shtml += '  </thead>';
            shtml += '  <tbody>';
            for (var i = 0; i < result.data.length; i++)
            {
                var item = result.data[i];
                shtml += '<tr>';
                shtml += '  <td class="text-center">' + (i + 1) + '</td>';
                shtml += '  <td class="text-left">' + item.name + '</td>';
                shtml += '  <td class="text-center">' + item.queryCount + '</td>';
                shtml += '</tr>';
            }
            shtml += '</table>';
            $('#topQueryNames').html(shtml);

            setTimeout(loadTop10QueryNames, 10000);
        });
    }

    // 几个计数项
    function loadSummary()
    {
        $.post('./summary', {}, function(result)
        {
            if (result.error && result.error.code) return setTimeout(loadSummary, 10000);
            $('#totalQueryCount').html(result.data.totalQueryCount);
            $('#totalAnswerCount').html(result.data.totalAnswerCount);
            $('#totalQueryUpstreamCount').html(result.data.totalQueryUpstreamCount);
            $('#totalCachedCount').html(result.data.totalCachedCount);

            setTimeout(loadSummary, 10000);
        });
    }

</script>
</body>
</html>