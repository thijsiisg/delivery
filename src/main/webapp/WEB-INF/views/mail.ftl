<#ftl strip_whitespace=true>
<#macro _ ident msg>${msgResolver.getMessage(ident, msg)}</#macro>

<#macro mail to>
<@_ "mail.dear" "Dear"/> ${to},

<#nested>

----
<@_ "mail.signature" "Kind regards"/>,

<@_ "mail.signedBy" "International Institute of Social History"/>

<@_ "mail.signedByDepartment" "Reading Room"/>


<@_ "mail.postalAddress" ""/>

<@_ "mail.visitingAddress" ""/>

<@_ "mail.email" ""/>: <@_ "iisg.email" ""/>
<@_ "mail.website" ""/>: <@_ "iisg.website" ""/>
</#macro>