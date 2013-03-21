<#include "base.ftl"/>
<#include "form.ftl" />

<#-- Build the title -->
<#assign title>
<@_ "reservationSingle.title" "Reservation"/> ${reservation.id?int}
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
    <li><span><@_ "reservation.queueNo" "Queue No"/></span> ${reservation.queueNo?html}</li>
    </#if>-->


    <#assign yes>
    <@_ "yes" "Yes" />
    </#assign>
    <#assign no>
    <@_ "no" "No" />
    </#assign>
    <li><span><@_ "reservation.printed" "Printed"/></span> ${reservation.printed?string(yes, no)}</li>

    <#if reservation.comment??>
    <li><span><@_ "reservation.comment" "Comment"/></span>${reservation.comment?html}</li>
    </#if>
    
    <#if  _sec.ifAllGranted("ROLE_RESERVATION_CREATE")>
    <li><br/><a href="${rc.contextPath}/reservation/masscreateform?fromReservationId=${reservation.id?int}">
    <@_ "reservationSingle.newReservation"/></a></li>
    </#if>

    <table class="records">
    <caption><@_ "reservation.holdings" "Holdings"/></caption>
    <thead>
      <tr>
        <th>ID</th>
        <th><@_ "record.title" "Title"/></th>
        <th>PID</th>
        <th><@_ "holding.status" "Status"/></th>
      </tr>
    </thead>
    <tbody>
      <#list reservation.holdingReservations as hr>
      <#assign h = hr.holding>
      <tr>
        <td>${h.id?html}</td>
        <td>${h.record.title?html} - ${h.signature?html}<#if hr.comment??> - ${hr.comment}</#if></td>
        <td><#if _sec.ifAllGranted("ROLE_RECORD_MODIFY")>
            <a target="_blank" href="${rc.contextPath}/record/editform/${h.record.pid?url}">${h.record.pid?html}</a>
            <#else>
            ${h.record.pid?html}
            </#if>
        </td>

        <td><@_ "holding.statusType.${h.status?string}" h.status?string/></td>
      </tr>
      </#list>
    </tbody>
  </table>
  </ul>
</@base>
