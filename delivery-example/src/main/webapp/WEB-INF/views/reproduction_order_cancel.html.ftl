<#include "base.ftl">

<#assign title>
<@_ "reproduction.order.cancel" "Payment was cancelled"/>
</#assign>

<@userbase title>
<section>
  <heading>
    <h1>${title}</h1>
  </heading>

  <section>
    <p><@_html "reproduction.order.cancel.message" "Unfortunately, you cancelled your payment." /></p>
  </section>

  <p><@_ "reproduction.backToSearch" "Close this tab and return to Search for new requests" /></p>
</section>
</@userbase>
