<h1>${searchEngine}</h1>
<div id="search-diversified-${searchEngine}" class="search-diversified-results ">
    <#if diversified??>
        <ul class="results unstyled">
            <#list diversified as result>
                <li>
                    <div class="result-item">
                        <div class="result-bookmark"><a href="${result.link}" data-source="${searchEngine}" class="result-bookmark-link"><i class="icon icon-bookmark"></i></a></div>
                        <div class="result-title"><a href="${result.link}" target="_blank" title="${result.link}">${result.title}</a></div>
                        <div class="result-snippet">${result.snippet}</div>
                    <div class="result-item-rating">
                        <a class="result-rating-btn novel" href="${result.link}" data-title="${result.title}" data-source="${searchEngine}" data-rating="novel" >Novel</a>
                        <a class="result-rating-btn valuable" href="${result.link}" data-title="${result.title}" data-source="${searchEngine}" data-rating="valuable" >Valuable</a>
                        <a class="result-rating-btn unexpected" href="${result.link}" data-title="${result.title}" data-source="${searchEngine}" data-rating="unexpected" >Unexpected</a>
                    </div>
                    </div>
                </li>
            </#list>
        </ul>
    </#if>
</div>

