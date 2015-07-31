<#include "base.ftl"/>
<#include "form.ftl"/>

<#-- Build the title -->
<#assign title>
  <@_ "reproduction.confirm" "Confirm Reproduction"/>
</#assign>

<#-- Build the page -->
<@preamble title />
<@userHeading />
<@body>
<h1>${title}</h1>

<#if reproduction.isOfferReadyImmediatly()>
  <ol class="progress on-all-non-custom">
    <li class="step">
      1. <@_ "reproduction.steps.request" "Request reproduction"/>
    </li>
    <li class="step active">
      2. <@_ "reproduction.steps.confirm" "Confirm reproduction request"/>
    </li>
    <li class="step">
      3. <@_ "reproduction.steps.payment" "Payment of reproduction"/>
    </li>
    <li class="step">
      4. <@_ "reproduction.steps.delivery" "Delivery of reproduction"/>
    </li>
  </ol>
<#else>
  <ol class="progress on-any-custom">
    <li class="step">
      1. <@_ "reproduction.steps.request" "Request reproduction"/>
    </li>
    <li class="step">
      2. <@_ "reproduction.steps.wait" "Wait for offer"/>
    </li>
    <li class="step active">
      3. <@_ "reproduction.steps.confirm" "Confirm reproduction request"/>
    </li>
    <li class="step">
      4. <@_ "reproduction.steps.payment" "Payment of reproduction"/>
    </li>
    <li class="step">
      5. <@_ "reproduction.steps.delivery" "Delivery of reproduction"/>
    </li>
  </ol>
</#if>

<#if paywayError??>
  <div class="errors">${paywayError?html}</div>
</#if>

<#if error??>
  <div class="errors"><@_ "reproduction.error."+error error /></div>
</#if>

<section>
  <@form "" "reproduction" "confirm">
    <heading>
      <hgroup>
        <fieldset>
          <legend><@_ "reproduction.records" ""/></legend>

          <#list reproduction.holdingReproductions as hr>
            <#assign h = hr.holding>
            <#assign info = h.record.externalInfo>

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
                  ${hr.comment?html}
                </li>
              </#if>
            </ul>

            <ul class="reproductionDetails create">
              <#if hr.standardOption??>
                <li>
                  <span>${hr.standardOption.optionName?html}</span>
                  &nbsp;
                </li>

                <li>
                  <em>${hr.standardOption.optionDescription?html}</em>
                </li>
              <#else>
                <li>
                  <span><@_ "reproduction.customReproduction" "Custom reproduction"/></span>
                  &nbsp;
                </li>

                <li>
                  <em>${hr.customReproductionCustomer?html}</em>
                </li>

                <#if hr.customReproductionReply??>
                  <li class="spacing">
                    <em>${hr.customReproductionReply?html}</em>
                  </li>
                </#if>
              </#if>

              <li class="spacing">
                <span><@_ "reproductionStandardOption.price" "Price"/></span>
                &euro; ${hr.price?string("0.00")}
              </li>

              <li>
                <span><@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/></span>
                ${hr.deliveryTime?html} <@_ "days" "days"/>
              </li>
            </ul>
          </#list>
        </fieldset>
      </hgroup>
    </heading>

    <div class="reproduction_confirm_form">
      <fieldset>
        <#if reproduction.deliveryTimeComment??>
          <div class="deliveryComment">
            <label><@_ "reproduction.deliveryTimeComment" "Expected delivery time"/>:</label>
            <em>${reproduction.deliveryTimeComment?html}</em>
          </div>
        </#if>

        <table>
          <thead>
            <tr>
              <td><@_ "reproduction.record" "Item"/></td>
              <td class="price_column"><@_ "reproductionStandardOption.price" "Price"/></td>
              <td><@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/></td>
            </tr>
          </thead>
          <tbody>
            <#list reproduction.holdingReproductions as hr>
              <#assign h = hr.holding/>
              <tr>
                <td>${h.record.externalInfo.title?html} - ${h.signature?html}</td>
                <td class="price_column">&euro; ${hr.price?string("0.00")}</td>
                <td>${hr.deliveryTime?html} <@_ "days" "days"/></td>
              </tr>
            </#list>

            <#if reproduction.copyrightPrice gt 0>
              <tr class="additional">
                <td class="label"><@_ "reproduction.copyright" "Copyright"/>:</td>
                <td class="price_column">&euro; ${reproduction.copyrightPrice?string("0.00")}</td>
                <td>&nbsp;</td>
              </tr>
            </#if>

            <#if reproduction.discount gt 0>
              <tr class="additional">
                <td class="label"><@_ "reproduction.discount" "Discount"/>:</td>
                <td class="price_column">&euro; ${reproduction.discount?string("0.00")}</td>
                <td>&nbsp;</td>
              </tr>
            </#if>

            <tr class="total">
              <td class="label"><@_ "total" "Total"/>:</td>
              <td class="price_column">&euro; ${reproduction.getTotalPrice()?string("0.00")}</td>
              <td>${reproduction.getEstimatedDeliveryTime()?html} <@_ "days" "days"/></td>
            </tr>
          </tbody>
        </table>

        <div class="accept">
          <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin ac hendrerit purus. Praesent eu consequat nunc, in tincidunt nisl. Donec pretium suscipit nunc id blandit. Duis nec posuere mauris. Maecenas ornare mauris ut mattis eleifend. Praesent elementum magna tellus, sit amet suscipit nulla lacinia id. Vivamus sit amet ante sit amet velit eleifend condimentum vitae a ex. Suspendisse et massa at ante eleifend porta. Nullam et semper arcu. Cras tristique, nulla quis cursus congue, risus eros pulvinar erat, nec efficitur purus ex fringilla urna. Vestibulum porttitor sapien leo, eu efficitur mauris fringilla at. Mauris tincidunt felis vitae neque euismod, aliquam tincidunt magna pretium. Sed massa nisl, interdum sed mollis a, lacinia vitae purus. Morbi tempus efficitur sapien. Praesent sed dolor sed odio euismod aliquam nec quis orci.</p>

          <label>
            <input type="checkbox" id="accept_terms_conditions" name="accept_terms_conditions" value="accept"/>
            I accept the terms and conditions
          </label>

          <#if acceptError??>
            <span class="errors">${acceptError?html}</span>
          </#if>
        </div>
      </fieldset>

      <@buttons>
        <@submit "reproduction" />
      </@buttons>
    </div>
  </@form>
</section>
</@body>
