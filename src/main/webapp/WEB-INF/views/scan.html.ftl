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
<#include "form.ftl"/>

<#assign title><@_ "scan.title" "Scan Items"/></#assign>

<@base title>
<h1>${title}</h1>
<@form_plain "" "scan">
  <label for="scanid">
    <@_ "scan.id" "ID:"/>
  </label>
  <input type="text" id="scanid" name="id" value="" />
  <@submit "scan"/>

  <#if error??>
    <ul class="errors">
      <li>
        <@_ "scan.error."+error error />.
      </li>
    </ul>
  <#elseif holding??>
  <ul class="messages">
    <li>
    &quot;${holding.record.title?html} - ${holding.signature?html}&quot;
    <@_ "scan.changedFrom" "has changed from status" />
    &quot;<@_ "holding.statusType.${oldStatus?string}" oldStatus?string/>&quot;
    <@_ "scan.changedTo" "to" />
    &quot;<@_ "holding.statusType.${holding.status?string}" holding.status?string/>&quot;.
    </li>
  </ul>
  <h3><@_ "scan.assocReservation" "Reservation details"/></h3>
  <ul class="reservationDetails">
    <li><span><@_ "reservation.visitorName" "Name"/></span> ${reservation.visitorName?html}</li>
    <li><span><@_ "reservation.visitorEmail" "E-mail"/></span> ${reservation.visitorEmail?html}</li>
    <li><span><@_ "reservation.date" "Date"/></span> ${reservation.date?string(prop_dateFormat)}</li>
    <li><span><@_ "reservation.status" "Status"/></span> <@_ "reservation.statusType.${reservation.status?string}" reservation.status?string/></li>
    <#if reservation.queueNo??>
    <li><span><@_ "reservation.queueNo" "Queue Number"/></span> ${reservation.queueNo?c}</li>
    </#if>
  </ul>
  <table class="records">
    <caption><@_ "reservation.holdings" "Holdings"/></caption>
    <thead>
      <tr>
        <th>ID</th>
        <th><@_ "record.title" "Titel"/></th>
        <th><@_ "holding.status" "Status"/></th>
      </tr>
    </thead>
    <tbody>
      <#list reservation.holdingReservations as hr>
      <#assign h = hr.holding>
      <tr>
        <td>${h.id?c}</td>
        <td>${h.record.title?html} - ${h.signature?html}<#if hr.comment??> - ${hr.comment}</#if></td>
        <td><@_ "holding.statusType.${h.status?string}" h.status?string/></td>
      </tr>
      </#list>
    </tbody>
  </table>
  </#if>
</@form_plain>
</@base>
