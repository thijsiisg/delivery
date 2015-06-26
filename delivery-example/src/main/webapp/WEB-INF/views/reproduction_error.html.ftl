<#include "base.ftl"/>
<#include "form.ftl"/>

<@userbase "Error">
<h1><@_ "reproduction.error" "An error has occurred creating a reproduction:"/></h1>
<p><@_ "reproduction.error."+error error /></p>
</@userbase>
