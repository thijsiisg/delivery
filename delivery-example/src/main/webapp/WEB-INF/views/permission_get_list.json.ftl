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

<#if callback??>
${callback}(
</#if>
[
<#list pageListHolder.pageList as permission>
  {
    "visitor_name": "${permission.name?js_string}",
    "visitor_email": "${permission.email?js_string}",
    "address": "${permission.address?js_string}",
    "research_organization": "${permission.researchOrganization?js_string}",
    "research_subject": "${permission.researchSubject?js_string}",
    "explanation": "${permission.explanation?js_string}",
    "items" :
      [
        <#list permission.recordPermissions as rp>
        ["${rp.record.pid}", ${rp.granted?string}<#if rp.motivation??>, ${rp.motivation?js_string}</#if>]<#if rp_has_next>,</#if>
        </#list>
      ]
  }
  <#if permission_has_next>,</#if>
</#list>
]
<#if callback??>
);
</#if>
