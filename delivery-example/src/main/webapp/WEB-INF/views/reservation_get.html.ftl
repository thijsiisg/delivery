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
<@_ "reservationSingle.title" "Reservation"/> ${reservation.id?c}
</#assign>

<#assign yes>
    <@_ "yes" "Yes" />
</#assign>
<#assign no>
    <@_ "no" "No" />
</#assign>

<#-- Build the page -->
<@base "${title}">
<h1>${title}</h1>

<ul class="reservationDetails">
    <li><span><@_ "reservation.visitorName" "Name"/></span> ${reservation.visitorName?html}</li>
    <li><span><@_ "reservation.visitorEmail" "E-mail"/></span> ${reservation.visitorEmail?html}</li>
    <li><span><@_ "reservation.date" "Date"/></span> ${reservation.date?string(prop_dateFormat)}</li>
    <#if reservation.returnDate??>
    <li><span><@_ "reservation.returnDate" "Return Date"/></span>${reservation.returnDate?string(prop_dateFormat)}</li>
    </#if>
    <li><span><@_ "reservation.status" "Status"/></span> <@_ "reservation.statusType.${reservation.status}" reservation.status?string /></li>
    <#--<#if reservation.queueNo??>
    <li><span><@_ "reservation.queueNo" "Queue No"/></span> ${reservation.queueNo?c}</li>
    </#if>-->


    <#if reservation.comment??>
    <li><span><@_ "reservation.comment" "Comment"/></span>${reservation.comment?html}</li>
    </#if>
    
    <#if  _sec.ifAllGranted("ROLE_RESERVATION_CREATE")>
    <li><br/><a href="${rc.contextPath}/reservation/masscreateform?fromReservationId=${reservation.id?c}">
    <@_ "reservationSingle.newReservation"/></a></li>
    </#if>

    <table class="records">
    <caption><@_ "reservation.holdings" "Holdings"/></caption>
    <thead>
      <tr>
        <th>ID</th>
        <th><@_ "record.title" "Title"/></th>
        <th>PID</th>
        <th><@_ "reservation.printed" "Printed"/></th>
        <th><@_ "holding.status" "Status"/></th>
      </tr>
    </thead>
    <tbody>
      <#list reservation.holdingReservations as hr>
      <#assign h = hr.holding>
      <tr>
        <td>${h.id?c}</td>
        <td>${h.record.title?html} - ${h.signature?html}<#if hr.comment??> - ${hr.comment}</#if></td>
        <td><#if _sec.ifAllGranted("ROLE_RECORD_MODIFY")>
            <a target="_blank" href="${rc.contextPath}/record/editform/${h.record.pid?url}">${h.record.pid?html}</a>
            <#else>
            ${h.record.pid?html}
            </#if>
        </td>
        <td>${hr.printed?string(yes, no)}</td>
        <td><@holdingStatus holdingActiveRequests reservation h/></td>
      </tr>
      </#list>
    </tbody>
  </table>
  </ul>
</@base>
