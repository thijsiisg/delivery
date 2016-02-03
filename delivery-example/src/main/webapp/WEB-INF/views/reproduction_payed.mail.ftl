<#include "mail.ftl">
<@mail reproduction.customerName>
<@_ "reproductionMail.payedMessage" "With this email we confirm your payment." />
<#if !reproduction.isForFree()>

<@_ "reproductionMail.payedNotFreeMessage" "You will also receive an email from our payment provider to confirm the payment." />
</#if>

<@_ "reproductionMail.reproductionId" "Reproduction number" />: ${reproduction.id?c}
<#if reproduction.order??><@_ "reproductionMail.orderId" "Order number" />: ${reproduction.order.id?c}</#if>

--- <@_ "reproduction.records" "Items"/> ---
<#list reproduction.holdingReproductions as hr>
 <#assign h = hr.holding>
 <#assign info = h.record.externalInfo>
* ${h.record.title} - ${h.signature} <#if info.author??>/ ${info.author} </#if><#if hr.comment??>- ${hr.comment}</#if>
<@_ "reproductionStandardOption.price" "Price"/>: <@holdingPrice hr.price hr.completePrice hr.numberOfPages/>
<#if hr.copyrightPrice?? && hr.copyrightPrice gt 0><@_ "reproductionStandardOption.copyrightPrice" "Copyright price"/>: ${hr.copyrightPrice?string("0.00")} EUR</#if>
<@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/>: ${hr.deliveryTime} <@_ "days" "days"/>

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
</@mail>