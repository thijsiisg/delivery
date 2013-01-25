<#include "base.ftl"/>
<#include "form.ftl" />

<#-- Build the title -->
<#assign title>
<@_ "userLogout.title" "You have been successfully logged out from Deliverance."/>
</#assign>

<#-- Build the page -->
<@base "${title}">
<h1>${title}</h1>
<p><@_ "userLogout.casLogout" "You are still logged in at the Central Authentication Service (CAS). If you also want to logout from CAS, please use the link below."/></p>

<a href="${prop_casUrl}/logout"><@_ "userLogout.casLogoutLink" "Logout from CAS"/></a>
</@base>
