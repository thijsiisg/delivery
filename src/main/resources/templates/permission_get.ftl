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
<@_ "permissionSingle.title" "Permission Request"/> ${permission.id?c}
</#assign>

<#-- Build the page -->
<@base "${title}">
<h1>${title}</h1>

<ul class="permissionDetails">
    <li><span><@_ "permission.name" "Name"/></span> ${permission.name?html}</li>
    <li><span><@_ "permission.email" "E-mail"/></span> ${permission.email?html}</li>
    <li><span><@_ "permission.address" "Address"/></span> ${permission.address?html}</li>
    <li><span><@_ "permission.researchSubject" "Research Subject"/></span>${permission.researchSubject?html}</li>
    <li><span><@_ "permission.researchOrganization" "Research Organization"/></span> ${permission.researchOrganization?html}</li>
    <li><span><@_ "permission.dateFrom" "Date From"/></span> ${permission.dateFrom?string(delivery.dateFormat)}</li>
    <li><span><@_ "permission.dateTo" "Date To"/></span> ${permission.dateTo?string(delivery.dateFormat)}</li>
    <li><span><@_ "permission.explanation" "Explanation/Comments"/></span>${permission.explanation?html}</li>
    <li><span><@_ "permission.status" "Status"/></span> <@_ "permission.statusType.${permission.status?string}" permission.status?string/></li>
  </ul>

<h3><@_ "permission.recordPermissions" "Permissions per record"/></h3>

<form action="process" method="POST">
<input type="hidden" name="id" value="${permission.id?c}"/>

<#list permission.recordPermissions as rp>
<fieldset class="recordPermissionDetails">
<#assign info = rp.record.externalInfo>
<legend>${rp.record.title?html} <#if info.author??>/ ${info.author}</#if></legend>
<ul>
  <li>
    <select name="granted_${rp.id?c}"
      <#if _sec.ifNotGranted("ROLE_PERMISSION_MODIFY")>disabled="disabled
      "</#if>>
      <option value="true"
      <#if rp.granted>selected="selected"</#if>>
      <@_ "recordPermission.granted.true" "Granted"/></option>
      <option value="false"
      <#if !rp.granted>selected="selected"</#if>>
      <@_ "recordPermission.granted.false" "Denied"/></option>
    </select>
  </li>
  <li class="motivation"><label for="motivation_${rp.id?c}">
  <@_ "recordPermission.motivation" "Motivation"/><br />
  <span class="note">(<@_  "permissionSingle.motivationLanguage" "Please specify in"/> <@_ "language.${permission.requestLocale}" "English" />)</span>
  </label>
      <textarea id="motivation_${rp.id?c}" name="motivation_${rp.id?c}"><#if rp.motivation??>${rp.motivation?html}</#if></textarea>
  </li>
  <#if rp.record.restriction??>
  <li><span><@_ "record.restriction" "Restriction Details"/></span> ${rp.record.restriction?html}</li>
  </#if>
  <#if rp.record.comments??>
  <li><span><@_ "record.comments" "Comments"/></span> ${rp.record.comments?html}</li>
  </#if>
  <#if rp.record.realContact??>
  <#assign cnt = rp.record.realContact>
  <#if cnt.firstname?? ||
       cnt.lastname??>
  <li><span><@_ "record.contactFullName" "Contact Name"/></span> ${(cnt.firstname!"")?html} ${(cnt.preposition!"")?html} ${(cnt.lastname!"")?html}</li>
  </#if>
  <#if cnt.phone??>
  <li><span><@_ "contact.phone" "Phone"/></span> ${cnt.phone?html}</li>
  </#if>
  <#if cnt.fax??>
  <li><span><@_ "contact.fax" "Fax"/></span> ${cnt.fax?html}</li>
  </#if>
  <#if cnt.email??>
  <li><span><@_ "contact.email" "E-mail"/></span> ${cnt.email?html}</li>
  </#if>
  <#if cnt.address??>
  <li><span><@_ "contact.address" "Address"/></span> ${cnt.address?html}</li>
  </#if>
  <#if cnt.zipcode??>
  <li><span><@_ "contact.zipcode" "Zipcode"/></span> ${cnt.zipcode?html}</li>
  </#if>
  <#if cnt.location??>
  <li><span><@_ "contact.location" "Location"/></span> ${cnt.location?html}</li>
  </#if>
  <#if cnt.country??>
  <li><span><@_ "contact.country" "Country"/></span> ${cnt.country?html}</li>
  </#if>
</#if>
  <#if _sec.ifAllGranted("ROLE_PERMISSION_MODIFY")>
  <li><a href="${rc.contextPath}/record/editform/${rp.record.pid?url}"
         target="_blank"><@_ "permissionSingle.toRecord" "Go to Record"/></a></li>
  </#if>
</ul>
</fieldset>

</#list>

<#if _sec.ifAllGranted("ROLE_PERMISSION_MODIFY")>
<fieldset class="actions">
<legend><@_ "permissionSingle.actions" "Actions"/>:</legend>
<#assign delete_label>
<@_ "permissionSingle.delete" "Delete" />
</#assign>
<#assign save_label>
<@_ "permissionSingle.save" "Save" />
</#assign>
<#assign save_and_finish_label>
<@_ "permissionSingle.saveAndFinish" "Save and Finish" />
</#assign>
<#assign deleteConfirm>
<@_ "permissionSingle.confirmDelete" "Deletion of this permission request, including all the individual permissions set, is permanent. Are you sure you want to delete this permission request?" />
</#assign>
<#assign finishConfirm>
<@_ "permissionSingle.confirmFinish" "Finishing the request will give the requester access to the records you granted access to. Please confirm you are done granting/denying the permission request."/>
</#assign>
<ul class="horizontal">
  <li>
    <input type="submit" name="save" value="${save_label}"/>
  </li>
  <#if permission.status?string != "HANDLED">
  <li>
    <input type="submit" name="saveandfinish"
                         onClick="return confirm('${finishConfirm}');"
                         value="${save_and_finish_label}"/>
  </li>
  </#if>
  <#if _sec.ifAllGranted("ROLE_PERMISSION_DELETE")>
  <li>
    <input type="submit" name="delete"
                         onClick="return confirm('${deleteConfirm}');"
                         value="${delete_label}"/>
  </li>
  </#if>
</ul>
</fieldset>
</#if>

</form>
</@base>
