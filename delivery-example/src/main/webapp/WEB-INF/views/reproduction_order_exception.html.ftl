<#include "base.ftl">

<#assign title>
<@_ "reproduction.order.exception" "Payment exception occured"/>
</#assign>

<#assign email>
  <@_ "iisg.email" ""/>
</#assign>

<@userbase title>
<section>
  <heading>
    <h1>${title}</h1>
  </heading>

  <section>
    <p>
      <@_html "reproduction.order.exception.message" "Unfortunately, your payment result is uncertain at the moment. Please contact the IISH to request information on your payment transaction:" />
      <a href="mailto:${email}">${email}</a>
    </p>
  </section>

  <p><@_ "reproduction.backToSearch" "Close this tab and return to Search for new requests" /></p>
</section>
</@userbase>
