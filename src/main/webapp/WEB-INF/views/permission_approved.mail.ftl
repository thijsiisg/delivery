<#include "mail.ftl">
<#assign granted_true>
<@_ "recordPermission.granted.true" "Granted"/>
</#assign>
<#assign granted_false>
<@_ "recordPermission.granted.false" "Denied"/>
</#assign>
<@mail to="${permission.name}">
<@_ "permissionMail.approvedMessage" "Your permission request has been (partially) approved. You can create a reservation with the link below."/>


${prop_urlSelf}/reservation/createform/<#list permission.recordPermissions as rp><#if rp.granted>${rp.record.pid?url}<#if rp_has_next>,</#if></#if></#list>?code=${permission.code}&locale=${locale}

<@_ "permissionMail.codeValidityFrom" "Code is valid from"/> ${permission.dateFrom?string(prop_dateFormat)} <@_ "permissionMail.codeValidityTo" "until"/> ${permission.dateTo?string(prop_dateFormat)}.

--- <@_ "permission.recordPermissions" "Permissions per Record"/> ---
<#list permission.recordPermissions as rp>
<#assign info = rp.record.externalInfo>
<@_ "record.title" "Title"/>: ${rp.record.title} <#if info.author??>/ ${info.author}</#if>
<@_ "recordPermission.granted" "Permission"/>: ${rp.granted?string(granted_true,granted_false)}
<#if rp.motivation??><@_ "recordPermission.motivation" "Motivation"/>: ${rp.motivation}</#if>
<#if rp_has_next>--</#if>
</#list>

</@mail>