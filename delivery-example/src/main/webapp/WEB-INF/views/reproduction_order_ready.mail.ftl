<#include "mail.ftl">
<@mail reproduction.customerName>
<@_ "reproductionMail.orderReadyMessage" " With this email we confirm your reproduction request. An order is ready on the following link:" /> \r\n
${prop_urlSelf}/reproduction/confirm/${reproduction.id?c}/${reproduction.token}

--- <@_ "reproduction.records" "Items"/> ---
<#list reproduction.holdingReproductions as hr>
 <#assign h = hr.holding>
 <#assign info = h.record.externalInfo>
* ${h.record.title} - ${h.signature} <#if info.author??>/ ${info.author} </#if><#if hr.comment??>- ${hr.comment}</#if>

<@_ "reproductionStandardOption.price" "Price"/>: ${hr.price?string("0.00")} EUR -
<@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/>: ${hr.deliveryTime} <@_ "days" "days"/>

  <#if !hr.standardOption??>
<@_ "reproduction.customReproductionCustomer" "Your wish"/>:
${hr.customReproductionCustomer}

${hr.customReproductionReply}
  </#if>
</#list>
</@mail>