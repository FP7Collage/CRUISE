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
    <link rel="stylesheet" href="<@spring.url "/static/css/jqcloud.css"/>" type="text/css" media="screen" />
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
    <script src="<@spring.url "/static/js/jqcloud-1.0.1.js"/>" type="text/javascript"></script>
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
            <h1>Welcome to <span class="wavy">CRUISE</span>!</h1>
        </div>
        <div class="span6" style="padding-top: 5px ;display:none">
            <div class="navbar navbar-inverse">
                <div class="navbar-inner">
                    <ul class="nav">
                        <li class="active"><a href="#">Home</a></li>
                        <li><a href="<@spring.url "/about"/>">About</a></li>

                    </ul>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
       <div class="span3">
           <div class="menu">
            <ul class="clippy unstyled">
                <li data-step="1">
                    <span class="step" >1</span> Enter your explore terms
                    <div
                            class="help explore clickover"
                            title="By entering your explore terms we will gather information
                                    from several information sources and we will form a word
                                    cloud which should help you vizualize terms outside of your
                                    initial ones"><i class="icon icon-question-sign"></i></div></li>
                <li data-step="2">
                    <span class="step">2</span> Navigate the term cloud
                    <div
                            class="help navigate clickover"
                            title="By clicking the <i class='icon icon-plus'></i> icon when hovering
                                    over the words, we add these to your 'Search Query'. Each time this
                                    query is modified, we display diversified content from online sources
                                    from which can draw inspiration.
                                    <br/><br/>
                                    If you would like to digg deeper as to why a word is displayed in the
                                    word cloud then you can clik on the <i class='icon icon-search'></i>
                                    button and the source of the word will be displayed"
                            ><i class="icon icon-question-sign"></i></div></li>
                <li data-step="3">
                    <span class="step">3</span>Checkout and bookmarks the results provided
                    <div
                            class="help results clickover"
                            title="By building your search query, the results from different sources are
                            diversified and updated (some faster than others...). <br/><br/>
                            Here you have the opportunity to bookmark <i class='icon icon-bookmark'></i> any
                            results you think may be interesting for anyone that might find this usefull
                            on a similar path to yours."><i class="icon icon-question-sign"></i></div></li>
            </ul>
           </div>
        </div>
        <div class="span9">
            <div class="row">
                <div class="span9 search-controls ">
                    <form>
                        <input type="text" id="stream-terms" name="stream-terms"/>
                        <a id="explore-button" href="#" class="btn btn-info btn-go"><i class="icon-white icon-search"></i>&nbsp;&nbsp;Explore!</a>
                        <#--<a id="add-sources-button" href="#" class="btn"><i class="icon-white icon-twitter"></i> Customize Results </a>-->
                        <#--<div id="add-sources-popup">-->
                            <#--<h5>Add twitter usernames, 1 per line (with or without the @) max: 20 lines</h5>-->
                            <#--<p class="small">The more names you add the longer it will take</p>-->
                            <#--<textarea id="source-textfield"></textarea>-->
                            <#--<a class="btn btn-success sources-close-btn">Close</a>-->
                        <#--</div>-->
                        <div class="status-update"></div>
                    <@iccs.loader id="search"/>
                    </form>
                </div>
            </div>
            <div class="row">
                <div id="cloud-wrapper" class="span9">
                    <h2 class="titles">Term Cloud</h2>
                    <div id="cloud">
                        <#if results??>
                            <#include "_cloud.ftl" />
                        </#if>
                    </div>
                </div>
            </div>
            <div class="row">
                <div id="query-wrapper" class="span9">
                    <h2 class="titles">Explore Query</h2>
                    <ul id="query" class="clicked inline"></ul>
                </div>
            </div>

            <div id="results-wrapper" class="row">
                <div class="span9">
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

    iccs.setup('<@spring.url "/academic" />','<@spring.url "/academic" />',['scholar']);
    iccs.handleStep(${step});
    iccs.handleTerms('${terms}');
    iccs.handleQuery(('${query}'));
</script>
</html>