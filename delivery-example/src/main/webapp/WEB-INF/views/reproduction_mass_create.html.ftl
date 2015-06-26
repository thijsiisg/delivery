<#include "base.ftl"/>
<#include "form.ftl" />

<#-- Build the title -->
<#assign title>
  <#if original??>
    <@_ "reproductionEdit.title" "Edit Reproduction"/> ${original.id?html}
  <#else>
    <@_ "reproductionMassCreate.title" "New Reproduction"/>
  </#if>
</#assign>

<#-- Build the page -->
<@preamble title/>
<@heading/>
<@body>
<h1>${title}</h1>
<fieldset class="reproduction_form">
<@form "" "reproduction">
  <ul class="form">
    <li><@input "reproduction.customerName" ""/></li>
    <li><@input "reproduction.customerEmail" ""/></li>
    <li><@input "reproduction.comment" ""/></li>
  </ul>

  <ul id="holdingReproductions" class="holdingReproductionDetails actions">
    <li id="newHoldingReproduction" class="hidden">
      <input type="button" class="removeButton"
             onclick="removeNewHoldingReproduction($(this).parent());renumberHoldingReproductions();"
             value="<@_ "deleteHolding.submit"/>"/>
      <input type="button" class="addButton" onclick="addNewHoldingReproduction($(this) .parent());"
             value="<@_ "addHolding.submit"/>"/>
    </li>

    <#assign i = 0>
    <#list reproduction.holdingReproductions as hr>
      <#assign info = hr.holding.record.externalInfo>

      <#assign reproductionOptions = []/>
      <#if reproductionStandardOptions[info.materialType.name()]??>
        <#assign reproductionOptions = reproductionStandardOptions[info.materialType.name()]/>
      </#if>

      <li class="holdingReproductions" id="holdingReproduction${i}">
        <input type="button" class="removeButton"
               onclick="removeNewHoldingReproduction($(this).parent());renumberHoldingReproductions();"
               value="<@_ "deleteHolding.submit"/>"/>
        <input type="hidden" id="holdingReproductions[${i}].holding" name="holdingReproductions[${i}].holding"
               class="holding" value="${hr.holding.id?c}"/>

        <ul class="inner">
          <li>
            <#if hr.holding.status != "AVAILABLE" || hr.holding.record.realRestrictionType == "CLOSED">
              <span class="red">
            <#else>
              <span class="green">
            </#if>

            ${hr.holding.record.title?html} <#if info.author??>/
            ${info.author}</#if> - ${hr.holding.signature?html}</span>
          </li>

          <li>
            <#list reproductionOptions as value>
              <label class="group">
                <input type="radio" class="standardOption" name="holdingReproductions[${i}].standardOption"
                       value="${value.id?c}"<#if hr.standardOption?? && hr.standardOption.id == value.id> checked="checked"</#if>/>
                ${value.optionName?html}
              </label>

              <span>
                (&euro; ${value.price?string("0.00")} - ${value.deliveryTime?html} <@_ "days" "days"/>)
              </span>
            </#list>

            <label class="group">
              <input type="radio" name="holdingReproductions[${i}].standardOption"
                     class="standardOption custom" value="0" <#if !hr.standardOption??> checked="checked"</#if>/>
              <@_ "reproduction.customReproduction" "Custom reproduction"/>
            </label>
          </li>

          <#if info.materialType == "SERIAL">
            <li>
              <label class="group">
                <strong class="label"><@_ "holdingReproductions.comment" "" /></strong>
                <@input_nolabel "reproduction.holdingReproductions[${i}].comment" "comment" />

                <#if hr.holding.externalInfo.serialNumbers??>
                  ( <@_ "reproductionCreate.serialAvailable" ""/>:
                ${hr.holding.externalInfo.serialNumbers} )
                </#if>
              </label>
            </li>
          </#if>

          <li class="on-custom" <#if hr.standardOption??> style="display:none;"</#if>>
            <label class="group">
              <strong class="label"><@_ "reproductionStandardOption.price" "Price"/></strong>
              <@input_nolabel "reproduction.holdingReproductions[${i}].price" "price" />
            </label>
          </li>

          <li class="on-custom" <#if hr.standardOption??> style="display:none;"</#if>>
            <label class="group">
              <strong class="label"><@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/></strong>
              <@input_nolabel "reproduction.holdingReproductions[${i}].deliveryTime" "deliveryTime" />
            </label>
          </li>

          <li class="on-custom" <#if hr.standardOption??> style="display:none;"</#if>>
            <label class="group">
              <strong class="label"><@_ "reproduction.customReproductionCustomer.backend" "Customer's wish"/></strong>
              <@textarea_nolabel "reproduction.holdingReproductions[${i}].customReproductionCustomer" "customReproductionCustomer" />
            </label>
          </li>

          <li class="on-custom" <#if hr.standardOption??> style="display:none;"</#if>>
            <label class="group">
              <strong class="label"><@_ "reproduction.customReproductionReply" "Reply on wish"/></strong>
              <@textarea_nolabel "reproduction.holdingReproductions[${i}].customReproductionReply" "customReproductionReply" />
            </label>
          </li>
        </ul>
      </li>

      <#assign i = i + 1>
    </#list>
  </ul>

  <ul class="form">
    <li>
      <label for="mail" class="field"><@_ "reproductionMassCreate.mail" "Mail"/></label>
      <input type="checkbox" name="mail" id="mail" class="field"/>
    </li>
  </ul>
</fieldset>

<fieldset class="actions">
  <legend><@_ "reproductionMassCreate.search" "Search for Holdings to Add"/></legend>

  <ul>
    <li>
      <label for="searchTitle"><@_ "record.title" "Title"/></label>
      <input id="searchTitle" type="text" name="searchTitle" value="${RequestParameters.searchTitle!""}"/>
    </li>
    <li>
      <label for="searchSignature"><@_ "holding.signature" "Signature"/></label>
      <input id="searchSignature" type="text" name="searchSignature" value="${RequestParameters.searchSignature!""}"/>

      <#assign searchLabel>
        <@_ "reproductionMassCreate.searchSubmit" "Search"/>
      </#assign>
    </li>
  </ul>

  <input type="submit" name="searchSubmit" value="${searchLabel}"/>

  <#if holdingList??>
    <ul id="holdingSearch" class="holdingReproductionDetails">
      <#assign noResults = holdingList?size == 0>

      <#list holdingList as h>
        <#assign info = h.record.externalInfo>

        <#assign reproductionOptions = []/>
        <#if reproductionStandardOptions[info.materialType.name()]??>
          <#assign reproductionOptions = reproductionStandardOptions[info.materialType.name()]/>
        </#if>

        <#if h.status != "AVAILABLE" || h.record.realRestrictionType == "CLOSED">
          <li>
            <span class="red">${h.record.title?html} <#if info.author??>/
            ${info.author}</#if> - ${h.signature?html}</span>
          </li>
        <#else>
          <li>
            <input type="button" class="addButton" onclick="addNewHoldingReproduction($(this).parent());"
                   value="<@_ "addHolding.submit"/>"/>
            <input type="hidden" class="holding" value="${h.id?c}"/>

            <ul class="inner">
              <li>
                <span class="green">${h.record.title?html} <#if info.author??>/
                ${info.author}</#if> - ${h.signature?html}</span>
              </li>

              <li class="hidden">
                <#list reproductionOptions as value>
                  <label class="group">
                    <input type="radio" class="standardOption" value="${value.id?c}"/>
                    ${value.optionName?html}
                  </label>

                  <span>
                    (&euro; ${value.price?string("0.00")} - ${value.deliveryTime?html} <@_ "days" "days"/>)
                  </span>
                </#list>

                <label class="group">
                  <input type="radio" class="standardOption custom" value="0" checked="checked"/>
                  <@_ "reproduction.customReproduction" "Custom reproduction"/>
                </label>
              </li>

              <#if info.materialType == "SERIAL">
                <li class="hidden">
                  <label class="group">
                    <strong class="label"><@_ "holdingReproductions.comment" "" /></strong>
                    <input type="text" class="comment"/>

                    <#if h.externalInfo.serialNumbers??>
                      ( <strong><@_ "reproductionCreate.serialAvailable" ""/>:</strong>
                    ${h.externalInfo.serialNumbers} )
                    </#if>
                  </label>
                </li>
              </#if>

              <li class="hidden on-custom">
                <label class="group">
                  <strong class="label"><@_ "reproductionStandardOption.price" "Price"/></strong>
                  <input type="text" class="price" value="${0?string("0.00")}"/>
                </label>
              </li>

              <li class="hidden on-custom">
                <label class="group">
                  <strong class="label"><@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/></strong>
                  <input type="text" class="deliveryTime" value="0"/>
                </label>
              </li>

              <li class="hidden on-custom">
                <label class="group">
                  <strong class="label"><@_ "reproduction.customReproductionCustomer.backend" "Customer's wish"/></strong>
                  <textarea class="customReproductionCustomer"></textarea>
                </label>
              </li>

              <li class="hidden on-custom">
                <label class="group">
                  <strong class="label"><@_ "reproduction.customReproductionReply" "Reply on wish"/></strong>
                  <textarea class="customReproductionReply"></textarea>
                </label>
              </li>
            </ul>
          </li>
        </#if>
      </#list>

      <#if noResults>
        <li>
          <span><@_ "search.notfound" "No results..."/></span>
        </li>
      </#if>
    </ul>
  </#if>
</fieldset>

<@buttons>
  <@submit "reproduction" />
</@buttons>

</@form>
</@body>
