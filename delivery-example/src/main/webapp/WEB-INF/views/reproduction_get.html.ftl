<#include "base.ftl"/>
<#include "form.ftl" />

<#-- Build the title -->
<#assign title>
  <@_ "reproductionSingle.title" "Reproduction"/> ${reproduction.id?c}
</#assign>

<#-- Build the page -->
<@base "${title}">
<h1>${title}</h1>

<ul class="reproductionDetails">
  <li><span><@_ "reproduction.customerName" "Name"/></span> ${reproduction.customerName?html}</li>
  <li><span><@_ "reproduction.customerEmail" "E-mail"/></span> ${reproduction.customerEmail?html}</li>
  <li><span><@_ "reproduction.creationDate" "Created on"/></span> ${reproduction.creationDate?string(prop_dateFormat)}
  </li>
  <li>
    <span><@_ "reproduction.status" "Status"/></span> <@_ "reproduction.statusType.${reproduction.status}" reproduction.status?string />
  </li>

  <#assign yes>
    <@_ "yes" "Yes" />
  </#assign>
  <#assign no>
    <@_ "no" "No" />
  </#assign>

  <li>
    <span><@_ "reproduction.printed" "Printed"/></span>
  ${reproduction.printed?string(yes, no)}
  </li>

  <#if reproduction.comment??>
    <li>
      <span><@_ "reproduction.comment" "Comment"/></span>
    ${reproduction.comment?html}
    </li>
  </#if>

  <#if  _sec.ifAllGranted("ROLE_REPRODUCTION_CREATE")>
    <li><br/><a href="${rc.contextPath}/reproduction/masscreateform?fromReproductionId=${reproduction.id?c}">
      <@_ "reproductionSingle.newReproduction"/></a></li>
  </#if>

  <#list reproduction.holdingReproductions as hr>
    <#assign h = hr.holding>
    <#assign info = h.record.externalInfo>

    <li class="spacing">
      <#if hr.standardOption??>
        <h3>${hr.standardOption.optionName?html} - Holding ${h.id}</h3>
      <#else>
        <h3><@_ "reproduction.customReproduction" "Custom reproduction"/> - Holding ${h.id}</h3>
      </#if>

      <ul class="holdingDetails">
        <li>
          <span><@_ "record.externalInfo.title" "Title"/></span>
        ${info.title?html}
        </li>

        <#if info.author??>
          <li>
            <span><@_ "record.externalInfo.author" "Author"/></span>
          ${info.author?html}
          </li>
        </#if>

        <#if info.displayYear??>
          <li>
            <span><@_ "record.externalInfo.displayYear" "Year"/></span>
          ${info.displayYear?html}
          </li>
        </#if>

        <li class="spacing">
          <span><@_ "record.externalInfo.materialType" "Material Type"/></span>
          <@_ "record.externalInfo.materialType.${info.materialType}" ""/>
        </li>

        <li>
          <span><@_ "holding.signature" "Signature"/></span>
        ${h.signature?html}
        </li>

        <li>
          <span>PID</span>
          <#if _sec.ifAllGranted("ROLE_RECORD_MODIFY")>
            <a target="_blank" href="${rc.contextPath}/record/editform/${h.record.pid?url}">${h.record.pid?html}</a>
          <#else>
          ${h.record.pid?html}
          </#if>
        </li>

        <li>
          <span><@_ "holding.status" "Status"/></span>
          <@_ "holding.statusType.${h.status?string}" h.status?string/>
        </li>

        <li class="spacing">
          <span><@_ "reproductionStandardOption.price" "Price"/></span>
          <#if hr.price??>
            &euro; ${hr.price?string("0.00")}
          <#else>
            <@_ "tbd" "To be determined"/>
          </#if>
        </li>

        <li>
          <span><@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/></span>
          <#if hr.deliveryTime??>
            ${hr.deliveryTime?html} <@_ "days" "days"/>
          <#else>
            <@_ "tbd" "To be determined"/>
          </#if>
        </li>

        <#if hr.comment??>
          <li>
            <span><@_ "holdingReproductions.comment" "Comment" /> </span>
          ${hr.comment?html}
          </li>
        </#if>

        <#if !hr.standardOption??>
          <li class="spacing">
            <span><@_ "reproduction.customReproductionCustomer.backend" "Customer's wish" /> </span>
            <em>${hr.customReproductionCustomer?html}</em>
          </li>

          <#if hr.customReproductionReply??>
            <li class="spacing">
              <span><@_ "reproduction.customReproductionReply" "Reply on wish" /> </span>
              <em>${hr.customReproductionReply?html}</em>
            </li>
          </#if>
        </#if>
      </ul>
    </li>
  </#list>
</ul>
</@base>
