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

<@_ "reproductionStandardOption.price" "Price"/>: ${hr.price?string("0.00")} EUR -
<@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/>: ${hr.deliveryTime} <@_ "days" "days"/>
</#list>

<#if (reproduction.copyrightPrice gt 0) || (reproduction.discount gt 0)>
--- <@_ "including" "Including" /> ---
  <#if reproduction.copyrightPrice gt 0>
<@_ "reproduction.copyright" "Copyright"/>: ${reproduction.copyrightPrice?string("0.00")} EUR
  </#if>
  <#if reproduction.discount gt 0>
<@_ "reproduction.discount" "Discount"/>: ${reproduction.discount?string("0.00")} EUR
  </#if>
</#if>

--- <@_ "total" "Total" /> ---
<@_ "reproductionStandardOption.price" "Price"/>: ${reproduction.getTotalPrice()?string("0.00")} EUR -
<@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/>: ${reproduction.getEstimatedDeliveryTime()} <@_ "days" "days"/>
</@mail>