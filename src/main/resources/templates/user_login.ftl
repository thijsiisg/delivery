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

<#assign login>
  <@_ "security.login" "Login"/>
</#assign>

<@preamble login />
<@userHeading />
<@body>
<h1>${login}</h1>

<#if error??>
  <ul class="errors">
    <li>
      <b>${error}</b>
    </li>
  </ul>
</#if>

<section>
  <form name="f" action="/user/login" method="POST">
    <fieldset>
      <label for="username" class="field">
        <@_ "security.username" "Username"/>
      </label>

      <input type="text" class="field" name="username" id="username"/>

      <label for="password" class="field">
        <@_ "security.password" "Password"/>
      </label>

      <input type="password" class="field" name="password" id="password"/>
    </fieldset>

    <ul class="buttons">
      <li>
        <input type='submit' id="submit" value='${login}'/>
      </li>
    </ul>
  </form>
</section>
</@body>
