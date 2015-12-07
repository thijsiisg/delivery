<#include "mail.ftl">
<@mailRaedingRoom>
<@_ "reproductionMail.reproductionId" "Reproduction number" />: ${reproduction.id?c}
<@_ "reproductionMail.customerName" "Customer" />: ${reproduction.customerName}
<@_ "reproductionMail.customerEmail" "E-mail" />: ${reproduction.customerEmail}

<#if notInSor?has_content>
<@_ "reproductionMail.activeReproductionNotInSorMessage" "The following items have been sent to the printer for repro:" />


<#list notInSor as hr>
    <#assign h = hr.holding>
    <#assign info = h.record.externalInfo>
* ${h.record.title} - ${h.signature} <#if info.author??>/ ${info.author} </#if><#if hr.comment??>- ${hr.comment}</#if>

</#list>
</#if>

<#if inSor?has_content>
<@_ "reproductionMail.activeReproductionInSorMessage" "The following items can be found in the SOR:" />

<@_ "reproductionMail.sorLinksWarningMessage" "WARNING: These links should not be emailed to the customer!!! Please use FileSender / WeTransfer to sent all reproductions to the customer!!!" />


<#list inSor as hr>
    <#assign h = hr.holding>
    <#assign info = h.record.externalInfo>
* ${h.record.title} - ${h.signature} <#if info.author??>/ ${info.author} </#if><#if hr.comment??>- ${hr.comment}</#if>
${prop_sorAddress}/file/${hr.standardOption.level?lower_case}/10622/${h.externalInfo.barcode?url}?access_token=${prop_sorAccessToken}&contentType=application/save

</#list>
</#if>
</@mailRaedingRoom>
