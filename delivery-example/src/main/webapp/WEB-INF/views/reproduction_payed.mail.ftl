<#include "mail.ftl">
<@mail reproduction.customerName>
<@_ "reproductionMail.payedMessage" "With this email we confirm your payment." />
<#if !reproduction.isForFree()>
  <@_ "reproductionMail.payedNotFreeMessage" "You will also receive an email from our payment provider to confirm the payment." />
</#if>

--- <@_ "reproduction.records" "Items"/> ---
<#list reproduction.holdingReproductions as hr>
 <#assign h = hr.holding>
 <#assign info = h.record.externalInfo>
* ${h.record.title} - ${h.signature} <#if info.author??>/ ${info.author} </#if><#if hr.comment??>- ${hr.comment}</#if>

<@_ "reproductionStandardOption.price" "Price"/>: euro ${hr.price?string("0.00")} -
<@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/>: ${hr.deliveryTime} <@_ "days" "days"/>
</#list>

--- <@_ "total" "Total" /> ---
<@_ "reproductionStandardOption.price" "Price"/>: euro ${reproduction.getTotalPrice()?string("0.00")} -
<@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/>: ${reproduction.getEstimatedDeliveryTime()} <@_ "days" "days"/>
</@mail>