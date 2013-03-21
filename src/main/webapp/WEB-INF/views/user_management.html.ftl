<#include "base.ftl"/>
<#include "form.ftl" />

<#-- Build the title -->
<#assign title>
<@_ "userList.title" "User Management"/>
</#assign>

<#-- Build the page -->
<@base "${title}">
<h1>${title}</h1>
<ul class="user_list">
  <#list users as user>
  <li>
    <span class="user">
      ${user.username?html}
    </span>
    <form method="POST" action="">
      <input type="hidden" name="action" value="chgrp"/>
      <input type='hidden' name="user" value="${user.id?c}"/>
      <select multiple name="groups"

      <#if user.id == _sec.principal.id>
        disabled="disabled"
      </#if>
        >
      <#list groups as group>
      <option value="${group.id?c}"
        <#if user.groups?seq_contains(group)>
        selected
        </#if>
      >${group.name?html}</option>
      </#list>
      </select>
      <#if user.id != _sec.principal.id>
      <input type="submit" value="<@_ "user.chgrp" "Change Groups"/>"/>
      </#if>
    </form>
  </#list>
</ul>
</@base>
