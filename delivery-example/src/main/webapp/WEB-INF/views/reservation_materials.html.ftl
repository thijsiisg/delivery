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

<#include "base.ftl"/>
<#include "form.ftl" />

<#-- Build the title -->
<#assign title>
<@_ "reservationMaterials.noRequestsPerMaterial" "Number of requests per material type"/>
</#assign>

<#-- Build the page -->
<@base "${title}">
<h1>${title}</h1>

<fieldset class="actions">
<legend>Filter:</legend>
<form action="" method="GET">
  <#-- Generate hidden input for already existing GET vars -->
  <#list RequestParameters?keys as k>
  <#if k!="date">
  <input type="hidden" name="${k?html}" value="${RequestParameters[k]?html}"/>
  </#if>
  </#list>
  <ul>
    <li>
      <#assign date_value>
	    <#if RequestParameters["date"]??>
	      ${RequestParameters["date"]?html}
	    <#else>
	      ${.now?string("yyyy-MM-dd")}
	    </#if>
      </#assign>
      <label for="date_filter"><@_ "reservationMaterials.date" "Date"/>
      </label>
      <input type="text" maxlength="10" id="date_filter" name="date"
             value="${date_value?trim}" class="filter_date" />
    </li>
  </ul>
    <#assign searchLabel>
    <@_ "reservationList.search" "Search"/>
    </#assign>
    <input type="submit" value="${searchLabel}"/>
</form>
</fieldset>

<#if tuples?size == 0>
<span class="bignote"><@_ "search.notfound" "No results..."/></span>
<#else>
<table class="overview">
  <thead>
  <tr>
    <th><@_ "record.externalInfo.materialType" "Material type"/></th>
	<th><@_ "reservationMaterials.noRequests" "Number of requests"/></th>
  </tr>
  </thead>
  <tbody>
  <#list tuples as tuple>
  <tr>
    <td><@_ "record.externalInfo.materialType.${tuple.get('material')?upper_case}" "${tuple.get('material')?upper_case}" /></td>
    <td>${tuple.get('noRequests')?html}</td>
  </tr>
  </#list>
  </tbody>
</table>
</#if>

</@base>
