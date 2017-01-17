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

<#import "spring.ftl" as spring/>
<#include "base.ftl">
<#include "localization.ftl">
<#assign title>
    <@_ "permission.success" "Permission request placed successfully."/>
</#assign>
<@userbase "${title}">
<section>
    <heading>
        <h1>${title}</h1>
    </heading>

    <section>
        <ul class="items">
            <#list records as rec>
                <#assign info = rec.externalInfo>
                <li>
                    ${rec.title?html}
                    <#if info.author??>/ ${info.author}</#if>
                    <#if rec.parent??> - ${rec.holdings[0].signature}</#if>
                </li>
            </#list>
        </ul>
    </section>

    <@_ "permission.successMsg" "Permission has been requested. You will be contacted with the decision as soon as possible."/>

    <#if error?? >
        <p class="error"><@_ "permission.error."+error error /></p>
    </#if>

    <p><@_ "permission.backToSearch" "Close this tab and return to Search for new requests" /></p>
</section>

</@userbase>
