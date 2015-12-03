
<!DOCTYPE html>
<!-- freemarker macros have to be imported into a namespace.  We strongly
recommend sticking to 'spring' -->
<#import "../spring.ftl" as spring/>
<#import "../macro/iccs.ftl" as iccs/>

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

    <script src="<@spring.url "/static/js/console.log.js" />" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/less-1.1.3.min.js" />" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery-1.9.0.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery-migrate-1.2.1.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery-ui-1.10.0.custom.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery.tools.min.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery.history.js"/>" type="text/javascript"></script>
     <script src="<@spring.url "/static/js/mootools-core-1.4.5-full-nocompat.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jquery.timers-1.2.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/slimScroll.min.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jqcloud-1.0.1.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/bootstrap.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/bootstrapx-clickover.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/jq.pubsub.js"/>" type="text/javascript"></script>
    <script src="<@spring.url "/static/js/v2.js" />" type="text/javascript"></script>
    <script type="text/javascript" src="http://codeorigin.jquery.com/jquery-2.0.3.js"></script>
    <script src="http://codeorigin.jquery.com/jquery-2.0.3.min.js"></script>
    <script type="text/javascript" src="http://147.102.23.40/codebird/sha1.js"></script>
    <script type="text/javascript" src="http://147.102.23.40/codebird/codebird.js"></script>
    <script src="http://cdn.jsdelivr.net/jquery.cookie/1.4.0/jquery.cookie.min.js"></script>
    <script src="http://cdnjs.cloudflare.com/ajax/libs/json2/20121008/json2.min.js"></script>    


    
 <script type="text/javascript">
   // var el = document.getElementById('foo');
   // el.onclick =  requestToken;

     var cb = new Codebird();
     var baseUrl='';
     cb.setConsumerKey("lgVvEYx0dV97iznewvQ","bu1avi4SKuQepkDzXWf6pwTMtMartMn9wcHo6TFaRU");
               // cb.setConsumerKey("7mUjnrmVTKB86QNSs346PA","ZHtdZWBqTrAGeUh3fouYICFq1czl0vIcQ3DQoxmBa0");
     cb.setProxy('http://147.102.23.40/codebird/codebird-cors-proxy.php');
     var id=null;
     var pass= null;
     var jsonObj=null;
     var temp_token=null;
     var temp_token_secret = null;

      if (window.location.href.match(/oauth_verifier(.+)/)){			
			authorize();
		}

         function requestToken(){     
        cb.__call(
            "oauth_requestToken",
                {oauth_callback: "http://localhost:8080/ui/explore"}, 
                function (reply) {
                    $.cookie("cookieStore", JSON.stringify(reply));

                    cb.setToken(reply.oauth_token, reply.oauth_token_secret);

                                temp_token = reply.oauth_token;
                                temp_token_secret = reply.oauth_token_secret;

                    cb.__call(
                    "oauth_authorize",  {},
                    function (auth_url) {                  
                                                
                       window.location = auth_url;                  
                      
            });
            });
}

function authorize(o) {

             var currentUrl = window.location.href;
			var query = currentUrl.match(/oauth_verifier(.+)/);
			var parameters  = {};
			var parameter;
			for (var i = 0; i < query.length; i++) {
				parameter = query[i].split("=");
				if (parameter.length === 1) {
					parameter[1] = "";
				}
				parameters[decodeURIComponent(parameter[0])] = decodeURIComponent(parameter[1]);
			}
	     var reply = JSON.parse($.cookie("cookieStore"));           


cb.setToken(reply.oauth_token, reply.oauth_token_secret);

    cb.__call(
        "oauth_accessToken", {oauth_verifier:  parameters.oauth_verifier},
        function (reply) {

                    id = reply.oauth_token;                                      
                    pass = reply.oauth_token_secret;
              
                    jsonObj = {
                                                                user: []
                                                        };
                                        jsonObj.user.push({
					"id" : id,
                                        "psw" : pass
                                        });
                                     $.ajax({
                                        //$.post(
                                          //baseUrl+'/AuthControl',
                                          //{
                                        
                                            cache : false,
                                            url: "<@spring.url '/about/AuthControl'/>", //context/about/AuthControl
                                            type: "POST",
                                          //  url: "http://localhost:8080/ui/about/AuthControl",                                                                                        
                                            data: JSON.stringify(jsonObj),
                                            //dataType: "json",     type that is returned                             
                                            contentType: "application/json; charset=utf-8",
                                            success: function(result) {
                                                //alert("POST sent!");
                                              
                                            },
                                             error: function(xhr) {
												//alert("error " + ErrorText);
												//alert("error " + xhRequest);
												//alert("error " + thrownError);
												alert(JSON.stringify(jsonObj));
                                            }
                                        });          

                    });                                           
            }
 function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}


</script>


<script>
$(document).ready(function(){
  $("input").focus(function(){
    $(this).css("background-color","#cccccc");
  });
  $("input").blur(function(){
    $(this).css("background-color","#ffffff");
  });
});
</script>

    

</head>

<body class="explore" >

<script type="text/javascript">
    iccs.bindHistory();
</script>

<div class="container">

    <div class="row sticky-top">
        <div class="span6">
            <h1>Welcome to <span class="wavy">CRUISE</span>!</h1>
        </div>
        <div class="span6" style="padding-top: 5px">
            <div class="navbar navbar-inverse">
                <div class="navbar-inner">
                    <ul class="nav">
                        <li class="active"><a href="#">Home</a></li>
                        <li><a href="<@spring.url "/about"/>">About</a></li>
                       <!-- <li><a href="<@spring.url "/twitterSignIn"/>">Sign-In</a></li>-->
                    </ul>
                </div>
            </div>
            
            <div align="right">
           
             <button type="button" class="btn btn-info btn-go" onclick="requestToken()">Twitter Sign in</button> 
           

            <!--<input type="text" id="PINFIELD" >-->
            <!--<button type="button" onclick="authorize()">Submit PIN!</button>-->
            
            
            </div>

            <div id="forms" class="hide">
                <img src="<@spring.url "/static/images/got-inspired.png" />" alt="Got Inspired?">
                <a href="#" class="btn btn-success btn-mini" id="got-inspired">Yes</a>
                <a href="#" class="btn btn-danger btn-mini"  id="not-inspired">No</a>
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
                                    cloud which should help you visualize terms outside of your
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

                        <div class="status-update">
                        <table>
                                <tr>
                                    <TD><div align="right">Inspiration Level:</div></TD>
                                    <TD><INPUT TYPE="radio" id="level1" NAME="level" VALUE="low" >Low</TD>
                                    <TD><INPUT TYPE="radio" id="level2" NAME="level" VALUE="medium">Medium</TD>
                                    <TD><INPUT TYPE="radio" id="level3" NAME="level" VALUE="deep" >All</TD>
                                <tr>
                        </table>
                        
                        </div>
                    <@iccs.loader id="search"/>                    

                      <div> <table>
                        <tr>
                            <td><div align="right">Twitter Source:</div></td>
                            <td><label><input type="checkbox" id = "source1" name="source1" value="public" class="twittercheckbox" onClick=""/>Public Stream</label><td>
                            <td><label><input type="checkbox" id = "source3" name="source3" value="personal" class="twittercheckbox" onClick=""/>My Network Only</label><td
                            <td><label><input type="checkbox" id = "source2" name="source2" value="both" class="twittercheckbox" onClick="" />Both</label><td>
                            
                        <tr>
                      </table><div>
                    </form>                                       
                <div>
                    
               
                </div>
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
                <div id="recommendedinfo" >
                        <#if recommendedinfo??>
                            <#include "../partials/recommendations.ftl" />
                        </#if>
                    </div>          
          
            </div>
  

            <div id="results-wrapper" class="row">
                <div class="span3">
                    <@iccs.loader id="waag" message="Fetching results from bing..."/>
                    <div id="bing-results" >
                        <#if engines?? && engines.bing??>
                            <!-- This should come from the controller -->
                            <#assign searchEngine="Bing" />
                            <#assign diversified= engines.bing />
                            <#include "../partials/diversify-single.ftl">
                        </#if>
                    </div>
                </div>
                <div class="span3">
                <@iccs.loader id="waag" message="Fetching results from waag..."/>
                    <div id="waag-results">
                        <#if engines?? && engines.waag??>
                            <!-- This should come from the controller -->
                            <#assign searchEngine="Waag" />
                            <#assign diversified= engines.waag />
                            <#include "../partials/diversify-single.ftl">
                        </#if>
                    </div>
                </div>
                <div class="span3">
                <@iccs.loader id="flickr" message="computing a more visual representation for you :)"/>
                    <div id="flickr-results">
                        <#if engines?? && engines.flickr??>
                            <!-- This should come from the controller -->
                            <#assign searchEngine="Flickr" />
                            <#assign images= engines.flickr />
                            <#include "../partials/flickr.ftl">
                        </#if>
                    </div>
                </div>
            </div>
        </div>
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
</body>
<script type="text/javascript">

    iccs.setup('<@spring.url "/explore" />','<@spring.url "/diversify" />',['flickr','waag','bing']);
    iccs.handleStep(${step});
    iccs.handleTerms('${terms}');
    iccs.handleQuery(('${query}'));
   
</script>
</html>
