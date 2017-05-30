<#include "base.ftl"/>
<#include "form.ftl" />

<#-- Build the title -->
<#assign title>
<@_ "userLogout.title" "You have been successfully logged out from Delivery."/>
</#assign>

<#-- Build the page -->
<@base "${title}">
<h1>${title}</h1>
</@base>
