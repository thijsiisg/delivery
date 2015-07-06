<#include "mail.ftl">
<@mail reproduction.customerName>
<@_ "reproduction.customRequest.message" "Your reproduction request has been successfully received. You will be notified of the total price and estimated delivery time by email." />


--- <@_ "reproduction.records" "Items"/> ---
<#list reproduction.holdingReproductions as hr>
 <#assign h = hr.holding>
 <#assign info = h.record.externalInfo>
* ${h.record.title} - ${h.signature} <#if info.author??>/ ${info.author} </#if><#if hr.comment??>- ${hr.comment}</#if>

  <#if hr.standardOption??>
<@_ "reproductionStandardOption.price" "Price"/>: euro ${hr.price?string("0.00")} -
<@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/>: ${hr.deliveryTime} <@_ "days" "days"/>
  <#else>
<@_ "reproductionStandardOption.price" "Price"/>: <@_ "tbd" "To be determined"/> - <@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/>: <@_ "tbd" "To be determined"/>

<@_ "reproduction.customReproductionCustomer" "Your wish"/>:
${hr.customReproductionCustomer}
  </#if>

</#list>
</@mail>