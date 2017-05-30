<#include "mail.ftl">
<#assign granted_true>
<@_ "recordPermission.granted.true" "Granted"/>
</#assign>
<#assign granted_false>
<@_ "recordPermission.granted.false" "Denied"/>
</#assign>
<@mail to="${permission.name}">
<@_ "permissionMail.approvedMessage" "Your permission request has been (partially) approved. You can create a reservation with the link below."/>

${delivery.urlSelf}/reservation/createform/<#list permission.recordPermissions as rp><#if rp.granted><#if rp.originalRequestPids??>${rp.originalRequestPids?url}<#else>${rp.record.pid?url}</#if><#if rp_has_next>,</#if></#if></#list>?codes=${permission.code}&locale=${locale}

<@_ "permissionMail.code" "Code"/>: ${permission.code}

--- <@_ "permission.recordPermissions" "Permissions per Record"/> ---
<#list permission.recordPermissions as rp>
<#assign info = rp.record.externalInfo>
<@_ "record.title" "Title"/>: ${rp.record.toString()}
<@_ "recordPermission.granted" "Permission"/>: ${rp.granted?string(granted_true,granted_false)}
<#if rp.motivation??><@_ "recordPermission.motivation" "Motivation"/>: ${rp.motivation}</#if>
<#if rp_has_next>--</#if>
</#list>

</@mail>
