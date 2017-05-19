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

<#import "spring.ftl" as spring/>
<#include "base.ftl">
<#include "localization.ftl">
<#assign title>
<@_ "reservation.success" "Reservation placed successfully."/>
</#assign>
<@userbase title true>
<section>
  <heading>
    <h1>${title}</h1>
  </heading>

  <section>


    <ul class="items">
      <#list reservation.holdingReservations as hr>
        <#assign h = hr.holding>
        <#assign info = h.record.externalInfo>
        <li>${hr.toString()?html}</li>
      </#list>
    </ul>

  </section>

  <#if reservation.date?date?string == .now?date?string>
    <p><@_html "reservation.successMsg" "Requested documents will be in the reading room within <strong>30 minutes</strong>." /></p>
  <#else>
    <p><@_html "reservation.successFutureMsg" "Requested documents will be in the reading room on the day of your visit as of <strong>9.30 am</strong>." /></p>
  </#if>

  <#if error?? >
      <p class="error"><@_ "reservation.error."+error error /></p>
  </#if>

  <p><@_ "reservation.backToSearch" "Close this tab and return to Search for new requests" /></p>
</section>

</@userbase>
