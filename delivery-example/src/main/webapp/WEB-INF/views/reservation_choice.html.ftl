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

<#include "base.ftl"/>
<#include "form.ftl" />

<#-- Build the title -->
<#assign title>
<@_ "reservation.create" "Create Reservation"/>
</#assign>

<#-- Build the page -->
<@userbase title>
<h1><@_ "reservation.permission" "Restricted Items:" /></h1>
<@_ "reservation.permissionMsg" "The following items are restricted and require a request for permission to be filed before reserving:"/>
<ul>
  <#assign pids = ""/>
  <#assign hasOpen = false>
  <#list holdingReservations as hr>
    <#assign h = hr.holding>
    <#assign info = h.record.externalInfo>
    <#if h.record.restriction == "RESTRICTED">
    <#assign pids = pids + h.record.pid?url+prop_pidSeparator>
    <li>${h.record.toString()?html}</li>
    <#elseif h.record.restriction == "OPEN">
    <#assign hasOpen = true>
    </#if>
  </#list>
</ul>

<form action="${rc.contextPath}/permission/createform/${pids?url}" method="GET" target="_blank">
  <input type="submit" value="<@_ "reservation.reqPermission" "Request Permission"/>"/>
</form>

<#if hasOpen>
<h1><@_ "reservation.allowed" "Open Items:" /></h1>
<@_ "reservation.openMsg" "The following items are freely available and can be reserved right now:"/>
<ul>
  <#assign pids = {}/>
  <#list holdingReservations as hr>
    <#assign h = hr.holding>
    <#if h.record.restriction == "OPEN">
    <#assign info = h.record.externalInfo>
    <#if pids[h.record.pid]??>
         <#assign pids = pids + {h.record.pid : (pids[h.record.pid] +  prop_holdingSeparator + h.signature?url) }>
    <#else>
        <#assign pids = pids + {h.record.pid :  h.signature?url}>
    </#if>
    <li>${h.record.toString()?html}</li>
    </#if>
  </#list>
</ul>
<#assign pidParam = "">
<#list pids?keys as k>
<#assign pidParam = pidParam + k + prop_holdingSeparator + pids[k] + prop_pidSeparator>
</#list>

<form action="${rc.contextPath}/reservation/createform/${pidParam?url}" method="GET" target="_blank">
  <input type="submit" value="<@_ "reservation.create" "Create Reservation"/>"/>
</form>

</#if>
</@userbase>
