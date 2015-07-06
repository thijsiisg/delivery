<#include "base.ftl">

<#assign title>
<@_ "reproduction.order.accept" "Payment successfully completed"/>
</#assign>

<@userbase title>
<section>
  <heading>
    <h1>${title}</h1>
  </heading>

  <section>
    <p><@_html "reproduction.order.accept.message" "Within a few minutes you will receive an email from us confirming your payment. You will also receive a second email from our payment provider confirming your payment." /></p>
  </section>

  <p><@_ "reproduction.backToSearch" "Close this tab and return to Search for new requests" /></p>
</section>
</@userbase>
