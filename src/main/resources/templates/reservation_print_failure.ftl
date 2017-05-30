<#include "base.ftl"/>
<#include "form.ftl" />

<#-- Build the title -->
<#assign title>
<@_ "reservationPrintFailure.title" "Printing (partially) failed"/>
</#assign>

<#-- Build the page -->
<@base "${title}">
<h1>${title}</h1>

<p><@_ "reservationPrintFailure.message" " Failed to print, please try again."/></p>
<p><a href="<@paramUrl {} "/reservation/"/>"><@_ "reservationPrintFailure.back" "Back"/></a></p>
</@base>
