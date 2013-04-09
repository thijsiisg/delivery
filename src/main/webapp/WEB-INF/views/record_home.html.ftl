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
<#assign title>
<@_ "homerecord.title" "Edit Records"/>
</#assign>

<@base "${title}">
  <h1>${title}</h1>
  <fieldset class="actions">
    <form action="" method="POST">
      <label class="field" for="pid">
        <@_ "editrecord.byPid" "Search by PID:"/>
      </label>
      <input type="text" class="field" name="pid" id="pid" value="${(RequestParameters.pid!"")?html}"/>
      <input type="submit" class="field" name="searchPid" value="<@_ "editrecord.searchPid" "Search"/>"/>
    </form>

    <form action="" method="POST">
      <label class="field" for="title">
        <@_ "editrecord.byTitle" "Search for title:"/>
      </label>
      <input type="text" class="field" name="title" id="title"
             value="${(RequestParameters.title!"")?html}" />
      <input type="submit" class="field" name="searchLocal" value="<@_ "editrecord.searchLocal" "Search"/>"/>
      <input type="submit" class="field" name="searchApi" value="<@_ "editrecord.searchApi" "Search"/>"/>
    </form>
  </fieldset>



  <#if results??>
        <#if results?size == 0>
          <span class="bignote"><@_ "search.notfound" "No results..."/></span>
        <#else>
              <table class="searchRecord">
        <thead>
          <tr>
            <th><@_ "record.title" ""/></th>
            <th>PID</th>
          </tr>
        </thead>
        <tbody>
          <#list results?keys as key>
          <tr><td>${results[key]?html}</td><td><a href="${rc.contextPath}/record/editform/${key?url}">
            ${key?html}
          </a></td></tr>
          </#list>
        </tbody>
        </table>
        </#if>

  </#if>
</@base>
