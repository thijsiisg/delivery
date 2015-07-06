<#include "base.ftl"/>
<#include "form.ftl"/>

<#-- Build the title -->
<#assign title>
  <@_ "reproduction.create" "Create Reproduction"/>
</#assign>

<#-- Build the page -->
<@preamble title />
<@userHeading />
<@body>
<h1>${title}</h1>

<ol class="progress on-any-non-custom" style="display:none;">
  <li class="step active">
    1. <@_ "reproduction.steps.request" "Request reproduction"/>
  </li>
  <li class="step">
    2. <@_ "reproduction.steps.confirm" "Confirm reproduction request"/>
  </li>
  <li class="step">
    3. <@_ "reproduction.steps.payment" "Payment of reproduction"/>
  </li>
  <li class="step">
    4. <@_ "reproduction.steps.delivery" "Delivery of reproduction"/>
  </li>
</ol>

<ol class="progress on-all-custom">
  <li class="step active">
    1. <@_ "reproduction.steps.request" "Request reproduction"/>
  </li>
  <li class="step">
    2. <@_ "reproduction.steps.wait" "Wait for offer"/>
  </li>
  <li class="step">
    3. <@_ "reproduction.steps.confirm" "Confirm reproduction request"/>
  </li>
  <li class="step">
    4. <@_ "reproduction.steps.payment" "Payment of reproduction"/>
  </li>
  <li class="step">
    5. <@_ "reproduction.steps.delivery" "Delivery of reproduction"/>
  </li>
</ol>

<section>
  <@form "" "reproduction" "create">
    <heading>
      <hgroup>
        <fieldset>
          <legend><@_ "reproduction.records" "Items"/></legend>

          <#assign idx = 0>
          <#list reproduction.holdingReproductions as hr>
            <#assign h = hr.holding>
            <#assign info = h.record.externalInfo>

            <input name="holdingReproductions[${idx}].holding" type="hidden" value="${h.id?c}"/>

            <h3>${info.title?html}</h3>

            <ul class="holdingDetails">
              <li>
                <span><@_ "record.externalInfo.materialType" "Material Type"/></span>
                <@_ "record.externalInfo.materialType.${info.materialType}" ""/>
              </li>

              <li>
                <span><@_ "holding.signature" "Signature"/></span>
                ${h.signature?html}
              </li>

              <#if info.author??>
                <li>
                  <span><@_ "record.externalInfo.author" "Author"/></span>
                  ${info.author?html}
                </li>
              </#if>

              <#if info.displayYear??>
                <li>
                  <span><@_ "record.externalInfo.displayYear" "Year"/></span>
                  ${info.displayYear?html}
                </li>
              </#if>

              <#if info.materialType == "SERIAL">
                <li>
                  <span><@_ "holdingReproductions.comment" "Comment" /> </span>
                  <@input_nolabel "reproduction.holdingReproductions[${idx}].comment" />

                  <#if h.externalInfo.serialNumbers??>
                    ( <strong><@_ "reproductionCreate.serialAvailable" ""/> :</strong> ${h.externalInfo.serialNumbers} )
                  </#if>
                </li>
              </#if>
            </ul>

            <#assign reproductionOptions = []/>
            <#if reproductionStandardOptions[info.materialType.name()]??>
              <#assign reproductionOptions = reproductionStandardOptions[info.materialType.name()]/>
            </#if>

            <@reproductionHoldingOptions "reproduction.holdingReproductions[${idx}].standardOption" reproductionOptions/>

            <#assign idx = idx + 1>
          </#list>
        </fieldset>
      </hgroup>
    </heading>

    <div class="reproduction_form">
      <fieldset>
        <@input "reproduction.customerName" ""/>
        <@input "reproduction.customerEmail" ""/>

        <label for="captcha_response_field" class="field">
          <@_ "captcha.explanation" "Type the following word to prevent spam" />
        </label>

        <div id="captcha_widget_div" class="field">
          <input type="text" id="captcha_response_field" name="captcha_response_field" value="" class="field"
                 autocomplete="off"/>
          <img src="<@spring.url relativeUrl="/captcha"/>" id="captcha_image"/>
          <a href="#" class="refreshCaptcha">
            <@_ "captcha.refresh" "Refresh captcha" />
          </a>
        </div>

        <#if captchaError?? >
          <ul class="errors">
            <li>
              <b>${captchaError?html}</b>
            </li>
          </ul>
        </#if>
      </fieldset>

      <@buttons>
        <@submit "reproduction" />
      </@buttons>
    </div>
  </@form>
</section>
</@body>

<#macro reproductionHoldingOptions path options>
  <@spring.bind path/>

  <ul class="holdingDetails">
    <#list options as value>
      <li>
        <#assign id="${spring.status.expression}.${value_index}">
        <input type="radio" id="${id}" name="${spring.status.expression}"
               value="${value.id?c}"<#if spring.stringStatusValue == value> checked="checked" </#if>/>

        <ul class="reproductionDetails">
          <li>
            <#assign label=path + "." + value.optionName?html />
            <label for="${id}"><@spring.messageText label value.optionName?html/></label>
          </li>

          <li>
            <em>${value.optionDescription?html}</em>
          </li>

          <li class="spacing">
            <span><@_ "reproductionStandardOption.price" "Price"/></span>
            &euro; ${value.price?string("0.00")}
          </li>

          <li>
            <span><@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/></span>
            ${value.deliveryTime?html} <@_ "days" "days"/>
          </li>
        </ul>
      </li>
    </#list>

    <li>
      <#assign id="${spring.status.expression}.null">
        <input type="radio" id="${id}" name="${spring.status.expression}" class="custom"
               value="0"<#if spring.stringStatusValue == ""> checked="checked" </#if>/>

        <ul class="reproductionDetails">
          <li>
            <label for="${id}"><@_ "reproduction.customReproduction" "Custom reproduction"/></label>
          </li>

          <li>
            <em><@_ "reproduction.customReproductionCustomer.description" ""/></em>
          </li>

          <li class="spacing">
            <span><@_ "reproductionStandardOption.price" "Price"/></span>
            <@_ "tbd" "To be determined"/>
          </li>

          <li>
            <span><@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/></span>
            <@_ "tbd" "To be determined"/>
          </li>

          <li class="spacing">
            <@textarea_nolabel "reproduction.holdingReproductions[${idx}].customReproductionCustomer"/>
          </li>
        </ul>
    </li>
  </ul>
</#macro>