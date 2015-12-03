<!DOCTYPE html>
<!-- freemarker macros have to be imported into a namespace.  We strongly
recommend sticking to 'spring' -->
<#import "../spring.ftl" as spring/>
<#import "../macro/iccs.ftl" as iccs/>

<html xmlns="http://www.w3.org/1999/xhtml" >
<head>
    <title>CRUISE: CReative User centric Inspirational SEarch</title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <!--http://stackoverflow.com/questions/3949941/what-throws-internet-explorer-into-quirks-mode -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />

    <!-- Your styles -->
    <link href="http://fonts.googleapis.com/css?family=Special+Elite" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" href="<@spring.url "/static/css/bootstrap.css"/>" type="text/css" media="screen" />
    <link rel="stylesheet" href="<@spring.url "/static/css/bootstrap-responsive.css"/>" type="text/css" media="screen" />
    <link rel="stylesheet" href="<@spring.url "/static/css/font-awesome.min.css"/>" type="text/css" media="screen" />
    <link rel="stylesheet" href="<@spring.url "/static/css/jquery-ui-1.10.0.custom.css"/>" type="text/css" media="screen" />


    <link rel="stylesheet/less" type="text/css" href="<@spring.url "/static/css/main.less"/>" />

    <script src="<@spring.url "/static/js/console.log.js" />" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/less-1.1.3.min.js" />" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery-1.9.0.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery-migrate-1.2.1.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery-ui-1.10.0.custom.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery.tools.min.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery.history.js"/>" type="text/javascript"></script>


    <script src="<@spring.url "/static/js/jquery.timers-1.2.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/slimScroll.min.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/bootstrap.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/bootstrapx-clickover.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jq.pubsub.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/v2.js" />" type="text/javascript"></script>

</head>

<body class="explore academic">

<script type="text/javascript">
    iccs.bindHistory();
</script>

<div class="container">

    <div class="row sticky-top">
        <div class="span6">
            <h1>Scholar Baseline!</h1>
        </div>
    </div>
    <div class="row">
    <div class="span12">
            <div class="row">
                <div class="span12 search-controls ">
                    <form>
                        <input type="text" id="stream-terms" name="stream-terms"/>
                        <a id="explore-button" href="#" class="btn btn-info btn-go"><i class="icon-white icon-search"></i>&nbsp;&nbsp;Explore!</a>
                        <a id="add-sources-button" href="#" class="btn"><i class="icon-white icon-twitter"></i> Customize Results </a>
                        <div id="add-sources-popup">
                            <h5>Add twitter usernames, 1 per line (with or without the @) max: 20 lines</h5>
                            <p class="small">The more names you add the longer it will take</p>
                            <textarea id="source-textfield"></textarea>
                            <a class="btn btn-success sources-close-btn">Close</a>
                        </div>
                        <div class="status-update"></div>
                    <@iccs.loader id="search"/>
                    </form>
                </div>
                <div class="span12 hide">
                    <ul id="query"></ul>
                </div>
            </div>
            <div class="row" style="display: none">
                <div id="query-wrapper" class="span9">
                    <h2 class="titles">Explore Query</h2>
                    <ul id="query" class="clicked inline"></ul>
                </div>
            </div>

            <div id="results-wrapper" class="row">
                <div class="span12">
                    <@iccs.loader id="scholar" message="Fetching results from scholar..."/>
                    <div id="scholar-results" >
                        <#if engines?? && engines.scholar??>
                            <!-- This should come from the controller -->
                            <#assign searchEngine="Scholar" />
                            <#assign diversified= engines.scholar/>
                            <#include "_diversify-single.ftl">
                        </#if>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">


    iccs.setup('<@spring.url "/baseline" />','<@spring.url "/baseline" />',['scholar']);
    iccs.setupBaseline();

</script>
</html>