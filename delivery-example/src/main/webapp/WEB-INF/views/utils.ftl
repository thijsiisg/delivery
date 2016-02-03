<#ftl strip_text=true strip_whitespace=true/>
<#--

    Copyright (C) 2013 International Institute of Social History

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->

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

  <#list params?keys as k>
  <#assign requestParams = requestParams + {k : params[k]?url} />
  </#list>

  <#if u != "">
    <#local uri = rc.getContextUrl(u)/>
  <#else>
    <#local uri = rc.getRequestUri()/>
  </#if>
${uri}?<#list requestParams?keys as k><#if requestParams[k] !="">${k}=${requestParams[k]}<#if k_has_next>&amp;</#if></#if></#list>
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

<#macro pageApiLinks pageChunk>
<#assign totalPages = (pageChunk.totalResultCount/pageChunk.resultCountPerChunk)?ceiling />
<#assign prev = max(pageChunk.resultStart-pageChunk.resultCountPerChunk, 1) />
<#assign last = (totalPages-1)*pageChunk.resultCountPerChunk+1 />
<#assign next = min(pageChunk.resultStart+pageChunk.resultCountPerChunk, last) />

<div class="pageLinks">
    <#if pageChunk.resultStart &gt; 1>
        <a href="<@paramUrl {"resultStart": "1"} />"
           class="pageLinks">&lt;&lt;</a>
        <a href="<@paramUrl {"resultStart": prev?c} />"
           class="pageLinks">&lt;</a>
    </#if>
    <@_ "pageListHolder.page" "Page"/> ${((pageChunk.resultStart/pageChunk.totalResultCount)*totalPages)?ceiling} /
${totalPages}
    <#if pageChunk.resultStart &lt; last>
        <a href="<@paramUrl {"resultStart": next?c} />"
           class="pageLinks">&gt;</a>
        <a href="<@paramUrl {"resultStart": last?c} />"
           class="pageLinks">&gt;&gt;</a>
    </#if>
</div>
</#macro>

<#function min nr1 nr2>
    <#if nr1 &gt; nr2>
        <#return nr2>
    <#else>
        <#return nr1>
    </#if>
</#function>

<#function max nr1 nr2>
    <#if nr1 &gt; nr2>
        <#return nr1>
    <#else>
        <#return nr2>
    </#if>
</#function>
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

<#macro holdingStatus holdingActiveRequests request holding>
  <#assign holdingActiveRequest = holdingActiveRequests[holding.toString()] ! request/>
  <@_ "holding.statusType.${holding.status?string}" "${holding.status?string}" />
  <#if (holding.status != "AVAILABLE") && !holdingActiveRequest.equals(request)>
    <em class="info">(<@_ "anotherRequest" "by another request"/>)</em>
  </#if>
</#macro>

<#macro holdingPrice price completePrice noPages=0>
    &euro; ${completePrice?string("0.00")}

    <#if noPages gt 0>
        <em class="info">(<@_ "price.page" "Price per page"/>: &euro; ${price?string("0.00")},
            <@_ "no.pages" "Number of pages"/>: ${noPages?html})</em>
    </#if>
</#macro>