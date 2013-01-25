<#if callback??>
${callback}(
</#if>
  {
    "visitor_name": "${permission.name?js_string}",
    "visitor_email": "${permission.email?js_string}",
    "address": "${permission.address?js_string}",
    "status": "${permission.status?string}",
    "from_date": "${permission.dateFrom?string("yyyy-MM-dd")}",
    "to_date": "${permission.dateTo?string("yyyy-MM-dd")}",
    "research_organization": "${permission.researchOrganization?js_string}",
    "research_subject": "${permission.researchSubject?js_string}",
    "explanation": "${permission.explanation?js_string}",
    "items" :
      [
        <#list permission.recordPermissions as rp>
        ["${rp.record.pid}", ${rp.granted?string}<#if rp.motivation??>, ${rp.motivation?js_string}</#if>]<#if rp_has_next>,</#if>
        </#list>
      ]
  }

<#if callback??>
);
</#if>
