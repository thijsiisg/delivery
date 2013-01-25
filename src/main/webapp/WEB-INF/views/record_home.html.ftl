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
