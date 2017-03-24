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
  <#if k!="search" && k!="from_date" && k!="to_date" && k!="permission" && k!="page_len" && k!="page">
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
          <label for="permission_filter"><@_ "recordPermission.granted" "Permission"/></label>
          <select id="permission_filter" name="permission">
              <option value=""
                  <#if !RequestParameters["permission"]?? ||
                  RequestParameters["permission"] == "">
                      selected="selected"</#if>>
                  <@_ "permissionList.allPermission" "Permission N/A"/>
              </option>

              <option value="null"
                  <#if (RequestParameters["permission"]?? &&
                  RequestParameters["permission"]?upper_case == 'NULL')>
                      selected="selected"</#if>>
                  <@_ "recordPermission.granted.null" "To decide" />
              </option>

              <option value="true"
                  <#if (RequestParameters["permission"]?? &&
                  RequestParameters["permission"]?upper_case == 'TRUE')>
                      selected="selected"</#if>>
                  <@_ "recordPermission.granted.true" "Granted" />
              </option>

              <option value="false"
                  <#if (RequestParameters["permission"]?? &&
                  RequestParameters["permission"]?upper_case == 'FALSE')>
                      selected="selected"</#if>>
                  <@_ "recordPermission.granted.false" "Denied" />
              </option>
          </select>
      </li>
      <li>
          <#assign from_date_value>
          <#-- The date field has priority over from_date -->
              <#if RequestParameters["date"]??>
              ${RequestParameters["date"]?html}
              <#elseif RequestParameters["from_date"]??>
              ${RequestParameters["from_date"]?html}
              </#if>
          </#assign>
          <label for="from_date_filter"><@_ "permissionList.dateFrom" "From"/>
          </label>
          <input type="text" maxlength="10" id="from_date_filter" name="from_date"
                 value="${from_date_value?trim}" class="filter_date" />
      </li>
      <li>
          <#assign to_date_value>
          <#-- The date field has priority over to_date -->
              <#if RequestParameters["date"]??>
              ${RequestParameters["date"]?html}
              <#elseif RequestParameters["to_date"]??>
              ${RequestParameters["to_date"]?html}
              </#if>
          </#assign>
          <label for="to_date_filter"><@_ "permissionList.dateUpTo" "Up To"/>
          </label>
          <input type="text" maxlength="10" id="to_date_filter" name="to_date"
                 value="${to_date_value?trim}" class="filter_date" />
      </li>
    <li>
      <label for="page_len_filter">
      <@_ "pageListHolder.nrResultsPerPage" "Amount of Results per Page"/>
      </label>
      <select id="page_len_filter" name="page_len">
        <#list 1..(delivery.permissionMaxPageLen?number/delivery.permissionPageStepSize?number)?floor as i>
        <#assign pageSize = (i*delivery.permissionPageStepSize?number)?floor/>
        <option value="${pageSize}"
        <#if (RequestParameters["page_len"]?? &&
              RequestParameters["page_len"]?number == pageSize) ||
             (!RequestParameters["page_len"]?? &&
              delivery.permissionPageLen?number == pageSize)>
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


<#if recordPermissions?size == 0>
<span class="bignote"><@_ "search.notfound" "No results..."/></span>
<#else>
<table class="overview">
  <thead>
  <tr>
    <th></th>
    <th><@_ "holding.record" "Item"/></th>
    <th>
      <@sortLink "visitor_name"><@_ "permission.name" "Name"/></@sortLink>
    </th>
    <th><@sortLink "permission"><@_ "permission.permission" "Permission"/></@sortLink></th>
    <th>
      <@sortLink "date_granted"><@_ "permission.dateGranted" "Date granted"/></@sortLink>
    </th>
  </tr>
  </thead>
  <tbody>
  <#list recordPermissions as recordPermission>
    <#assign record = recordPermission.record>
    <#assign permission = recordPermission.permission>
  <tr>
    <td>
      <a href="${rc.contextPath}/permission/${permission.id?c}">
      <@_ "permissionList.edit" "Administrate"/>
      </a>
    </td>
    <td class="leftAligned">${record.toString()?html}</td>
    <td>${permission.name?html}</td>
    <td>
    <#if !recordPermission.granted && !recordPermission.dateGranted??>
        ${granted_null}
    <#else>
        ${recordPermission.granted?string(granted_true,granted_false)}
    </#if>
    </td>
    <td>
    <#if recordPermission.dateGranted??>
        ${recordPermission.dateGranted?string(prop_dateFormat)}
    <#else>
        ${granted_null}
    </#if>
    </td>
  </tr>
  </#list>
  </tbody>
</table>
<@pageLinks recordPermissionsSize RequestParameters["page_len"]!prop_requestPageLen?number RequestParameters["page"]!1 />
</#if>
</@base>

