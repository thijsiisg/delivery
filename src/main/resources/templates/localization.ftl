<#ftl strip_whitespace=true>
<#import "spring.ftl" as spring/>

<#macro _ ident msg=""><@spring.messageText ident msg/></#macro>

<#macro _html ident msg="">
    ${springMacroRequestContext.setDefaultHtmlEscape(false)}
    <@spring.messageText ident msg/>
    ${springMacroRequestContext.setDefaultHtmlEscape(true)}
</#macro>