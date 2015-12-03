<h1>${searchEngine}</h1>
<div id="search-diversified-${searchEngine}" class="search-diversified-results v-scrollable">
    <#if diversified??>
        <ul class="results unstyled">
            <#list diversified as result>
                <li>
                    <div class="result-item">
                        <div class="result-bookmark"><a href="${result.link}" data-source="${searchEngine}" class="result-bookmark-link"><i class="icon icon-bookmark"></i></a></div>
                        <div class="result-title"><a href="${result.link}" target="_blank" title="${result.link}">${result.title}</a></div>
                        <div class="result-snippet">${result.snippet}</div>
                    </div>
                </li>
            </#list>
        </ul>
    </#if>
</div>

