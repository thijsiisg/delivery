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
<#include "form.ftl"/>

<#-- Build the title -->
<#assign title>
<@_ "permission.create" "Create Permission Request"/>
</#assign>

<@userbase title>
<h1>${title}</h1>
<section>
  <heading>
    <hgroup>
    <#list records as rec>
    <#assign info = rec.externalInfo>
    <h1>${rec.title?html} <#if info.author??>/ ${info.author}</#if></h1>
    </#list>
    </hgroup>
  </heading>

  <div class="permission_form">
  <@form "" "permission" "create">
  <@input "permission.visitorName" ""/>
  <@input "permission.visitorEmail" ""/>
  <@textarea "permission.address" ""/>
  <@date "permission.dateFrom" ""/>
  <@date "permission.dateTo" ""/>
  <@input "permission.researchOrganization" "create"/>
  <@textarea "permission.researchSubject" "create"/>
  <@textarea "permission.explanation" "create"/>

  <label for="captcha_response_field" class="field">
	  <@_ "captcha.explanation" "Type the following word to prevent spam" />
  </label>

  <div id="captcha_widget_div">
	  <input type="text" id="captcha_response_field" name="captcha_response_field" value="" class="field" />
      <img src="/captcha" id="captcha_image" />
  </div>
  <#if captchaError?? >
	  <ul class="errors">
		  <li>
			  <b>${captchaError?html}</b>
		  </li>
	  </ul>
  </#if>

  <@buttons>
    <@submit "permission" />
  </@buttons>
  </@form>
  </div>
  <p><@_ "createpermission.note" ""/></p>
</section>
</@userbase>
