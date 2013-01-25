<#include "mail.ftl">
<@mail to="${permission.name}">
<@_ "permissionMail.confirmationMessage" "With this mail we confirm your permission request has been successfully received. We will notify you when your request has either been approved or refused."/>


--- <@_ "permission.dateFrom" "Date From"/> ---
${permission.dateFrom?string(prop_dateFormat)}

--- <@_ "permission.dateTo" "Date To"/> ---
${permission.dateTo?string(prop_dateFormat)}

--- <@_ "reservation.records" "Records"/> ---
<#list permission.recordPermissions as rp>
<#assign info = rp.record.externalInfo>
* ${rp.record.title} <#if info.author??>/ ${info.author}</#if>
</#list>
</@mail>