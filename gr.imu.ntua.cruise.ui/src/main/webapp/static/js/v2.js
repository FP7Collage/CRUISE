//http://stackoverflow.com/questions/881515/javascript-namespace-declaration
//http://enterprisejquery.com/2010/10/how-good-c-habits-can-encourage-bad-javascript-habits-part-1/

(function( iccs, $, undefined ) {

    var baseUrl ='';
    var diversifyUrl='';
    var engines=[];

    var previousTerms='';
    var previousQuery='';
    var querySearchTimeout = -1;

    //https://github.com/browserstate/history.js/issues/47#issuecomment-8448141
    var timestamps=[];

    log("Starting up....");

    var addLoading = function(id){

        console.log("Adding loading to ",id);
        $(id).html(
            "<div class='loading-frame'>" +
                "<img src='/static/images/ajax-loader.gif'> Fetching... </div>"
        );
    }
    var uuid = function(){
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
            return v.toString(16);
        });
    }

    var pushState = function(title, url, anydata){

        // Creating a unique timestamp that will be associated with the state.
        var t = uuid();
        log("Time "+t);
        timestamps[t] = t;
        log("Pushing state");
        log(timestamps[t]);
        //Adding to history
        History.pushState({timestamp:t}, title, url);
    };

    var getTerms =  function(){

        iccs.handleStep(1);

//        if($("#add-sources-popup").hasClass("open")){
//            addSources();
//        }



        addLoading("#cloud");

        $("#cloud-wrapper").fadeIn('fast',function(){
        });


        $("#query-wrapper").fadeOut('fast',function(){
            $("#query").empty();
        });

        $("#results-wrapper").fadeOut('fast');
        
          $("#recommendedinfo-wrapper").fadeOut('fast');


        var searchTerms = $("#stream-terms").val();
        
        //Check the value of checkbox-Twitter source of tweets
        if($('#source1').is(':checked')){
            var source = $("#source1").val();
        }
        else if($('#source2').is(':checked')){
            var source = $("#source2").val();
        }
        else if($('#source3').is(':checked')){
            var source = $("#source3").val();
        }
        
         //Check the value of radiobutton-Inspiration Level
    
         
         var level="Low";
         var terms=searchTerms;
           level= $('input:radio[name="level"]:checked').val();
        //  $("#recommendedinfo-wrapper").fadeIn('fast');
                    
       
        
        //push to the browser
        $.ajax({
            url:baseUrl+'/terms?q='+searchTerms+'&level='+level+'&source='+source
        }).done(function(data){

            $("#cloud").html(data);
          
            $("#cloud-wrapper").fadeIn('fast');
            $("#query-wrapper").fadeIn('fast');
            $("#recommendedinfo").fadeIn('fast');
          //  $('#recommendedinfo').html(data); 
           addLoading("#recommendedinfo");
                
       
             var  jsonObj = null;
         jsonObj = {
                      terms: []
                   };
                   jsonObj.terms.push({
		   "query" : searchTerms
                   
                   });
       //alert(jsonObj);
            $.ajax({
                  cache : false,
                  url: "http://localhost:8080/ui/recommend/tweets",//"<@spring.url '/recommend/tweets'/>", 
                  type: "POST",
                  data:  JSON.stringify(jsonObj),                                  
                  contentType: "application/json; charset=utf-8",
                  success: function(result) {
                           $('.recommendedinfo').html(result);
                           //alert(result);
                                        
                                            },
                                            complete: function(data){
                                            // $('.recommendedinfo').html(data);  
                                            }
                                        });     
       
           // $('#recommendedinfo').html(data); 
            iccs.handleStep(2);

            //fetch query with term filter
            var $list = searchTerms.split(" ");
            console.log($list);
            for(var i=0; i < $list.length; i++){

                appendQueryTerm($list[i]);
            }

            expandQuery();
        });

        previousTerms=searchTerms;

        pushState('CRUISE - Explore: '+searchTerms,baseUrl+"?terms="+searchTerms);
        



    };


    var expandQuery = function(){
        log('Expand query ...');

        $("#results-wrapper").fadeIn('fast');

        var $list = [];


        $('ul#query > li > .term').each(function(i,data){
            $list.push($(data).text());
        });


        iccs.handleStep(3);

        if($list.length <=0){
            $list="";
            iccs.handleStep(2);
        }


        for(var i=0; i<engines.length; i++ ){

            var engine=engines[i];

            addLoading("#"+engine+"-results");

            (function(e){

                $.post(diversifyUrl+"/"+e,{
                    "filter[]":$list
                },function(data){
                    var engineId ='#'+e+'-results';
                    $(engineId).html(data);
                }).complete(function() {
                        //turn off loader
                    });
            })(engine);
        }

        previousQuery = $list.join(',');

        pushState(
            'CRUISE - Explore: '+previousTerms+' and query: '+previousQuery,
            baseUrl+"?terms="+previousTerms+"&query="+previousQuery
        );

    };
    
    



    var bookmarkResult=function(that){

        $('.result-bookmark-link').addClass('hide');

        var oldHtml = $(that).html();

        $(that).html("<img src='/static/images/ajax-loader-facebook.gif'>");

        $(that).removeClass('hide');

        var $url = $(that).attr('href');
        var $source = $(that).attr('data-source');

        $.post(
            baseUrl+'/bookmark',
            {
                term:previousTerms,
                query:previousQuery,
                url:$url,
                source:$source
            },
            function(data){
                var json = JSON.parse(data);
                if(json.success){

                    log('success');
                    $(that).removeClass('result-bookmark-link');
                    log($(that).parent().parent());
                    $(that).parent().parent().addClass('bookmarked');
                    $(that).parent().html("<i class='icon icon-ok'></i>");
                }else{

                    $(that).parent().parent().removeClass('bookmarked');
                    $(that).addClass('result-bookmark-link');
                }
            }
        ).complete(function(){
                $('.result-bookmark-link').removeClass('hide');
                $(that).html(oldHtml);
            });
    };

    var recordRating=function(that){


        var boolRemove=$(that).hasClass('rated');

        $('.result-rating-btn').addClass('hide');

        var oldHtml = $(that).html();

        $(that).html("<img src='/static/images/ajax-loader-facebook.gif'>");

        $(that).removeClass('hide');

        log(that);
        var $url = $(that).attr('href');

        log("URL: "+$url);
        var $source = $(that).attr('data-source');
        var $rating = $(that).attr('data-rating');
        var $title = $(that).attr('data-title');


        $.post(
            baseUrl+'/rate',
            {
                term:previousTerms,
                query:previousQuery,
                url:$url,
                source:$source,
                title:$title,
                rating:$rating,
                remove:boolRemove
            },
            function(data){
                var json = JSON.parse(data);
                if(json.success){
                    log('success');

                    if(boolRemove){
                        $(that).removeClass('rated');
                    }else{
                        $(that).addClass('rated');
                    }
                }else{


                    if(boolRemove){
                        $(that).addClass('rated');
                    }else{
                        $(that).removeClass('rated');
                    }

                    $(that).addClass('result-rating-btn');
                }
            }
        ).complete(function(){
                $('.result-rating-btn').removeClass('hide');
                $(that).html(oldHtml);
        });
    };


    var appendQueryTerm=function(term){

        console.log("Appending query "+term);
        $('#query').append('<li><a href="#" class="filterterm">&times;</a> <span class="term">'+term+'</span></li>')

    };


//    var addSources=function(){
//        var element=$('#add-sources-button');
//        var textBox = $('#source-textfield');
//        var parent =textBox.parent();
//        var closeBtn =$(".sources-close-btn");
//        console.log(closeBtn);
//
//        if(parent.hasClass("open")){
//            parent.removeClass("open");
//            closeBtn.unbind('click');
//
//        }else{
//            parent.addClass("open");
//            closeBtn.bind('click',function(e){
//                console.log('click');
//                e.preventDefault();
//                addSources();
//
//            });
//        }
//
//        if(textBox.val() !=""){
//            $(element).addClass("btn-success");
//        }else{
//            $(element).removeClass("btn-success");
//        }
//    };

    iccs.setupBaseline = function(){

        $('#explore-button').unbind('click');


        $('#explore-button').bind('click',function(e){

            console.log('Te');
            e.preventDefault();
            //create list

            $("#query").html("");

            var terms = $("#stream-terms").val();

            terms = terms.split(" ");

            for(var i =0; i< terms.length; i++){
                appendQueryTerm(terms[i]);
            }

            expandQuery();

            return false;
        });


    }


    iccs.setup = function(burl,durl,e){

        engines = e;
        baseUrl= burl;
        diversifyUrl = durl;

        log("Setting up the UI...");

        $('#explore-button').bind('click',function(e){
            e.preventDefault();
            getTerms();
            return false;
        });



        $("#stream-terms").bind('keypress',function(e){

            if(e.keyCode == 13){
                e.preventDefault();
                getTerms();
                return false;
            }


        });


//        $("#add-sources-button").bind('click',function(e){
//
//            e.preventDefault();
//            addSources($(this));
//            return false;
//
//        });
//
//        $(".clickover").each(function(index,element){
//
//            $(element).clickover({
//                'animation':true,
//                'placement':'bottom',
//                'html':true
//            });
//        });


        $.subscribe("term.added",function(term){

            if(term != undefined && term !==''){
                appendQueryTerm(term);
            }

            clearTimeout(querySearchTimeout);
            querySearchTimeout = setTimeout(function(){
               clearTimeout(querySearchTimeout);
               expandQuery();
            },1500);
        });

        //check the current step
        console.log($("#cloud").length);
        if($("#cloud").length  && $("#cloud").html().trim() !== ''){
            $("#cloud-wrapper").fadeIn('fast',function(){});
            $("#query-wrapper").fadeIn('fast');
        }


        if($('#results-wrapper').find('.search-diversified-results').length > 0){
            $("#results-wrapper").fadeIn('fast');
        }
        if($('#recommended').find('.search-diversified-results').length > 0){
            $("#recommended").fadeIn('fast');
        }

        $(document).on('click','.filterterm',function(e){
            e.preventDefault();
            $(this).parent().remove();
            $.publish("term.added");
            return false;
        });

        $(document).on('click','.result-rating-btn',function(e){
            e.preventDefault();
            recordRating(this);
            return false;
        });

        $(document).on('click','.result-bookmark-link',function(e){
            e.preventDefault();
            bookmarkResult(this);
            return false;

        });

        $(".menu").affix({
            offset:0
        });

    };

    iccs.handleTerms=function(terms){

        previousTerms=terms;

        var $stream = $("#stream-terms");
        if(terms ==null || terms == undefined && terms ==''){

            $stream.val('');
            return;
        }

        $stream.val(terms);
    };

    iccs.handleQuery=function(query){

        previousQuery=query;
        $("#query").empty();
        var $lis = query.split(',');
        $.each($lis,function(i,data){
            if(data !=''){
                appendQueryTerm(data);
            }
        });
    };

    iccs.handleStep=function(step){

        $(".clippy").find('li').each(function(index,element){

            $(element).removeClass('on');
            $(element).removeClass('active');

            var liStep = parseInt($(element).attr('data-step'));

            if(liStep == step){
                $(element).addClass('active');
            }else if(liStep < step){
                $(element).addClass('on');
            }
        });
    }

    iccs.setupWordCloud=function(opts){


        var defaultOptions={
            wordcloud:"#wordcloud",
            slider:"#wc-slider",
            word_list:[]
        };

        var o= $.extend(defaultOptions, opts);

        var word_list = o.word_list;

        var wordcloud = $(o.wordcloud);
        var slider = $(o.slider);




        slider.slider({
            min:0,
            max:99,
            range: 'true',
            step: 1,
            change:function(event,ui){

                var that = this;
                $(that).fadeOut('fast');
                //get the max and min weight
                var max_weight = word_list[0].weight;
                var min_weight = word_list[word_list.length-1].weight;
                var diff = max_weight-min_weight;



                var range = min_weight+diff*((100-ui.value)/100);
                var new_word_list = [];
                


//                console.log("%s <-(%s)-> %s => %s",max_weight,diff,min_weight,range);

                for(var i =0; i < word_list.length-1; i ++){

                    var word = word_list[i];
                    if(word.weight <= range){

                        new_word_list.push(
                            {
                                text: word.text,
                                weight:word.weight,
                                html:{"data-id":word.html["data-id"],class:"cloud-links"},
                                link: word.link,
                                afterWordRender:iccs.wordCloudRenderHandler
                            }
                        );
                    }
                }

                wordcloud.fadeOut('fast',function(){
                    wordcloud.html('');
                    wordcloud.show();
                    wordcloud.jQCloud(new_word_list,
                        {
                            width:870,
                            afterCloudRender:  function(a){

                                cloudRenderHandler(a);
                                $(that).fadeIn('fast');

                            }
                        }
                    );
                });

            }
        });

        wordcloud.jQCloud(word_list,{
            width:870,
            animate:false,
            afterCloudRender:  cloudRenderHandler
        });

    };

    var cloudRenderHandler = function(a){

       // var $cloud = $(".twitter");
       var $image = $(".image-results");
       // var $customresults = $(".customize-results");
       var $cloud = $(".cloud-links");
      

        $cloud.mouseenter(function(){
            $(this).addClass('hover');
             // var cw = $image.width();
           // $image.css({
           //     'height': cw + 'px'
           // });
            $(this).find('.btn-cloud').show('fast');
            $(this).find('.btn-tweets').show('fast');
        }).mouseleave(function(){
                $(this).removeClass('hover');
                $(this).find('.btn-cloud').hide();
                $(this).find('.btn-tweets').hide();
                $('.popover').hide();
        });
        
       // $customresults.mouseenter(function(){
        //    $(this).addClass('hover');
          //  $(this).find('.btn-cloud').show('fast');
          //  $(this).find('.btn-tweets').show('fast');
       // }).mouseleave(function(){
         //     $(this).removeClass('hover');
           //     $(this).find('.btn-cloud').hide();
             //   $(this).find('.btn-tweets').hide();
             //   $('.popover').hide();
        //});
            
         $image.mouseenter(function(){
            $(this).addClass('hover');
            $(this).find('.btn-cloud').hide();
            $(this).find('.btn-tweets').hide();
        }).mouseleave(function(){
                $(this).removeClass('hover');             
                $('.popover').hide();
            });


        $('.btn-cloud').bind('click',function(e){
            e.preventDefault();

            var $action = $(this).attr("data-action");

            if($action =='boost'){
                var $term= $(this).parent().parent().text().trim();
                $.publish("term.added",[$term]);
            }
        });
        
                
        $('.btn-tweets').clickover({
            animation:true,
            html:true,
            placement:function(tip,element){

                var $cloud = $("#wordcloud");

                /** we need to get the location of the #wordcloud parent span*/
                var offset = $(element).parent().parent().position();


                var popupWidth=300;
                var popupHeight=190;

                var height = $cloud.innerHeight();
                var width = $cloud.innerWidth();

                console.log("Dimension %sx%s",width,height);
                console.log("Element offset",offset);


                var vert="bottom";
                if(offset.top+ popupHeight >= height){
                    vert = "top";
                }


                var hor="Right";
                if(offset.left+ popupWidth >= width){
                    hor="Left";
                }
                return vert+hor;


            },
            'content':function(){

                $word = $(this).parent().parent();

                var id =$word.attr("data-id");
                var source=$word.attr("data-source");

                var $ret = $('<div class="popover-tweet-content"/>');
                $ret.html($("#term-tweets-"+id).html());

                return $ret;

            }
        });
        $('#imagelink').click(function() {
            //   doc=document.getElementById("href");
            //location.href = $('#imagelink').attr("href"); //It is correct!
            // window.location.href = this.id + '.html'; //It is correct!
            //  window.open($('#imagelink').attr("href"), '_blank');
            //  $.winOpen($('#imagelink').attr("href"), "windowName", { fullscreen: "no", height: "600px", toolbar: 1, width: 600 });
            // var URL = $.myURL("index", $(this).attr("href"));
            //  window.open(URL,'_blank','',''); 
            window.location = $.myURL("index", $(this).attr("href"));

        });


    }

    iccs.wordCloudRenderHandler= function(){


        $(this).prepend('<div class="toolbar clearfix">' +
            '<a href="#" class="btn btn-mini btn-success btn-cloud" data-action="boost" ><i class="icon-plus icon-white"></i></a> '+
            '<a href="#" class="btn btn-mini btn-info btn-tweets" data-action="view-tweets" ><i class="icon-search icon-white"></i></a>'+
            '</div>'
        );

    };


    iccs.bindHistory = function(){

        History.Adapter.bind(window,'statechange',function(){ // Note: We are using statechange instead of popstate


            var State = History.getState();

            if(State.data.timestamp in timestamps) {
                // Deleting the unique timestamp associated with the state
                delete timestamps[State.data.timestamp];
            }
            else{
                // Manage Back/Forward button here
                log('Back or Forward');

                //just reload the page and let the magic happen
                window.location = State.url;
            }
        });

    }

}( window.iccs = window.iccs|| {}, jQuery ));



$