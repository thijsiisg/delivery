<#include "mail.ftl">
<@mail reservation.visitorName>
<@_ "reservationMail.confirmationMessage" "With this mail we confirm your reservation has been successfully received. You can come to the reading room at the date given below."/>


--- <@_ "reservation.date" "Date"/> ---
${reservation.date?string(prop_dateFormat)}

<#--<#if reservation.queueNo??>
--- <@_ "reservation.queueNo" "Queue Number"/> ---
${reservation.queueNo}
</#if>-->

--- <@_ "reservation.records" "Records"/> ---
<#list reservation.holdingReservations as hr>
 <#assign h = hr.holding>
 <#assign info = h.record.externalInfo>
* ${h.record.title} - ${h.signature} <#if info.author??>/ ${info.author} </#if><#if hr.comment??>- ${hr.comment}</#if>
</#list>
</@mail>