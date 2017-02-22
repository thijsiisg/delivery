<#--

    Copyright (C) 2013 International Institute of Social History

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->

<#include "mail.ftl">
<@mail to="${permission.name}">
<@_ "permissionMail.confirmationMessage" "With this mail we confirm your permission request has been successfully received. We will notify you when your request has either been approved or refused."/>


--- <@_ "reservation.records" "Records"/> ---
<#list permission.recordPermissions as rp>
* ${rp.record.toString()}
</#list>
</@mail>
