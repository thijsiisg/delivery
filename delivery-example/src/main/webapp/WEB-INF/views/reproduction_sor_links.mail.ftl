<#include "mail.ftl">
<@mailRaedingRoom>
<@_ "reproductionMail.sorLinksMessage" "After all items have been ingested, the items can be downloaded from the SOR using the following links." />

<@_ "reproductionMail.sorLinksWarningMessage" "WARNING: These links should not be emailed to the customer!!! Please use FileSender to sent all reproductions to the customer!!!" />

<@_ "reproductionMail.reproductionId" "Reproduction number" />: ${reproduction.id?c}

--- <@_ "reproduction.records" "All tems"/> ---
<#list reproduction.holdingReproductions as hr>
  <#assign h = hr.holding>
  <#assign info = h.record.externalInfo>
* ${h.record.title} - ${h.signature} <#if info.author??>/ ${info.author} </#if><#if hr.comment??>- ${hr.comment}</#if>
  <#if hr.standardOption??>
${prop_sorAddress}/file/${hr.standardOption.level?lower_case}/10622/${h.externalInfo.barcode?url}?access_token=${prop_sorAccessToken}&contentType=application/save
  <#else>
<@_ "reproduction.customReproduction" "Custom reproduction"/>
  </#if>
</#list>
</@mailRaedingRoom>
