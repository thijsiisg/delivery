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
<#include "form.ftl"/>

<#-- Build the title -->
<#assign title>
    <@_ "reservationDateExceptionOverview.title" "Date exception overview"/>
</#assign>

<#macro border>
<table cellpadding=3><tr><td>
    <#nested>
</tr></td></table>
</#macro>

<#-- Build the page -->
<@preamble title>
</@preamble>
<@heading/>
<@body>
<h1>${title}</h1>
    <#--<form action="" method="get">-->
    <form action="/reservation/date_exception_overview" method="post">
    <fieldset class="reservation_form">
        <#if reservationDateExceptions?has_content>
            <@border>
                <thead>
                    <tr>
                        <th></th>
                        <th>
                            <@sortLink "description"><@_ "exception.description" "Description"/></@sortLink>
                        </th>
                        <th>
                            <@sortLink "start_date"><@_ "exception.startDate" "Start date"/></@sortLink>
                        </th>
                        <th>
                            <@sortLink "end_date"><@_ "exception.endDate" "End date"/></@sortLink>
                        </th>
                    </tr>
                </thead>
                <#list reservationDateExceptions as exception>
                    <tr>
                        <td><input type="checkbox" name="checked" value=${exception.id?c}></td>
                        <td>${exception.getdescription()?html}</td>
                        <td>${exception.startDate?string(prop_dateFormat)}</td>
                        <#if exception.endDate??>
                            <td>${exception.endDate?string(prop_dateFormat)}</td>
                        <#else><td>${"No end date given"}</td>
                        </#if>
                    </tr>
                </#list>
            </@border>
        <#else><@_ "reservationDateExceptionOverview.empty" "No exception dates exist"/>
        </#if>
    </fieldset>
    <#--</form>-->
    <#--<form action="/reservation/date_exception_overview" method="post">-->
        <#if _sec.ifAnyGranted("ROLE_RESERVATION_DELETE")>
            <#assign deleteLabel>
                <@_ "reservationDateExceptionOverview.submit" "Delete"/>
            </#assign>
            <#assign deleteConfirm>
                <@_ "reservationDateExceptionOverview.confirmDelete" "" />
            </#assign>
            <input type="submit" name="delete" value="${deleteLabel}" onClick="return confirm('${deleteConfirm}');"/>
        </#if>
    </form>
</@body>
