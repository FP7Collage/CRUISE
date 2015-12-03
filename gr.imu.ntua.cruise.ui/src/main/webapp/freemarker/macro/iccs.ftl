<#import "/spring.ftl" as spring/>
<#macro loader id message="">
    <span id="${id}-loader" style="display:none">
        <img src="<@spring.url "/static/images/ajax-loader.gif"/>"/>
        ${message}
    </span>
</#macro>
<#macro field name>
<div class="form-field">
    <div class="form-label">
        <@spring.message "${name}" />
    </div>
    <div class="form-input">
        <@spring.bind "${name}" />
        <input type="text" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
    </div>
    <div class="form-errors">
        <#list spring.status.errorMessages as error> <b>${error}</b> <br> </#list>
    </div>
</div>
</#macro>

<#macro area name>
<div class="form-field">
    <div class="form-label">
        <@spring.message "${name}" />
    </div>
    <div class="form-input">
        <@spring.bind "${name}" />
        <textarea rows="3" name="${spring.status.expression}">${spring.status.value?default("")}</textarea>
    </div>
    <div class="form-errors">
        <#list spring.status.errorMessages as error> <b>${error}</b> <br> </#list>
    </div>
</div>
</#macro>


<#macro range name>
<div class="form-field">
    <div class="form-label"><@spring.message "${name}" /></div>
    <div class="form-input range clearfix">
        <@spring.bind "${name}" />
        <label class="pull-left">
            <span>1</span>
            <input type="radio" value="1" name="${spring.status.expression}" name="${spring.status.expression}" <#if spring.stringStatusValue == "1">checked</#if> />
        </label>
        <label class="pull-left">
            <span>2</span>
            <input type="radio" value="2" name="${spring.status.expression}" name="${spring.status.expression}" <#if spring.stringStatusValue == "2">checked</#if> />
        </label>
        <label class="pull-left">
            <span>3</span>
            <input type="radio" value="3" name="${spring.status.expression}" name="${spring.status.expression}" <#if spring.stringStatusValue == "3">checked</#if> />
        </label>
        <label class="pull-left">
            <span>4</span>
            <input type="radio" value="4" name="${spring.status.expression}" name="${spring.status.expression}" <#if spring.stringStatusValue == "4">checked</#if> />
        </label>
        <label class="pull-left">
            <span>5</span>
            <input type="radio" value="5" name="${spring.status.expression}" name="${spring.status.expression}" <#if spring.stringStatusValue == "5">checked</#if> />
        </label>
    </div>
    <div class="form-errors">
        <#list spring.status.errorMessages as error> <b>${error}</b> <br> </#list>
    </div>
</div>
</#macro>


<#macro password name>
<div class="form-field">
    <div class="form-label">
        <@spring.message "${name}" />
    </div>
    <div class="form-input">
        <@spring.bind "${name}" />
        <input type="password" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
    </div>
    <div class="form-errors">
        <#list spring.status.errorMessages as error> <b>${error}</b> <br> </#list>
    </div>
</div>
</#macro>

<#macro textField name>
    <div class="form-field">
    <div class="form-label">
        <@spring.message "${name}" />
    </div>
    <div class="form-input">
        <@spring.bind "${name}" />
        <textarea cols="40" rows="5" type="text" name="${spring.status.expression}" value="${spring.status.value?default("")}" ></textarea>
    </div>
    <div class="form-errors">
        <#list spring.status.errorMessages as error> <b>${error}</b> <br> </#list>
    </div>
    </div>

</#macro>

