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
    <link rel="stylesheet" href="<@spring.url "/static/css/jqcloud.css"/>" type="text/css" media="screen" />
    <link rel="stylesheet" href="<@spring.url "/static/css/jquery-ui-1.10.0.custom.css"/>" type="text/css" media="screen" />
    <link rel="stylesheet/less" type="text/css" href="<@spring.url "/static/css/main.less"/>" />

    <script src="<@spring.url "/static/js/less-1.1.3.min.js" />" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery-1.9.0.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery-ui-1.10.0.custom.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery.timers-1.2.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/slimScroll.min.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jqcloud-1.0.1.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/bootstrap.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/bootstrapx-clickover.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jq.pubsub.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/site.js" />" type="text/javascript"></script>

</head>

<body class="home">

<script type="text/javascript">
</script>

<div class="container">

    <div class="row sticky-top">
        <div class="span6">
            <h1>Welcome to <span class="wavy">CRUISE</span>!</h1>
            <div class="search-controls ">
                <input type="text" id="stream-terms" name="stream-terms"/>
                <a href="#" class="btn btn-info btn-go twitter"><i class="icon-white icon-search"></i>&nbsp;&nbsp;Explore!</a>
                <label>
                    <input type="checkbox" class="waag"/>
                    Inspirational Sources Only
                </label>
                <div class="status-update"></div>
                <@iccs.loader id="search"/>
            </div>
        </div>
        <div class="span6" style="padding-top: 5px">
            <div class="navbar navbar-inverse">
                <div class="navbar-inner">
                    <ul class="nav">
                        <li class="active"><a href="#">Home</a></li>
                        <li><a href="<@spring.url "/about"/>">About</a></li>
                    </ul>
                </div>
            </div>
            <div id="forms" class="hide">
                <img src="<@spring.url "/static/images/got-inspired.png" />" alt="Got Inspired?">
                <a href="#" class="btn btn-success btn-mini" id="got-inspired">Yes</a>
                <a href="#" class="btn btn-danger btn-mini"  id="not-inspired">No</a>
            </div>
        </div>
    </div>
    <div class="row">
        <div id="results-wrapper" class="span12">
            <h2 class="titles hide">Explore Cloud</h2>
            <div id="results"></div>
        </div>
    </div>
    <div id="terms" class="row">
        <div class="span12">
            <h2 class="titles hide">Explore Query</h2>
            <ul class="clicked inline"></ul>
        </div>
    </div>

    <div class="row ">
        <div class="span4">
            <div class="search-results"></div>
        </div>
        <div class="span4">
            <@iccs.loader id="waag" message="Fetching results from waag..."/>
            <div class="waag-resutls"></div>
        </div>
        <div class="span4">
            <@iccs.loader id="flickr" message="computing a more visual representation for you :)"/>
            <div class="flickr-resutls"></div>
        </div>
    </div>
    <div id="div-inspired">
        <form id="form-inspired" name="form-inspired">
            <fieldset>
                <@iccs.range "questionaire.task"/>
                <@iccs.range "questionaire.variety"/>
                <@iccs.range "questionaire.unexpected"/>
                <@iccs.range "questionaire.divergence"/>
                <@iccs.range "questionaire.reformulate"/>
                <@iccs.range "questionaire.visualization"/>
                <@iccs.area "questionaire.other"/>
                <a href="<@spring.url "/searchreal/form/inspired"/>" class="btn submit-inspired">Submit</a>
            </fieldset>
        </form>
    </div>
    <div id="div-not-inspired">
        <form id="form-not-inspired" name="form-not-inspired">
            <fieldset>
                <@iccs.area "questionaire.ui"/>
                <@iccs.area "questionaire.other"/>
                <a href="<@spring.url "/searchreal/form/inspired"/>" class="btn submit-not-inspired">Submit</a>
            </fieldset>
        </form>
    </div>
</div>


<script type="text/javascript">

    var timeout =-1;
    var $check = $(".btn-go" );
    var $loader=$("#search-loader");
    var $floader=$("#flickr-loader");
    var $wloader=$("#wagg-loader");
    var $terms= $('#stream-terms');
    var $fpost=null;

    var $nexStatus=-1;

    $terms.bind('keypress', function(e) {
        if(e.keyCode==13){
            ICCS.explore();
        }
    });


    $check.click('click',function(e){

        e.preventDefault();
        ICCS.explore();

    });

    var ICCS = {
        current: 0,
        setupForm:function(){

            var tmp = $.fn.clickover.Constructor.prototype.show;
            $.fn.clickover.Constructor.prototype.show = function () {
                tmp.call(this);
                if (this.options.callback) {
                    this.options.callback();
                }
            };


            var $gi = $('#got-inspired');
            var $ni = $('#not-inspired');




            $gi.clickover({
                'animation':true,
                'placement':'bottom',
                'html':true,
                'content':function(){
                    return $('#div-inspired').html();
                },
                'callback':function(){
                    $(".submit-inspired").bind('click',function(e){
                        e.preventDefault();
                        var $data=$("#form-inspired").serialize();
                        $.post($(this).attr("href"),$data).done(function(data){
                            window.location= '<@spring.url "/searchreal" />';

                        });
                    });
                }
            });

            $ni.clickover({
                'animation':true,
                'placement':'bottom',
                'html':true,
                'content':function(){
                    return $('#div-not-inspired').html();
                },
                'callback':function(){
                        $(".submit-not-inspired").bind('click',function(e){
                        e.preventDefault();
                        var $data=$("#form-not-inspired").serialize();
                        $.post($(this).attr("href"),$data).done(function(data){
                            window.location= '<@spring.url "/searchreal" />';
                        });
                    });
                }

            });


        },

        explore:function(){


            $('ul.clicked').html("");
            $('.results').html('');
            $('.search-results').html('');
            $('.flickr-resutls').html('');
            $('.waag-resutls').html('');
            $('.titles').addClass('hide');
            $('#forms').addClass('hide');


            var $list = [];

            var $li = $('ul.clicked > li > .term').each(function(i,data){
                $list.push($(data).text());
            });

            if($list.length <=0){
                $list="";
            }


            var enableWaag=$('.waag').is(":checked");
            $loader.fadeIn();
            $.post("<@spring.url "/searchreal/explore"/>",
                    {
                        terms: $terms.val(),
                        "filter[]":$list,
                        "threshold": 0,
                        "enableWaag":enableWaag
                    },
            function(data){
                $("#results").html(data);
                $('.titles').removeClass('hide');
                $('#forms').removeClass('hide');
            }).complete(function () {
                $loader.fadeOut();
            }).error(function () {
                $loader.fadeOut();
                $("#results").html('<h1 class="alert-error">Ooops!, an error occurred</h1>');
                $('.titles').addClass('hide');
            });
        },



        statusUpdate:function(){

            $('.status-update').html(60-ICCS.current);
            if(ICCS.current == 60){
                ICCS.current=0;
            }else{
                ICCS.current=ICCS.current+1;
            }

        },

        fetch : function(){

            var $list = [];

            var $li = $('ul.clicked > li > .term').each(function(i,data){
                $list.push($(data).text());
            });

            if($list.length <=0){
                $list="";
            }


            var enableWaag=$('.waag').is(":checked");
            $loader.fadeIn();
            $.post("<@spring.url "/searchreal/realQuery"/>",
                    {
                        "filter[]":$list,
                        "threshold": 0,
                        "enableWaag":enableWaag
                    },
                    function(data){
                        $("#results").html(data);
                        $('.titles').removeClass('hide');
                    }).complete(function() {
                        $loader.fadeOut();
                    }).error(function(data){

                    });
        },

        diversify:function(){

            $('.search-results').fadeOut('fast',function(){
                $(".search-results").html("");
            });

            var $list = [];

            var $li = $('ul.clicked > li > .term').each(function(i,data){
                $list.push($(data).text());
            });

            if($list.length <=0){
                $list="";
            }



            $loader.fadeIn();
            $floader.fadeIn();
            $wloader.fadeIn();

            if($fpost !=null){
                $fpost.abort();
            }

            $.post("<@spring.url "/diversify/waag"/>",{
                "filter[]":$list
            },function(data){
                $('.waag-resutls').html(data);
            }).complete(function() {
                $wloader.fadeOut();
            }).error(function(data){

            });

            $.post("<@spring.url "/diversify/flickr"/>",{
                "filter[]":$list
            },function(data){
                $('.flickr-resutls').html(data);
            }).complete(function() {
                $floader.fadeOut();
            }).error(function(data){

            });
            $.post("<@spring.url "/diversify/bing"/>",
            {
                "filter[]":$list
            },
            function(data){

                $(".search-results").html(data);
                $(".search-results").fadeIn();

            }).complete(function() {
                $loader.fadeOut();
            }).error(function(data){

            });

        }
    }

    $.subscribe("term.added",function(){
       ICCS.fetch();
       ICCS.diversify();
    });

    $(document).on('click','.filterterm',function(e){
        $(this).parent().remove();
        $.publish("term.added");
    });

    ICCS.setupForm();

</script>
</body>
</html>