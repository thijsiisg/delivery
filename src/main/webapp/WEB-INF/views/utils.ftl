<#ftl strip_text=true strip_whitespace=true/>

<#macro paramUrl params={} u="">
  <#assign requestParams = {}>
  <#if rc.queryString??>
    <#assign varValues = rc.queryString?split("&")>
    <#list varValues as varValue>
      <#if varValue?contains("=")>
        <#assign varValueSplit = varValue?split("=")>
        <#assign requestParams = requestParams + {varValueSplit[0] : varValueSplit[1]}>
      </#if>
    </#list>
  </#if>

  <#local params = requestParams + params/>
  <#if u != "">
    <#local uri = rc.getContextUrl(u)/>
  <#else>
    <#local uri = rc.getRequestUri()/>
  </#if>
${uri}?<#list params?keys as k><#if params[k] !="">${k?url}=${params[k]?url}<#if k_has_next>&amp;</#if></#if></#list>
</#macro>

<#macro sortLink column attributes="">
  <#if RequestParameters["sort"]?? &&
       RequestParameters["sort"] == column &&
       RequestParameters["sort_dir"]?? &&
       RequestParameters["sort_dir"] == "asc">
    <#local dir = "desc"/>
  <#else>
    <#local dir = "asc"/>
  </#if>
  <a href="<@paramUrl {"sort":column, "sort_dir" : dir} />" ${attributes}>
  <#nested>
  </a>
</#macro>

<#macro pageLinks pageListHolder>
<div class="pageLinks">
<#if pageListHolder.page != pageListHolder.firstLinkedPage>
<a href="<@paramUrl {"page":(pageListHolder.firstLinkedPage+1)?string} />"
   class="pageLinks">&lt;&lt;</a>
<a href="<@paramUrl {"page":(pageListHolder.page)?string} />"
   class="pageLinks">&lt;</a>
</#if>
<@_ "pageListHolder.page" "Page"/> ${pageListHolder.page+1} /
${pageListHolder.lastLinkedPage+1}
<#if pageListHolder.page != pageListHolder.lastLinkedPage>
<a href="<@paramUrl {"page":(pageListHolder.page+2)?string} />"
   class="pageLinks">&gt;</a>
<a href="<@paramUrl {"page":(pageListHolder.lastLinkedPage+1)?string} />"
   class="pageLinks">&gt;&gt;</a>
</#if>
</div>
</#macro>

<#macro generatePidToHoldingsJson rs>
    <#assign pidToHoldings = {}>
    <#list rs.holdingReservations as hr>
    <#assign h = hr.holding>
    <#assign pid = h.record.pid>
    <#if !pidToHoldings[pid]??>
        <#assign pidToHoldings = pidToHoldings + {pid : []}>
    </#if>
    <#assign pidToHoldings = pidToHoldings + {pid : pidToHoldings[pid] + [h.signature]}>
    </#list>
    {
      <#list pidToHoldings?keys as pid>
      "${pid}" :
        [
          <#list pidToHoldings[pid] as signature>
          "${signature?js_string}"<#if signature_has_next>,</#if>
          </#list>
        ]
      <#if pid_has_next>,</#if>
      </#list>
    }
</#macro>
