<div id="wordcloud"></div>

<div style="display: none">
<#list results as terms>
    <div id="term-tweets-${terms.id}" class="term-tweets">
        <ul>
            <#list terms.data as content>
                <#if content??>
                    <li>
                        <span class="tweet-content">
                            ${content}
                        </span>
                    </li>
                </#if>
            </#list>
        </ul>
    </div>
</#list>
</div>

<script type="text/javascript">

    var word_list=[
    <#list results as terms>
        {
            text: "${terms.term}",
            weight:"${terms.frequency}",
            html:{class:"cloud-links","data-id":"${terms.id}"},
            afterWordRender:function(){

                $(this).prepend('<div class="toolbar clearfix">' +
                        '<a href="#" class="btn btn-mini btn-success btn-cloud" data-action="boost" ><i class="icon-plus icon-white"></i></a> '+
                        '<a href="#" class="btn btn-mini btn-info btn-tweets" data-action="view-tweets" ><i class="icon-search icon-white"></i></a>'+
                        '</div>'
                );

                $(this).find('.btn-cloud').fadeOut('fast');
                $(this).find('.btn-tweets').fadeOut('fast');

            }

        },
    </#list>
    ];


    if(word_list.length <= 0){
        $('#wordcloud').addClass("loading");
    }

    $("#wordcloud").jQCloud(word_list,{

        afterCloudRender:function(){

            var current=null;
            var $cloud = $(".cloud-links");


//            $cloud.bind("click",function(e){
//
//                var id =$(this).attr("data-id");
//
//                var $scroll = $("#scroll-content");
//                $scroll.css({"display":"none"});
//                $scroll.fadeOut("fast",function(){
//                    $(this).html($("#term-tweets-"+id).html());
//                    $(this).fadeIn();
//                    current=id;
//                });
//
//
//
//           });


            $cloud.mouseenter(function(){
                $(this).addClass('hover');
                $(this).find('.btn-cloud').show('fast');
                $(this).find('.btn-tweets').show('fast');
            }).mouseleave(function(){
                $(this).removeClass('hover');
                $(this).find('.btn-cloud').hide();
                $(this).find('.btn-tweets').hide();
                $('.popover').hide();


            });

            $('.btn-cloud').bind('click',function(e){
                e.preventDefault();

                var $action = $(this).attr("data-action");

                if($action =='boost'){

//                    //find the li by text
//                    $('.ignore > .term').each(function(data){
//                    });

                    var $term= $(this).parent().parent().text().trim();
                    var $clicked=$(".clicked");
                    $($clicked).append($('<li><a href="#" class="filterterm">&times;</a> <span class="term">'+$term+'</span></li>'))


                    $.publish("term.added");
                }
            });

            $('.btn-tweets').clickover({
               animation:true,
               html:true,
               placement:function(tip,element){

                   var $cloud = $("#wordcloud");
                   var offset = $(element).offset();

                   height = $cloud.innerHeight()-190;// carefull here hardcoded values may not always be correct;
                   width = $cloud.innerWidth()-300;// carefull here hardcoded values may not always be correct;

                   vert = 0.5 * height - offset.top;
                   vertPlacement = vert > 0 ? 'bottom' : 'top';
                   horiz = 0.5 * width - offset.left;
                   horizPlacement = horiz > 0 ? 'right' : 'left';
                   placement = Math.abs(horiz) > Math.abs(vert) ?  horizPlacement : vertPlacement;

                   return placement;


               },
                'content':function(){

                    $word = $(this).parent().parent();

                    var id =$word.attr("data-id");

                    var $ret = $('<div class="popover-tweet-content"/>');
                    $ret.html($("#term-tweets-"+id).html());

                    return $ret;
//                    var id =$(this).attr("data-id");
//
//                    var $scroll = $("#scroll-content");
//                    $scroll.css({"display":"none"});
//                    $scroll.fadeOut("fast",function(){
//                        $(this).html($("#term-tweets-"+id).html());
//                        $(this).fadeIn();
//                        current=id;
//                    });
//
//                    return $('#div-inspired').html();
                }
            });

        }


    });

</script>