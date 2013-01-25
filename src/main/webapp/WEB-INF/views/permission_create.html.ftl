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
  <@buttons>
    <@submit "permission" />
  </@buttons>
  </@form>
  </div>
  <p><@_ "createpermission.note" ""/></p>
</section>
</@userbase>
