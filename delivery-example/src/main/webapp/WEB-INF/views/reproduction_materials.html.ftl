<#include "base.ftl"/>
<#include "form.ftl" />

<#-- Build the title -->
<#assign title>
<@_ "reproductionMaterials.noRequestsAndTotalAmount" "Number of requests and total amount payed"/>
</#assign>

<#-- Build the page -->
<@base "${title}">
<h1>${title}</h1>

<fieldset class="actions">
<legend>Filter:</legend>
<form action="" method="GET">
  <#-- Generate hidden input for already existing GET vars -->
  <#list RequestParameters?keys as k>
    <#if k!="from_date" && k!="to_date">
      <input type="hidden" name="${k?html}" value="${RequestParameters[k]?html}"/>
    </#if>
  </#list>
  <ul>
    <li>
      <#assign from_date_value>
        <#if RequestParameters["from_date"]??>
          ${RequestParameters["from_date"]?html}
        <#else>
          ${.now?string("yyyy-MM-dd")}
        </#if>
      </#assign>
      <label for="from_date_filter">
        <@_ "reproductionList.dateFrom" "From"/>
      </label>
      <input type="text" maxlength="10" id="from_date_filter" name="from_date"
             value="${from_date_value?trim}" class="filter_date" />
    </li>
    <li>
      <#assign to_date_value>
        <#if RequestParameters["to_date"]??>
          ${RequestParameters["to_date"]?html}
        <#else>
          ${.now?string("yyyy-MM-dd")}
        </#if>
      </#assign>
      <label for="to_date_filter">
        <@_ "reproductionList.dateUpTo" "Up To"/>
      </label>
      <input type="text" maxlength="10" id="to_date_filter" name="to_date"
             value="${to_date_value?trim}" class="filter_date" />
    </li>
  </ul>
    <#assign searchLabel>
    <@_ "reproductionList.search" "Search"/>
    </#assign>
    <input type="submit" value="${searchLabel}"/>
</form>
</fieldset>

<#if tuplesMaterials?size gt 0>
  <table class="overview">
  <thead>
  <tr>
    <th><@_ "record.externalInfo.materialType" "Material type"/></th>
    <th><@_ "reproductionMaterials.noRequests" "Number of requests"/></th>
  </tr>
  </thead>
  <tbody>
    <#list tuplesMaterials as tuple>
    <tr>
      <td><@_ "record.externalInfo.materialType.${tuple.get('material')?upper_case}" "${tuple.get('material')?upper_case}" /></td>
      <td>${tuple.get('noRequests')?html}</td>
    </tr>
    </#list>
  </tbody>
  </table>
</#if>

<table class="overview">
  <thead>
  <tr>
    <th><@_ "reproductionMaterials.sumAmount" "Total amount payed"/></th>
    <th><@_ "reproductionMaterials.sumRefundedAmount" "Total amount refunded"/></th>
    <th><@_ "reproductionMaterials.sumNotRefundedAmount" "Total amount not refunded"/></th>
  </tr>
  </thead>
  <tbody>
    <#if tuplePayedAmounts.get("totalAmount")??>
    <tr>
      <td>&euro; ${(tuplePayedAmounts.get('totalAmount') / 100)?string("0.00")}</td>
      <td>&euro; ${(tuplePayedAmounts.get('totalRefundedAmount') / 100)?string("0.00")}</td>
      <td>&euro; ${((tuplePayedAmounts.get('totalAmount') - tuplePayedAmounts.get('totalRefundedAmount')) / 100)?string("0.00")}</td>
    </tr>
    <#else>
    <tr>
      <td>&euro; ${0?string("0.00")}</td>
      <td>&euro; ${0?string("0.00")}</td>
      <td>&euro; ${0?string("0.00")}</td>
    </tr>
    </#if>
  </tbody>
</table>
</@base>
