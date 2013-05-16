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
<@userbase title>
<section>
  <heading>
    <h1>${title}</h1>
  </heading>

  <section>


    <ul class="items">
      <#list reservation.holdingReservations as hr>
        <#assign h = hr.holding>
        <#assign info = h.record.externalInfo>
        <li>${h.record.title?html} <#if info.author??>/ ${info.author}</#if>
            - ${h.signature?html}<#if hr.comment??> - ${hr.comment}</#if></li>
      </#list>
    </ul>

  </section>

  <p><@_ "reservation.successMsg" "The requested items will be available for your visit to the reading room."/></p>
  <p><a href="${prop_urlSearch}"><@_ "reservation.backToSearch" "Back to Search" /></a></p>

  <#--<#if reservation.queueNo??>
    <@_ "reservation.queueNoMsg" "Your queue number is: "/> <span class="queueNo">${reservation.queueNo?c}</span>
  </#if>-->
</section>

</@userbase>
