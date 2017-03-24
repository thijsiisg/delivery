<#include "mail.ftl">
<@mailRaedingRoom>
<@_ "permissionMail.readingRoomMessage" "A visitor has sent a request for permission to access a restricted archive."/>

URL: ${prop_urlSelf}/permission/${permission.id?c}

* <@_ "permission.name" "Name" />: ${permission.name}
* <@_ "permission.email" "E-mail" />: ${permission.email}
* <@_ "permission.address" "Address" />:
${permission.address}
* <@_ "permission.researchOrganization" "for" />: ${permission.researchOrganization}
* <@_ "permission.researchSubject" "Subject" />:
${permission.researchSubject}
* <@_ "permission.explanation" "Explanation" />:
${permission.explanation}

--- <@_ "reservation.records" "Records"/> ---
<#list permission.recordPermissions as rp>
* ${rp.record.toString()}
</#list>
</@mailRaedingRoom>
