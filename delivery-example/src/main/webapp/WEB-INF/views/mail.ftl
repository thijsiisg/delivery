<#ftl strip_whitespace=true>
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

<#macro _ ident msg>${msgResolver.getMessage(ident, msg)}</#macro>

<#macro mail to>
<@_ "mail.dear" "Dear"/> ${to},

<#nested>

----
<@_ "mail.signature" "Kind regards"/>,

<@_ "mail.signedBy" "International Institute of Social History"/>

<@_ "mail.signedByDepartment" "Reading Room"/>


<@_ "mail.postalAddress" ""/>

<@_ "mail.visitingAddress" ""/>

<@_ "mail.email" ""/>: <@_ "iisg.email" ""/>
<@_ "mail.website" ""/>: <@_ "iisg.website" ""/>
</#macro>

<#macro mailRaedingRoom>
    <#nested>

----
    <@_ "mail.auto" "This is an email automatically sent by Delivery."/>
</#macro>