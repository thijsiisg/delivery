<#include "mail.ftl">
<@mailRaedingRoom>
<@_ "permissionMail.readingRoomMessage" "A visitor has sent a request for permission to access a restricted archive."/>

URL: ${prop_urlSelf}/permission/${permission.id?c}


--- <@_ "reservation.records" "Records"/> ---
<#list permission.recordPermissions as rp>
* ${rp.record.toString()}
</#list>
</@mailRaedingRoom>
