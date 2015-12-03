<h3>Other tweets and links you may also find inspiring</h3>
<div id="recommended" class="search-diversified-results v-scrollable">
    <#if recommendedinfo??>
        <ul class="results unstyled">
            <#list recommendedinfo as result>
                <li>
                    <div class="result-item">
                       
                        <div class="result-snippet">${result}</div>
                    </div>
                </li>
            </#list>
        </ul>
    </#if>
</div>

