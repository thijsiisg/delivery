<#include "mail.ftl">
<#assign granted_true>
<@_ "recordPermission.granted.true" "Granted"/>
</#assign>
<#assign granted_false>
<@_ "recordPermission.granted.false" "Denied"/>
</#assign>
<@mail to="${permission.name}">
<@_ "permissionMail.refusedMessage" "We are sorry, but the permission request has been refused by the owner of the archive."/>


--- <@_ "permission.recordPermissions" "Permissions per Record"/> ---
<#list permission.recordPermissions as rp>
<#assign info = rp.record.externalInfo>
<@_ "record.title" "Title"/>: ${rp.record.title} <#if info.author??>/ ${info.author}</#if>
<@_ "recordPermission.granted" "Permission"/>: ${rp.granted?string(granted_true,granted_false)}
<#if rp.motivation??><@_ "recordPermission.motivation" "Motivation"/>: ${rp.motivation}</#if>
<#if rp_has_next>--</#if>
</#list>
</@mail>