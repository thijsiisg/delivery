<#if callback??>
${callback}([
<#else>
[
</#if>
  <#list records as record>
  {
    "pid" : "${record.pid?js_string}",
    "title" : "${record.title?js_string}",
    <#if record.embargo??>
      "embargo" : "${record.embargo?string("yyyy-MM-dd")}",
    </#if>
    "restrictionType": "${record.realRestrictionType}",


    "holdings" : [
      <#list record.holdings as h>
      {
        <#if h.direction??>
        "direction" : "${h.direction?js_string}",
        </#if>
        <#if h.floor??>
        "floor" : ${h.floor?c},
        </#if>
        <#if h.cabinet??>
        "cabinet" : "${h.cabinet?js_string}",
        </#if>
        <#if h.shelf??>
        "shelf" : "${h.shelf?js_string}",
        </#if>
        "signature" : "${h.signature?js_string}",
        "status" : "${h.status}",
        "usageRestriction" : "${h.usageRestriction}"
      }
      <#if h_has_next>,</#if>
      </#list>
    ]

  <#if _sec.ifAllGranted("ROLE_RECORD_CONTACT_VIEW")>
    <#if record.realRestriction??>
    , "restriction" : "${record.realRestriction?js_string}"
    </#if>
    <#if record.realContact??>
    ,
    <#assign c = record.realContact>
    <#assign hasPrev = false>
    "contact" : {
      <#if  c.firstname??>
      <#assign hasPrev = true>
      "firstname" : "${c.firstname?js_string}"
      </#if>
      <#if  c.lastname??>
      <#if hasPrev>,</#if>
      <#assign hasPrev = true>
      "lastname" : "${c.lastname?js_string}"
      </#if>
      <#if  c.preposition??>
      <#if hasPrev>,</#if>
      <#assign hasPrev = true>
      "preposition" : "${c.preposition?js_string}"
      </#if>
      <#if  c.address??>
      <#if hasPrev>,</#if>
      <#assign hasPrev = true>
      "address" : "${c.address?js_string}"
      </#if>
      <#if  c.zipcode??>
      <#if hasPrev>,</#if>
      <#assign hasPrev = true>
      "zipcode" : "${c.zipcode?js_string}"
      </#if>
      <#if  c.location??>
      <#if hasPrev>,</#if>
      <#assign hasPrev = true>
      "location" : "${c.location?js_string}"
      </#if>
      <#if  c.country??>
      <#if hasPrev>,</#if>
      <#assign hasPrev = true>
      "country" : "${c.country?js_string}"
      </#if>
      <#if  c.email??>
      <#if hasPrev>,</#if>
      <#assign hasPrev = true>
      "email" : "${c.email?js_string}"
      </#if>
      <#if  c.phone??>
      <#if hasPrev>,</#if>
      <#assign hasPrev = true>
      "phone" : "${c.phone?js_string}"
      </#if>
      <#if  c.fax??>
      <#if hasPrev>,</#if>
      <#assign hasPrev = true>
      "fax" : "${c.fax?js_string}"
      </#if>
    }
    </#if>
  </#if>
  }<#if record_has_next>,</#if>
  </#list>
]
<#if callback??>
);
</#if>
