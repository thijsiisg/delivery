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
<#if isNewRecord??><@_ "newrecord.title" "New Record"/><#else><@_ "editrecord.title" "Edit Record"/></#if>
</#assign>

<#macro fakeInput path value>
  <@spring.bind path/>
    <input type="text" id="${spring.status.expression}"
      name="${spring.status.expression}"
      value="${value?html}"/>
</#macro>

<#macro parentTree rec>
    <#if rec.parent??>
      <@parentTree rec.parent/>
    <#else>
    <ul class="record_tree">
      <li>
        <a <#if rec == record>class="current"</#if> href="${rc.contextPath}/record/editform/${rec.pid?url}">
          ${rec.title?html} (${rec.pid?html})
        </a>
        <ul class="parent_tree">
          <@tree rec/>
              <#if !isNewRecord??>
              <#assign item = record>
              <#if record.parent??><#assign item = record.parent></#if>

        </ul>
              <ul>
                  <li class="edit_item">
            <@form_plain "" "edititem">
              <input type="hidden" name="edit" value="${item.pid?html}"/>
              <label for="${item.pid?html}_item">
                <@_ "edititem.label" "Sub-Item:"/>
              </label>
              <input type="text" id="${item.pid?html}_item" name="item"
                     value="" />
              <@submit "edititem"/>
            </@form_plain>
           </#if>
          </li>
              </ul>
      </li>
      </ul>
    </#if>
</#macro>

<#macro tree rec>
  <#list rec.children as child>
  <li>
    <a <#if child.pid == record.pid>class="current"</#if> href="${rc.contextPath}/record/editform/${child.pid?url}">
      <#assign length = rec.pid?length />
      <@_ "editrecord.item" "Item"/> ${child.pid?substring(length + 1)?html}
    </a>

    <ul class="record_tree">
      <@tree child/>
    </ul>
  </li>
  </#list>
</#macro>

<#assign useParentTxt><@_ "editrecord.useParent" "Use Parent Data"/></#assign>
<#assign useChildTxt><@_ "editrecord.useChild" "Specify Child Data"/></#assign>


<@base "${title}">
<#assign info = record.externalInfo>
  <h1>${title}</h1>
  <h3>${record.pid?html}: ${record.title?html} (<@_ "record.externalInfo.materialType.${info.materialType}" ""/>)</h3>



<#if info.materialType == "ARCHIVE">
<nav class="related_records">
  <h1><@_ "editrecord.relatedRecords" "Related Records"/></h1>
  <@parentTree record/>
</nav>
</#if>

<#assign parentClass = "hidden">
<#assign childClass = "">
<#if record.parent??>
<#assign childClass = "hidden">
<#assign parentClass = "">
</#if>

<@form "" "record" "save">
<fieldset class="holdings">
  <legend><@_ "editrecord.set.holdings" "Holdings"/></legend>
  <table class="holdings">
    <thead><tr>
        <td><@_ "holding.signature" "Signature/Type"/></td>
        <td><@_ "holding.usageRestriction" "Usage Restriction"/></td>
        <td><@_ "holding.status" "Status"/></td>
        <td></td>
    </tr></thead>


    <#assign idx = 0>
    <#list record.holdings as h>

    <tr class="holding" id="holding${idx}">
        <@spring.bind "record.holdings[${idx}].signature" />
        <#if spring.status.errorMessages?size == 0>
        <input type="hidden" id="${spring.status.expression}"
        name="${spring.status.expression}" value="${(h.signature!'')?html}"/>

        <td class="signature">${(h.signature!'')?html}</td>
        <#else>
        <td><@input_nolabel "record.holdings[${idx}].signature"/></td>
        </#if>
        <td><@select_nolabel "record.holdings[${idx}].usageRestriction" "holding.usageRestriction" usageRestriction_types/></td>
        <#if h.status == "AVAILABLE">
        <td class="green"><@_ "holding.statusType.AVAILABLE" "Available" /></td>
        <#else>
        <td class="orange"><@_  "holding.statusType.${h.status}" h.status?string /></td>
        </#if>

       <td>
       <#assign deleteHoldingConfirm>
       <@_ "deleteHolding.confirmDelete" "Are you sure you want to delete the holding?" />
       </#assign>
        <input type="button" onclick="var y = confirm('${deleteHoldingConfirm}'); if (y) {$(this).parent().parent().remove();renumberHoldings();}" value="<@_ "deleteHolding.submit" "Remove"/>"/>
       </td>
    </tr>
    <#assign idx = idx + 1>
    </#list>
      <tfoot>
    <tr id="newHolding"class="hidden">
        <td><input type="text" id="holdings.new.signature"/></td>
        <td><select id="holdings.new.usageRestriction"
                    class="field">
              <#list usageRestriction_types?keys as value>
               <#assign label = "holding.usageRestriction." + usageRestriction_types[value]?html />
                <option value="${value?html}"><@spring.messageText label  usageRestriction_types[value]?html/></option>
              </#list>
            </select>
       </td>
        <td class="green"><@_ "holding.statusType.AVAILABLE" "Available"/></td>
       <td>
        <input type="button" onclick="$(this).parent().parent().remove();
                renumberHoldings();" value="<@_ "deleteHolding.submit" "Remove"/>"/>
       </td>
    </tr></tfoot>
</table>
    <div  class="btlist">
      <input id="newHoldingButton" type="button" onclick="addNewHolding();"
             value="<@_ "addHolding.submit" "Add New"/>"/>
    </div>
</fieldset>

<div class="btlist">
  <@buttons>
    <@submit "editrecord" />
  </@buttons>
</div>
</@form>
<div class="btlist">
    <#if _sec.ifAllGranted("ROLE_RECORD_DELETE")>
    <#assign deleteConfirm>
    <@_ "editrecord.confirmDelete" "Are you sure you want to delete the record?" />
    </#assign>
<@buttons>
  <@button "" "delete" "editrecord.delete" "return confirm('${deleteConfirm}');"/>
</@buttons>
    </#if>
</div>
</@base>
