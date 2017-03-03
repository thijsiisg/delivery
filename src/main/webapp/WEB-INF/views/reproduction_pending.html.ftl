<#import "spring.ftl" as spring/>
<#include "base.ftl">
<#include "localization.ftl">

<#assign title>
  <@_ "reproduction.success" "Your reproduction request has been received."/>
</#assign>

<@userbase title>
<section>
  <heading>
    <h1>${title}</h1>
  </heading>

  <ol class="progress on-any-custom">
    <li class="step">
      1. <@_ "reproduction.steps.request" "Request reproduction"/>
    </li>
    <li class="step active">
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
    <ul class="items spacing">
      <#list reproduction.holdingReproductions as hr>
        <#assign h = hr.holding>
        <#assign info = h.record.externalInfo>

        <li>
          <#if hr.standardOption??>
            <strong>${hr.standardOption.optionName?html}</strong> <br/>
          <#else>
            <strong><@_ "reproduction.customReproduction" "Request offer"/></strong> <br/>
          </#if>

          ${h.record.title?html} <#if info.author??>/ ${info.author}</#if>
          - ${h.signature?html} <#if hr.comment??>- ${hr.comment}</#if>
          <br/>

          <#if hr.standardOption??>
            <em>
              <@_ "reproductionStandardOption.price" "Price"/>: &euro; ${hr.completePrice?string("0.00")} -
              <@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/>: ${hr.deliveryTime?html} <@_ "days" "days"/>
            </em>
          <#else>
            <em>
              <@_ "reproductionStandardOption.price" "Price"/>: <@_ "tbd" "To be determined"/> -
              <@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/>: <@_ "tbd" "To be determined"/>
            </em>

            <br/>

            <@_ "reproduction.customReproductionCustomer" "Your wish"/>: <br/>
            <em>${hr.customReproductionCustomer?html}</em>
          </#if>
        </li>
      </#list>
    </ul>
  </section>

  <p><@_html "reproduction.requestSuccess" "Your reproduction request has been successfully received. You will soon receive an offer from us." /></p>

  <#if error?? >
    <p><@_ "reproduction.error."+error error /></p>
  </#if>

  <p><@_ "reproduction.backToSearch" "Close this tab and return to Search for new requests" /></p>
</section>
</@userbase>
