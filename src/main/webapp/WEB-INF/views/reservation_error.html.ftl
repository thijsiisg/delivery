<#include "base.ftl"/>
<#include "form.ftl"/>

<@userbase "Error">
<h1><@_ "reservation.error" "An error has occurred creating a reservation:"/></h1>
<p><@_ "reservation.error."+error error /></p>

<#if error == "availability">
<#assign email>
<@_ "iisg.email" ""/>
</#assign>
<p><a href="mailto:${email}">${email}</a></p>
</#if>
</@userbase>
