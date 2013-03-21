<#include "utils.ftl"/>
<#if callback??>
${callback}(
</#if>
  {
    "visitorName" : "${reservation.visitorName?js_string}",
    "visitorEmail" : "${reservation.visitorEmail?js_string}",
    "status" : "${reservation.status?string}",
    "date" : "${reservation.date?string("yyyy-MM-dd")}",
    <#if reservation.returnDate??>"returnDate" : "${reservation.returnDate?string("yyyy-MM-dd")}",</#if>
    <#if reservation.queueNo??>"queueNo" : ${reservation.queueNo?c},</#if>
    "printed" : ${reservation.printed?string},
    "special" : ${reservation.special?string},
    <#if reservation.comment??>"comment" : "${reservation.comment?js_string}",</#if>
    "items" :
      <@generatePidToHoldingsJson reservation/>
  }
<#if callback??>
);
</#if>
