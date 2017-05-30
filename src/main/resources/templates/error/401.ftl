<#include "../base.ftl"/>
<#include "../form.ftl"/>

<@userbase "Error">
<h1><@_ "delivery.error" "Oops! An error has occurred."/></h1>
    <@_ "delivery.unauthorized" "You are not authorized to visit this page!" />
    <#assign email>
        <@_ "iisg.email" ""/>
    </#assign>
<p><a href="mailto:${email}">${email}</a></p>
</@userbase>
