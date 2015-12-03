<div id="wordcloud-zoom" ><div id="wc-slider"></div></div>
<div id="wordcloud"></div>

<div style="display: none">
<#list results as terms>
    <div id="term-tweets-${terms.id}" data-id="${terms.id}" data-source="${terms.source}" class="term-tweets">
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
            html:{"data-id":"${terms.id}",class:"cloud-links ${terms.source}"},
            afterWordRender:iccs.wordCloudRenderHandler
        },
    </#list>
    ];

    iccs.setupWordCloud({
        word_list:word_list
    });

</script>