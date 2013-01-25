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
        <li>${rec.pid?html}: ${rec.title?html} <#if info.author??>/ ${info.author}</#if></li>
      </#list>
    </ul>
  </section>

  <@_ "permission.successMsg" "Permission has been requested. You will
  be contacted with the decision as soon as possible."/>
</section>

</@userbase>
