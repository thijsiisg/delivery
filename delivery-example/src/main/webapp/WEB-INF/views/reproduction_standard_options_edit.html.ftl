<#include "base.ftl"/>
<#include "form.ftl"/>

<#assign title>
  <@_ "" "Standard reproduction options"/>
</#assign>

<#-- Build the page -->
<@preamble title/>
<@heading/>
<@body>
<@form "" "standardOptions">
  <h1>${title}</h1>
  <fieldset>
    <table class="reproduction_standard_options">
      <thead>
        <tr>
          <td>Option name</td>
          <td>Option description</td>
          <td>Material type</td>
          <td>SOR level</td>
          <td>Price</td>
          <td>Delivery time</td>
        </tr>
      </thead>

      <#assign idx = 0>
      <#list standardOptions.items as stOption>
        <tr class="standard_option" id="standard_option${idx}">
          <td><@input_nolabel "standardOptions.items[${idx}].optionName"/></td>
          <td><@textarea_nolabel "standardOptions.items[${idx}].optionDescription"/></td>
          <td><@select_nolabel "standardOptions.items[${idx}].materialType" "items${idx}.materialType" materialTypes/></td>
          <td><@select_nolabel "standardOptions.items[${idx}].level" "items${idx}.level" levels/></td>
          <td><@input_nolabel "standardOptions.items[${idx}].price" "small"/></td>
          <td><@input_nolabel "standardOptions.items[${idx}].deliveryTime" "small"/></td>
        </tr>
        <#assign idx = idx + 1>
      </#list>

      <tfoot>
        <tr id="newHolding" class="hidden">
          <td><input type="text" id="standardOptions.items.new.optionName"/></td>
          <td><textarea id="standardOptions.items.new.optionDescription"></textarea></td>
          <td>
            <select id="standardOptions.items.new.materialType" class="field">
              <#list materialTypes?keys as value>
                <#assign label = "stOptions.materialType." + materialTypes[value]?html/>
                <option value="${value?html}">
                  <@spring.messageText label materialTypes[value]?html/>
                </option>
              </#list>
            </select>
          </td>
          <td>
            <select id="standardOptions.items.new.level" class="field">
              <#list levels?keys as value>
                <#assign label = "stOptions.level." + levels[value]?html/>
                <option value="${value?html}">
                  <@spring.messageText label levels[value]?html/>
                </option>
              </#list>
            </select>
          </td>
          <td><input type="text" id="standardOptions.items.new.price" class="small"/></td>
          <td><input type="text" id="standardOptions.items.new.deliveryTime" class="small"/></td>
        </tr>
      </tfoot>
    </table>
  </fieldset>

  <@buttons>
    <@submit "reproduction" />
  </@buttons>
</@form>
</@body>
