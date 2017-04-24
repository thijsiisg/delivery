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

    <li class="spacing">
        <span><@_ "permission.researchSubject" "Research Subject"/></span>
        <em>${permission.researchSubject?html}</em>
    </li>
    <li>
        <span><@_ "permission.researchOrganization" "Research Organization"/></span>
        <em>${permission.researchOrganization?html}</em>
    </li>
    <li>
        <span><@_ "permission.explanation" "Explanation/Comments"/></span>
        <em>${permission.explanation?html}</em>
    </li>

    <li class="spacing"><span><@_ "permission.code" "Code"/></span> ${permission.code?html}</li>
</ul>

<h3><@_ "permission.recordPermissions" "Permissions per record"/>:</h3>

<form action="process" method="POST">
    <input type="hidden" name="id" value="${permission.id?c}"/>

    <#list permission.recordPermissions as rp>
        <fieldset class="recordPermissionDetails">
            <#assign info = rp.record.externalInfo>
            <legend>${rp.record.toString()?html}</legend>

            <ul>
                <li>
                    <select name="granted_${rp.id?c}" <#if _sec.ifNotGranted("ROLE_PERMISSION_MODIFY")>disabled="disabled"</#if>>
                        <option value="null" <#if !rp.dateGranted??>selected="selected"</#if>>
                            <@_ "recordPermission.granted.null" "To decide"/>
                        </option>
                        <option value="true" <#if rp.dateGranted?? && rp.granted>selected="selected"</#if>>
                            <@_ "recordPermission.granted.true" "Granted"/>
                        </option>
                        <option value="false" <#if rp.dateGranted?? && !rp.granted>selected="selected"</#if>>
                            <@_ "recordPermission.granted.false" "Denied"/>
                        </option>
                    </select>

                    <#if rp.record.parent??>
                        <label>
                            <input type="checkbox" name="parent_${rp.id?c}"/>
                            <@_ "recordPermission.parent" "Allow/Deny on a collection level"/>
                        </label>
                    </#if>
                </li>
                <li class="motivation">
                    <label for="motivation_${rp.id?c}">
                        <@_ "recordPermission.motivation" "Motivation"/><br/>
                        <span class="note">
                            (<@_  "permissionSingle.motivationLanguage" "Please specify in"/>
                            <@_ "language.${permission.requestLocale}" "English" />)
                        </span>
                    </label>
                    <textarea id="motivation_${rp.id?c}"
                              name="motivation_${rp.id?c}"><#if rp.motivation??>${rp.motivation?html}</#if></textarea>
                </li>
                <#if _sec.ifAllGranted("ROLE_PERMISSION_MODIFY")>
                    <li>
                        <a href="${rc.contextPath}/record/editform/${rp.record.pid?url}" target="_blank">
                            <@_ "permissionSingle.toRecord" "Go to Record"/>
                        </a>
                    </li>
                </#if>
            </ul>
        </fieldset>
    </#list>

    <#if _sec.ifAllGranted("ROLE_PERMISSION_MODIFY")>
        <#assign delete_label>
            <@_ "permissionSingle.delete" "Delete" />
        </#assign>
        <#assign save_label>
            <@_ "permissionSingle.save" "Save" />
        </#assign>
        <#assign save_and_email_label>
            <@_ "permissionSingle.saveAndEmail" "Save and email" />
        </#assign>
        <#assign deleteConfirm>
            <@_ "permissionSingle.confirmDelete" "Deletion of this permission request, including all the individual permissions set, is permanent. Are you sure you want to delete this permission request?" />
        </#assign>

        <input type="submit" name="save" value="${save_label}"/>
        <input type="submit" name="saveandemail" value="${save_and_email_label}"/>
        <#if _sec.ifAllGranted("ROLE_PERMISSION_DELETE")>
            <input type="submit" name="delete"
                   onClick="return confirm('${deleteConfirm}');"
                   value="${delete_label}"/>
        </#if>
    </#if>
</form>
</@base>