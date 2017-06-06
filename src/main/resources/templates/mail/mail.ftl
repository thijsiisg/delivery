<#ftl strip_whitespace=true>

<#macro _ ident msg>${msgResolver.getMessage(ident, msg)}</#macro>

<#macro mail to>
<@_ "mail.dear" "Dear"/> ${to},

<#nested>

----
<@_ "mail.signature" "Kind regards"/>,
<@_ "mail.signedByDepartment" "Reading Room"/> <@_ "mail.signedBy" "International Institute of Social History"/>

<@_ "mail.postalAddress" ""/>

<@_ "mail.visitingAddress" ""/>

<@_ "mail.email" ""/>: <@_ "iisg.email" ""/>
<@_ "mail.website" ""/>: <@_ "iisg.website" ""/>
</#macro>

<#macro mailRaedingRoom>
    <#nested>

----
    <@_ "mail.auto" "This is an email automatically sent by Delivery."/>
</#macro>

<#macro holdingPrice price completePrice materialType noPages=1>
    ${completePrice?string("0.00")} EUR <#t/>

    <#if noPages gt 1>
        <#if materialType == "BOOK">
            (<@_ "price.page" "Price per page"/>: ${price?string("0.00")} EUR, <#t/>
            <@_ "no.pages" "Number of pages"/>: ${noPages?html}) <#t/>
        <#else>
            (<@_ "price.copy" "Price per copy"/>: ${price?string("0.00")} EUR, <#t/>
            <@_ "no.copies" "Number of copies"/>: ${noPages?html}) <#t/>
        </#if>
    </#if>
</#macro>
