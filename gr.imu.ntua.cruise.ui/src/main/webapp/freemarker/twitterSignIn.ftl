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
    <script type="text/javascript" src="http://147.102.23.40/codebird/sha1.js"></script>
    <script type="text/javascript" src="http://147.102.23.40/codebird/codebird.js"></script>
    <script type="text/javascript">
   // var el = document.getElementById('foo');
   // el.onclick =  requestToken;

                var cb = new Codebird();
                var baseUrl='';

                cb.setConsumerKey("7mUjnrmVTKB86QNSs346PA","ZHtdZWBqTrAGeUh3fouYICFq1czl0vIcQ3DQoxmBa0");
                cb.setProxy('http://147.102.23.40/codebird/codebird-cors-proxy.php');
                var id=null;
                var pass= null;
                var jsonObj=null;

                // gets a request token
            function requestToken(){
                        cb.__call(
                                "oauth_requestToken",

                                {oauth_callback: "oob"},
                                function (reply) {
                                        // stores it
                                        cb.setToken(reply.oauth_token, reply.oauth_token_secret);
                                     

                                        // gets the authorize screen URL
                                        cb.__call(
                                                "oauth_authorize",
                                                {},
                                                function (auth_url) {
                                                     
                                                        
                                                        window.codebird_auth = window.open(auth_url);
                                                        
                                                }
                                        );
                                }
                        );
                }
                function authorize(){
                
                        cb.__call(
                                "oauth_accessToken",
                                {oauth_verifier: document.getElementById("PINFIELD").value},

                                function (reply) {
                                        // store the authenticated token, which may be different from the request token (!)\
                                      //  cb.setToken(reply.oauth_token, reply.oauth_token_secret);
                                       
			
                                        
                                        id = reply.oauth_token;
                                      
                                        pass = reply.oauth_token_secret;
                                       // alert(id);
                                       // alert(pass);                                        

                                        // if you need to persist the login after page reload,
                                        // consider storing the token in a cookie or HTML5 local storage
                                         jsonObj = {
                                                                user: []
                                                        };
                                        jsonObj.user.push({
					"id" : id,
                                        "psw" : pass
                                        });
                                     //   jsonObj =
                                       // {
                                         //   id : id,
                                           // psw : pass
                                        //};
                                        
                                    //    alert(JSON.stringify(jsonObj));                                        
                                                                     
                                }

                        );
                                                                          

                }
       
                 function Search(){
                                $.ajax({
                                        //$.post(
                                          //baseUrl+'/AuthControl',
                                          //{
                                        
                                            cache : false,
                                          //  url: "<@spring.url 'about/AuthControl'/>",
                                            url: "http://localhost:8080/ui/about/AuthControl",
                                            type: "POST",                                            
                                            data: JSON.stringify(jsonObj),
                                          //  dataType: 'json',                                  
                                            contentType: "application/json; charset=utf-8",
                                            success: function(result) {
                                                alert("POST sent!");
                                            },
                                             error: function(xhr) {
												//alert("error " + ErrorText);
												//alert("error " + xhRequest);
												//alert("error " + thrownError);
												alert(JSON.stringify(jsonObj));
                                            }
                                        });   
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



<body class="twitterSignIn">

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
                        <li ><a href="<@spring.url "/about"/>">About</a></li>
                        <li class="active"><a  href="#" onclick="requestToken()">Sign-In</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
   
  <div>
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
</body>
</html>
