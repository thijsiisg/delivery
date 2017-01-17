<#--

    Copyright (C) 2013 International Institute of Social History

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->

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
<@_ "record.title" "Title"/>: ${rp.record.title} <#if info.author??>/ ${info.author}</#if><#if rp.record.parent??> - ${rp.record.holdings[0].signature}</#if>
<@_ "recordPermission.granted" "Permission"/>: ${rp.granted?string(granted_true,granted_false)}
<#if rp.motivation??><@_ "recordPermission.motivation" "Motivation"/>: ${rp.motivation}</#if>
<#if rp_has_next>--</#if>
</#list>
</@mail>
