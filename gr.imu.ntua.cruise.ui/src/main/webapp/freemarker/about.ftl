<!DOCTYPE html>
<!-- freemarker macros have to be imported into a namespace.  We strongly
recommend sticking to 'spring' -->
<#import "spring.ftl" as spring/>
<#import "macro/iccs.ftl" as iccs/>

<html xmlns="http://www.w3.org/1999/xhtml">
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

    <script src="<@spring.url "/static/js/less-1.1.3.min.js" />" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery-1.9.0.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery-ui-1.10.0.custom.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/slimScroll.min.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/bootstrap.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/site.js" />" type="text/javascript"></script>
    <script type="text/javascript" src="http://courses.ischool.berkeley.edu/i290-4/f09/resources/gm_jq_xhr.js"></script>
    


</head>

<body class="about">

<script type="text/javascript">
</script>

<div class="container">

    <div class="row sticky-top">
        <div class="span6">
            <h1>Welcome to <span class="wavy">CRUISE</span>!</h1>
        </div>
        <div class="span6" style="padding-top: 5px">
            <div class="navbar navbar-inverse">
                <div class="navbar-inner" >
                    <ul class="nav">
                        <li ><a href="<@spring.url "/"/>">Home</a></li>
                        <li class="active"><a href="#">About</a></li>
                        <li ><a href="<@spring.url "/twitterSignIn"/>">Sign-In</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    <div class="row body">
        <p>The <strong>CR</strong>eative <strong>U</strong>ser centric <strong>I</strong>nspirational <strong>SE</strong>arch is
            a tool which crawls the twittersphere based on your search terms, and provides a visual
            representation (in the form of a word cloud) of <em>twiiter</em> terms which are related.</p>
        <p>By clicking on the terms in the word cloud, <strong>CRUISE</strong> fetches search results from <strong>Bing</strong> and <strong>Flickr</strong> with a means to
            broaden your search space, and ultimately provide inspiration.</p>

        <h3>How it works</h3>
        <p>If you want to maximize your <strong>inspiration</strong> here are some technical details to help you.</p>
        <p>The first step of the process is entering some search terms and pressing
            the <em>"Explore!"</em>. At that moment our tool will fetch the popular tweets matching
            your search terms and it will create a term frequency map which will be displayed to you in the form of a <em>word cloud</em>.
            This process can be repeated by clickingh <em>"Explore!"</em> and the word cloud is refresh analogously. If your search terms
            are modified then this process is started from the beginning</p>
        <p>You can define your search path by clicking the <i class="icon icon-plus"></i> sign that is displayed when you hover over a
            word.<p>
        <p>Each time your search path is modified we will present to you the <strong>top 30 diversified</strong> results from
            <a href="http://www.bing.com" target="_blank">Bing!</a> and <a href="http://www.flickr.com" target="_blank">Flick</a>.</p>
        <p style="font-size: 0.9em;margin: 20px 0">In case the word cloud doesn't show up, this means that either no
            tweets have been found matching your search terms, or that your information is still being processed.</p>
        <p>
            <a href="#"
        </p>
        <div>
                <h3>Twitter Sign In</h3>
                <button type="button" onclick="requestToken()">Request Token</button>
                <input type="text" id="PINFIELD">
                <button type="button" onclick="authorize()">Enter PIN!</button>
                <button type="button" onclick="Search()">Search!</button>
            </div>
            <div style="margin-top: 20px;">
                <div id="twitter_stream" style="width: 200px; display: inline-block;">
                        Twitter Stream
                </div>

                <div id="my_twitter_stream" style="width: 200px; display: inline-block;">
                        My Twitter Stream
                </div>
            </div>

  
    
   
</div>

    <div class="row">
        <div class="span6">

        <h3>Project</h3>
        <img src="http://projectcollage.eu/wp-content/uploads/2012/11/Logo_Horizontal_a_01.png" style="margin: 10px;" alt="Project Collage"/>
        <hr/>
        <p>
        This work is part of the COLLAGE project <a href="http://projectcollage.eu" target="_blank">http:/projectcollage.eu</a>.
        Partially funded by the FP7 Programme <a href="http://cordis.europa.eu/fp7/home_en.html" target="_blank">http://cordis.europa.eu/fp7/home_en.html</a>
        </p>

        </div>

        <div class="span6">

        <h3>Team</h3>
        <div class="row">
            <div class="span1">
                <img src="<@spring.url "/static/images/imu-logo.png"/>" width="60" height="60" style="margin: 10px" alt="IMU" />
            </div>
            <div class="span5">
            <ul>
                <li><a href="http://imu.ntua.gr/users/mtara" target="_blank">Maria Taramigkou</a></li>
                <li><a href="http://imu.ntua.gr/users/fotisp" target="_blank">Fotis Paraskevopoulos</a></li>
                <li><a href="http://imu.ntua.gr/users/mpthimios" target="_blank">Efthimios Bothos</a></li>
                <li><a href="http://imu.ntua.gr/users/dapost" target="_blank">Dimitris Apostolou</a></li>
                <li><a href="http://imu.ntua.gr/users/gmentzas" target="_blank">Gregoris Mentzas</a></li>
            </ul>

            </div>
        </div>
        </div>

    </div>
</div>
</body>
</html>
