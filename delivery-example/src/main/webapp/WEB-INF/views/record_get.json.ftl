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
${callback}([
<#else>
[
</#if>
  <#list records as record>
  {
    "pid" : "${record.pid?js_string}",
    "title" : "${record.title?js_string}",
    "publicationStatus": "${record.publicationStatus}",
    "restriction": "${record.restriction}",
    "openForReproduction": ${record.openForReproduction?c},
     <#if record.copyright??>
      "copyright": "${record.copyright?js_string}",
     </#if>

    "holdings" : [
      <#list record.holdings as h>
      {
        <#if h.direction??>
        "direction" : "${h.direction?js_string}",
        </#if>
        <#if h.floor??>
        "floor" : ${h.floor?c},
        </#if>
        <#if h.cabinet??>
        "cabinet" : "${h.cabinet?js_string}",
        </#if>
        <#if h.shelf??>
        "shelf" : "${h.shelf?js_string}",
        </#if>
        "signature" : "${h.signature?js_string}",
        "status" : "${h.status}",
        "usageRestriction" : "${h.usageRestriction}"
      }
      <#if h_has_next>,</#if>
      </#list>
    ],

    "reservedChilds" : [
      <#list reservedChilds[record.pid] as reservedChildRecord>
        <#assign childHolding = reservedChildRecord.holdings?first/>
        "${childHolding.signature?js_string}"<#if reservedChildRecord_has_next>,</#if>
      </#list>
   ]
  }<#if record_has_next>,</#if>
  </#list>
]
<#if callback??>
);
</#if>
