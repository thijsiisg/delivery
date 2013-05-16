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

<#include "base.ftl"/>
<#include "form.ftl" />

<#-- Build the title -->
<#assign title>
<@_ "userLogout.title" "You have been successfully logged out from Delivery."/>
</#assign>

<#-- Build the page -->
<@base "${title}">
<h1>${title}</h1>
<p><@_ "userLogout.casLogout" "You are still logged in at the Central Authentication Service (CAS). If you also want to logout from CAS, please use the link below."/></p>

<a href="${prop_casUrl}/logout"><@_ "userLogout.casLogoutLink" "Logout from CAS"/></a>
</@base>
