<#include "base.ftl"/>
<#include "form.ftl"/>

<#assign title>
  <@_ "reproductionStandardOption.title" "Standard reproduction options"/>
</#assign>

<#-- Build the page -->
<@preamble title/>
<@heading/>
<@body>
  <@form "" "standardOptions">
  <h1>${title}</h1>

  <table class="reproduction_standard_options">
    <thead>
    <tr>
      <td><@_ "reproductionStandardOption.optionName" "Option name"/></td>
      <td><@_ "reproductionStandardOption.optionDescription" "Option description"/></td>
      <td><@_ "reproductionStandardOption.materialType" "Material type"/></td>
      <td><@_ "reproductionStandardOption.level" "SOR level"/></td>
      <td><@_ "reproductionStandardOption.price" "Price"/></td>
      <td><@_ "reproductionStandardOption.deliveryTime" "Delivery time"/></td>
    </tr>
    </thead>

    <tbody>
      <#assign idx = 0>
      <#list standardOptions.options as stOption>
      <tr class="standard_option">
        <td>
          <input type="hidden" name="options[${idx}].id" value="${standardOptions.options[idx].id}" class="id"/>

          <label>
            <span>NL:</span>
            <@input_nolabel "standardOptions.options[${idx}].optionNameNL"/>
          </label>
          <label>
            <span>EN:</span>
            <@input_nolabel "standardOptions.options[${idx}].optionNameEN"/>
          </label>
        </td>
        <td>
          <label>
            <span>NL:</span>
            <@textarea_nolabel "standardOptions.options[${idx}].optionDescriptionNL"/>
          </label>
          <label>
            <span>EN:</span>
            <@textarea_nolabel "standardOptions.options[${idx}].optionDescriptionEN"/>
          </label>
        </td>
        <td>
          <@select_nolabel "standardOptions.options[${idx}].materialType" "record.externalInfo.materialType" materialTypes/>
        </td>
        <td>
          <@select_nolabel "standardOptions.options[${idx}].level" "reproductionStandardOption.level" levels/>
        </td>
        <td>
          <label>
            <@spring.formInput "standardOptions.options[${idx}].price" "class='small'"/>
            <span>EUR</span>
            <#if spring.status.errorMessages?size != 0>
              <ul class="errors">
                <li>
                  <@spring.showErrors "</li><li>"/>
                </li>
              </ul>
            </#if>
          </label>
        </td>
        <td>
          <label>
            <@spring.formInput "standardOptions.options[${idx}].deliveryTime" "class='small'"/>
            <span><@_ "days" "days"/></span>
            <#if spring.status.errorMessages?size != 0>
              <ul class="errors">
                <li>
                  <@spring.showErrors "</li><li>"/>
                </li>
              </ul>
            </#if>
          </label>
        </td>
      </tr>
        <#assign idx = idx + 1>
      </#list>
    </tbody>

    <tfoot>
    <tr id="newStandardOption" class="hidden">
      <td>
        <label>
          <span>NL:</span>
          <input type="text" id="new.optionNameNL" name="new.optionNameNL">
        </label>
        <label>
          <span>EN:</span>
          <input type="text" id="new.optionNameEN" name="new.optionNameEN">
        </label>
      </td>
      <td>
        <label>
          <span>NL:</span>
          <textarea id="new.optionDescriptionNL" name="new.optionDescriptionNL"
                    class="field"></textarea>
        </label>
        <label>
          <span>EN:</span>
          <textarea id="new.optionDescriptionEN" name="new.optionDescriptionEN"
                    class="field"></textarea>
        </label>
      </td>
      <td>
        <select id="new.materialType" name="new.materialType" class="field">
          <#list materialTypes?keys as value>
            <#assign label = "record.externalInfo.materialType." + materialTypes[value]?html/>
            <option value="${value?html}">
              <@spring.messageText label materialTypes[value]?html/>
            </option>
          </#list>
        </select>
      </td>
      <td>
        <select id="new.level" name="new.level" class="field">
          <#list levels?keys as value>
            <#assign label = "reproductionStandardOption.level." + levels[value]?html/>
            <option value="${value?html}">
              <@spring.messageText label levels[value]?html/>
            </option>
          </#list>
        </select>
      </td>
      <td>
        <label>
          <input type="text" id="new.price" name="new.price" value="${0?string('0.00')}" class="small"/>
          <span>EUR</span>
        </label>
      </td>
      <td>
        <label>
          <input type="text" id="new.deliveryTime" name="new.deliveryTime" value="0" class="small">
          <span><@_ "days" "days"/></span>
        </label>
      </td>
    </tr>
    </tfoot>
  </table>

    <@buttons>
      <@submit "reproduction" />
      <button type="button" onclick="addNewStandardOption();">Add new option</button>
    </@buttons>
  </@form>
</@body>
