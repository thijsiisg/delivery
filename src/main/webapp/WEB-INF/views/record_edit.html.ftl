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

<#macro restrictionTypeRadio path prefix options>
  <@spring.bind path/>
    <label for="${spring.status.expression}0" class="field">
      <#assign msgName = prefix + path/>
      <@spring.messageText msgName spring.status.expression/>
    </label>

    <ul class="options">
      <#list options?keys as value>
      <#if value != "INHERIT" || record.parent??>
      <li>
        <#assign id="${spring.status.expression}${value_index}">
        <input type="radio" id="${id}" name="${spring.status.expression}" value="${value?html}"<#if spring.stringStatusValue == value> checked="checked" </#if>/>
        <#assign label = prefix + path + "." + options[value]?html />
        <label for="${id}"><@spring.messageText label options[value]?html/></label>
      </li>
      </#if>
      </#list>
    </ul>

    <#if spring.status.errorMessages?size != 0>
    <ul class="errors">
      <li>
        <@spring.showErrors "</li><li>"/>
      </li>
    </ul>
    </#if>
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
  <fieldset class="record_form">

    <legend><@_ "editrecord.set.record" "Metadata"/></legend>

    <ul class="form">
    <li><@restrictionTypeRadio "record.restrictionType" "" restriction_types/></li>

    <li>
    <@date "record.embargo" "" ""/>
    </li>

    <#assign rsClass = "">
    <#assign rsInfoClass = "hidden">
    <#assign rsStartTxt = useParentTxt>
    <#if record.parent?? && record.restrictionType == "INHERIT" && !record.restriction??>
        <#assign rsClass = "hidden">
        <#assign rsInfoClass = "">
        <#assign rsStartTxt = useChildTxt>
    </#if>
    <#assign rs = "">
    <#if record.parent?? && record.parent.realRestriction??>
        <#assign rs = record.parent.realRestriction>
    </#if>
    <li><@textarea "record.restriction" "" rsClass/>
        <textarea disabled="disabled" id="restrictionInfoField"
                  class="parentInfo ${rsInfoClass}">${rs}</textarea>
           <#if record.parent?? && record.restrictionType == "INHERIT"><a  class="parentInfoLink"
           onclick="toggleField('restrictionInfoField',
           'restriction', this, '${useParentTxt}', '${useChildTxt}');
                   ">${rsStartTxt}</a></#if>
    </li>

    <#assign csClass = "">
    <#assign csInfoClass = "hidden">
    <#assign csStartTxt = useParentTxt>
    <#if record.parent?? && !record.comments??>
        <#assign csClass = "hidden">
        <#assign csInfoClass = "">
        <#assign csStartTxt = useChildTxt>
    </#if>
    <#assign cs = "">
    <#if record.parent?? && record.parent.realComments??>
        <#assign cs = record.parent.realComments>
    </#if>
    <li><@textarea "record.comments" "" csClass/>
        <textarea disabled="disabled" id="commentsInfoField" class="parentInfo
        ${csInfoClass}">${cs}</textarea>
           <#if record.parent??><a  class="parentInfoLink"
           onclick="toggleField('commentsInfoField',
           'comments', this, '${useParentTxt}', '${useChildTxt}');
                   ">${csStartTxt}</a></#if>
    </li>
    </ul>
  </fieldset>

  <fieldset>
    <legend><@_ "editrecord.set.contact" "Contact Data"/></legend>
    <#assign contactClass = "contactField">
    <#assign contactInfoClass = "contactInfoField hidden">
    <#assign cStartTxt = useParentTxt>
    <#if record.parent?? && !record.contact??>
        <#assign contactClass = "contactField hidden">
        <#assign contactInfoClass = "contactInfoField">
        <#assign cStartTxt = useChildTxt>
    </#if>

    <#assign c = {}>
    <#if record.parent?? && record.parent.realContact??>
        <#assign c = record.parent.realContact>
    </#if>
    <#if record.parent??><a  class="parentInfoLink"
           onclick="toggleFieldsWithClass('contactInfoField',
           'contactField', this, '${useParentTxt}', '${useChildTxt}');
                   ">${cStartTxt}</a></#if>
    <ul class="form">

    <li><@input "contact.firstname" "" "record." contactClass/>
        <input type="text" disabled="disabled" class="parentInfo ${contactInfoClass}" value="${c.firstname!""}" id="contact.firstnameInfo"/>

    </li>
    <li><@input "contact.preposition" "" "record." contactClass/>
        <input type="text" disabled="disabled" class="parentInfo ${contactInfoClass}" value="${c.preposition!""}" id="contact.prepositionInfo"/>
    </li>
    <li><@input "contact.lastname" "" "record." contactClass/>
        <input type="text" disabled="disabled" class="parentInfo ${contactInfoClass}" value="${c.lastname!""}" id="contact.lastnameInfo"/>
    </li>
    <li><@input "contact.email" "" "record." contactClass/>
        <input type="text" disabled="disabled" class="parentInfo ${contactInfoClass}" value="${c.email!""}" id="contact.emailInfo"/>
    </li>
    <li><@input "contact.phone" "" "record." contactClass/>
        <input type="text" disabled="disabled" class="parentInfo ${contactInfoClass}" value="${c.phone!""}" id="contact.phoneInfo"/>
    </li>
    <li><@input "contact.fax" "" "record." contactClass/>
        <input type="text" disabled="disabled" class="parentInfo ${contactInfoClass}" value="${c.fax!""}" id="contact.faxInfo"/>
    </li>
    <li><@input "contact.address" "" "record." contactClass/>
        <input type="text" disabled="disabled" class="parentInfo ${contactInfoClass}" value="${c.address!""}" id="contact.addressInfo"/>
    </li>
    <li><@input "contact.zipcode" "" "record." contactClass/>
        <input type="text" disabled="disabled" class="parentInfo ${contactInfoClass}" value="${c.zipcode!""}" id="contact.zipcodeInfo"/>
    </li>
    <li><@input "contact.location" "" "record." contactClass/>
        <input type="text" disabled="disabled" class="parentInfo ${contactInfoClass}" value="${c.location!""}" id="contact.locationInfo"/>
    </li>
    <li><@input "contact.country" "" "record." contactClass/>
        <input type="text" disabled="disabled" class="parentInfo ${contactInfoClass}" value="${c.country!""}" id="contact.country"/>
    </li>
    </ul>
  </fieldset>



<fieldset class="holdings">
  <legend><@_ "editrecord.set.holdings" "Holdings"/></legend>
  <table class="holdings">
    <thead><tr>
        <td><@_ "holding.signature" "Signature/Type"/></td>
        <td><@_ "holding.floor" "Floor"/></td>
        <td><@_ "holding.direction" "Direction"/></td>
        <td><@_ "holding.cabinet" "Cabinet"/></td>
        <td><@_ "holding.shelf" "Shelf"/></td>
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

        <td class="signature">${(h.signature!'')?html}
        <#else>
        <td><@input_nolabel "record.holdings[${idx}].signature"/></td>
        </#if>
        <td><@input_nolabel "record.holdings[${idx}].floor"/></td>
        <td><@input_nolabel "record.holdings[${idx}].direction"/></td>
        <td><@input_nolabel "record.holdings[${idx}].cabinet"/></td>
        <td><@input_nolabel "record.holdings[${idx}].shelf"/></td>
        <td><@select_nolabel "record.holdings[${idx}].usageRestriction" "holding.usageRestriction" usageRestriction_types/></td>
        <#if h.status == "AVAILABLE">
        <td class="green"><@_ "holding.statusType.AVAILABLE" "Available" /></td>
        <#else>
        <#--<#assign activeReservation = {}>
        <#list h.reservations as res>
            <#if res.status != "COMPLETED">
            <#assign activeReservation = res>
            <#break>
            </#if>
        </#list>
        <td class="orange"><a target="_blank" href="${rc.contextPath}/reservation/${activeReservation.id}"><@_  "holding.statusType.${h.status}" h.status?string /></a></td>-->
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
        <td><input type="text" id="holdings.new.floor"/></td>
        <td><input type="text" id="holdings.new.direction"/></td>
        <td><input type="text" id="holdings.new.cabinet"/></td>
        <td><input type="text" id="holdings.new.shelf"/></td>

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
