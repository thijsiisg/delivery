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
<@mail reservation.visitorName>
<@_ "reservationMail.confirmationMessage" "With this mail we confirm your reservation has been successfully received. You can come to the reading room at the date given below."/>


--- <@_ "reservation.date" "Date"/> ---
${reservation.date?string(prop_dateFormat)}

<#--<#if reservation.queueNo??>
--- <@_ "reservation.queueNo" "Queue Number"/> ---
${reservation.queueNo?c}
</#if>-->

--- <@_ "reservation.records" "Records"/> ---
<#list reservation.holdingReservations as hr>
 <#assign h = hr.holding>
 <#assign info = h.record.externalInfo>
* ${h.record.title} - ${h.signature} <#if info.author??>/ ${info.author} </#if><#if hr.comment??>- ${hr.comment}</#if>
</#list>
</@mail>