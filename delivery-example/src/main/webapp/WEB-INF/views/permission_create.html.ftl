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


<p><@_ "createpermission.note" ""/></p>

<section>
    <@form "" "permission" "create">
        <heading>
            <hgroup>
                <fieldset>
                    <legend><@_ "permission.records" ""/></legend>
                    <#assign skip = []>
                    <#list records as r>
                        <#assign info = r.externalInfo>
                        <#if !skip?seq_contains(r.pid)>
                            <h3>${info.title?html}</h3>

                            <ul class="holdingDetails">
                                <li>
                                    <span><@_ "record.externalInfo.materialType" "Material Type"/></span>
                                    <@_ "record.externalInfo.materialType.${info.materialType}" ""/>
                                </li>
                                <#if r.parent??>
                                    <li>
                                        <span><@_ "record.items" "Items"/></span>

                                        <#assign items = []>
                                        <#list records as childR>
                                            <#if childR.parent.pid == r.parent.pid>
                                                <#assign skip = skip + [childR.pid]>
                                                <#assign items = items + [childR.holdings[0].signature?html]>
                                            </#if>
                                        </#list>

                                        ${items?join(', ')}
                                    </li>
                                </#if>
                                <#if info.author??>
                                    <li>
                                        <span><@_ "record.externalInfo.author" "Author"/></span> ${info.author?html}
                                    </li>
                                </#if>
                                <#if info.displayYear??>
                                    <li>
                                        <span><@_ "record.externalInfo.displayYear" "Year"/></span> ${info.displayYear?html}
                                    </li>
                                </#if>
                            </ul>
                        </#if>
                    </#list>
                </fieldset>
            </hgroup>
        </heading>

        <div class="permission_form">
            <fieldset>
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
                    <input type="text" id="captcha_response_field" name="captcha_response_field" value="" class="field"
                           autocomplete="off"/>
                    <img src="<@spring.url relativeUrl="/captcha"/>" id="captcha_image"/>
                    <a href="#" class="refreshCaptcha">
                        <@_ "captcha.refresh" "Refresh captcha" />
                    </a>
                </div>
                <#if captchaError?? >
                    <ul class="errors">
                        <li>
                            <b>${captchaError?html}</b>
                        </li>
                    </ul>
                </#if>
            </fieldset>

            <@buttons>
                <@submit "permission" />
            </@buttons>
        </div>
    </@form>
</section>
</@userbase>
