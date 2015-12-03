<h1>Flickr!</h1>
<div class="images-scroll v-scrollable">
<#if images??>
    <#list images as image>
        <a href="${image.link}" target="_blank">
            <img class="image-item" src="${image.thumbnail}" alt="${image.title}" rel="#images-nav">
        </a>
    </#list>
</#if>
</div>

<#--<div id="images-nav">-->
    <#--<div class="scrollable">-->
        <#--<a class="prev browse left"></a>-->
        <#--<div class="items">-->
        <#--<#if images??>-->
            <#--<#list images as image>-->
                <#--<div>-->
                    <#--<img src="${image.link}" alt="${image.title}">-->
                <#--</div>-->
            <#--</#list>-->
            <#--</div>-->
            <#--<a class="next browse right"></a>-->
        <#--</#if>-->
    <#--</div>-->
<#--</div>-->

<script type="text/javascript">
//    $(".scrollable").scrollable();
//    $(".image-item").overlay();
</script>
