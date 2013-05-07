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
