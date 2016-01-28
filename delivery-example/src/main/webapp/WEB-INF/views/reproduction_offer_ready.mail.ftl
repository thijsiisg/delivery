<#include "mail.ftl">
<@mail reproduction.customerName>
<@_ "reproductionMail.offerReadyMessage" " With this email we confirm your reproduction request. An order is ready on the following link:" />

${prop_urlSelf}/reproduction/confirm/${reproduction.id?c}/${reproduction.token}?locale=${locale}

<@_ "reproductionMail.reproductionId" "Reproduction number" />: ${reproduction.id?c}

--- <@_ "reproduction.records" "Items"/> ---
<#list reproduction.holdingReproductions as hr>
 <#assign h = hr.holding>
 <#assign info = h.record.externalInfo>
* ${h.record.title} - ${h.signature} <#if info.author??>/ ${info.author} </#if><#if hr.comment??>- ${hr.comment}</#if>
<@_ "reproductionStandardOption.price" "Price"/>: ${hr.price?string("0.00")} EUR
<#if hr.copyrightPrice?? && hr.copyrightPrice gt 0><@_ "reproductionStandardOption.copyrightPrice" "Copyright price"/>: ${hr.copyrightPrice?string("0.00")} EUR</#if>
<@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/>: ${hr.deliveryTime} <@_ "days" "days"/>
<#if hr.standardOption??>
<#if locale == 'nl'>${hr.standardOption.optionDescriptionNL}<#else>${hr.standardOption.optionDescriptionEN}</#if>

<#else>

<@_ "reproduction.customReproductionCustomer" "Your wish"/>:
${hr.customReproductionCustomer}

<#if hr.customReproductionReply??>
${hr.customReproductionReply}

  </#if>
</#if>
</#list>
<#if (reproduction.getAdminstrationCosts() gt 0) ||(reproduction.getCopyrightPrice() gt 0) || (reproduction.discount gt 0)>
--- <@_ "including" "Including" /> ---
<#if reproduction.getAdminstrationCosts() gt 0>
<@_ "reproduction.adminstrationCosts" "Adminstration cost"/>: ${reproduction.getAdminstrationCosts()?string("0.00")} EUR
</#if>
<#if reproduction.getCopyrightPrice() gt 0>
<@_ "reproduction.copyright" "Copyright"/>: ${reproduction.getCopyrightPrice()?string("0.00")} EUR
</#if>
<#if reproduction.discount gt 0>
<@_ "reproduction.discount" "Discount"/>: ${reproduction.discount?string("0.00")} EUR
</#if>

</#if>
--- <@_ "total" "Total" /> ---
<@_ "reproductionStandardOption.price" "Price"/>: ${reproduction.getTotalPrice()?string("0.00")} EUR
<@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/>: ${reproduction.getEstimatedDeliveryTime()} <@_ "days" "days"/>

<#if reproduction.deliveryTimeComment??>
<@_ "reproduction.deliveryTimeComment" "Expected delivery time"/>:
${reproduction.deliveryTimeComment}
</#if>
</@mail>