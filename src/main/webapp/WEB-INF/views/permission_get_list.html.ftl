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
<@_ "permissionList.title" "Permission RequestOverview"/>
</#assign>

<#-- Build the page -->
<@base "${title}">
<h1>${title}</h1>


<fieldset class="actions">
<legend>Filter:</legend>
<form action="" method="GET">
  <#-- Generate hidden input for already existing GET vars -->
  <#list RequestParameters?keys as k>
  <#if k!="search" && k!="status" && k!="page_len" && k!="page">
  <input type="hidden" name="${k?html}" value="${RequestParameters[k]?html}"/>
  </#if>
  </#list>
  <ul>
    <li>
      <#assign search_value>
        <#if RequestParameters["search"]??>
          ${RequestParameters["search"]?html}
        </#if>
      </#assign>
      <label for="search_filter"><@_ "permissionList.search" "Search"/>
      </label>
      <input type="text" id="search_filter" name="search"
             value="${search_value?trim}" />
    </li>
    <li>
      <label for="status_filter"><@_ "permission.status" "Status"/></label>
      <select id="status_filter" name="status">
        <option value=""
        <#if !RequestParameters["status"]?? ||
             RequestParameters["status"] == "">
        selected="selected"</#if>>
        <@_ "permissionList.allStatus" "Status N/A"/>
        </option>

        <#list status_types?keys  as k>
        <option value="${k?lower_case}"
        <#if (RequestParameters["status"]?? &&
              RequestParameters["status"]?upper_case == k)>
        selected="selected"</#if>>
        <@_ "permission.statusType.${k}" "${k}" />
        </option>
        </#list>
      </select>
    </li>
    <li>
      <label for="page_len_filter">
      <@_ "pageListHolder.nrResultsPerPage" "Amount of Results per Page"/>
      </label>
      <select id="page_len_filter" name="page_len">
        <#list 1..(prop_permissionMaxPageLen?number/prop_permissionPageStepSize?number)?floor as i>
        <#assign pageSize = (i*prop_permissionPageStepSize?number)?floor/>
        <option value="${pageSize}"
        <#if (RequestParameters["page_len"]?? &&
              RequestParameters["page_len"]?number == pageSize) ||
             (!RequestParameters["page_len"]?? &&
              prop_permissionPageLen?number == pageSize)>
        selected="selected"</#if>>${pageSize}</option>
        </#list>
      </select>
    </li>
  </ul>
    <#assign searchLabel>
    <@_ "permissionList.search" "Search"/>
    </#assign>
    <input type="submit" value="${searchLabel}"/>
</form>
</fieldset>

<#assign granted_true>
<@_ "recordPermission.granted.true" "Granted"/>
</#assign>
<#assign granted_false>
<@_ "recordPermission.granted.false" "Denied"/>
</#assign>
<#assign granted_null>
<@_ "recordPermission.granted.null" "To be reviewed"/>
</#assign>


<#if pageListHolder.pageList?size == 0>
<span class="bignote"><@_ "search.notfound" "No results..."/></span>
<#else>
<table class="overview">
  <thead>
  <tr>
    <th></th>
    <th>
      <@sortLink "visitor_name"><@_ "permission.name" "Name"/></@sortLink>
    </th>
    <th>
      <@sortLink "research_subject"><@_ "permission.researchSubject" "Research Subject"/></@sortLink>
    </th>
    <th>
      <@sortLink "from_date"><@_ "permission.dateFrom" "Date From"/></@sortLink>
    </th>
    <th>
      <@sortLink "to_date"><@_ "permission.dateTo" "Date To"/></@sortLink>
    </th>
    <th><@_ "permission.recordPermissions" "Record Permissions"/></th>
    <th><@sortLink "status"><@_ "permission.status" "Status"/></@sortLink></th>
  </tr>
  </thead>
  <tbody>
  <#list pageListHolder.pageList as permission>
  <tr>
    <td>
      <a href="${rc.contextPath}/permission/${permission.id?c}">
      <@_ "permissionList.edit" "Administrate"/>
      </a>
    </td>
    <td>${permission.name?html}</td>
    <td>${permission.researchSubject?html}</td>
    <td>${permission.dateFrom?string(prop_dateFormat)}</td>
    <td>${permission.dateTo?string(prop_dateFormat)}</td>
    <td>
      <ul>
        <#list permission.recordPermissions as rp>
        <#assign info = rp.record.externalInfo>
        <li>${rp.record.title?html} <#if info.author??>/ ${info.author} </#if>-
        <#if !rp.granted && permission.status?string == "PENDING">
        ${granted_null}
        <#else>${rp.granted?string(granted_true,granted_false)}
        </#if>
        </li>
        </#list>
      </ul>
    </td>

    <td><@_ "permission.statusType.${permission.status?string}" "${permission.status?string}" /></td>
  </tr>
  </#list>
  </tbody>
</table>
<@pageLinks pageListHolder />
</#if>
</@base>

