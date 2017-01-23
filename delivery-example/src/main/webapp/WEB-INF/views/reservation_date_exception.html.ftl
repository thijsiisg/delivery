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
<#assign exceptionTitle>
    <@_ "reservationDateException.title" "New date exception"/>
</#assign>
<#assign overviewTitle>
    <@_ "reservationDateExceptionOverview.title" "Date exception overview"/>
</#assign>

<#macro border>
<table cellpadding=3><tr><td>
    <#nested>
</tr></td></table>
</#macro>

<#-- Build the page -->
<@preamble exceptionTitle>
<script type="text/javascript">
    $(document).ready(function(){
        $(".reservation_form .date").datepicker({
            "dateFormat": "yy-mm-dd",
            "minDate": "0",
            "beforeShowDay": $.datepicker.noWeekends
        });
    });
</script>
</@preamble>
<@heading/>
<@body>
    <h1>${exceptionTitle}</h1>
    <@form "" "reservationDateException">
        <fieldset class="reservation_form">
            <ul class="form">
                <li><@input "reservationDateException.description" ""/></li>
                <li><@date "reservationDateException.startDate" ""/></li>
                <li><@date "reservationDateException.endDate" ""/></li>
            </ul>
        </fieldset>
        <#if _sec.ifAnyGranted("ROLE_DATE_EXCEPTION_CREATE")>
        <@buttons>
            <@submit "reservationDateException" />
        </@buttons>
        </#if>
    </@form>
    <h1>${overviewTitle}</h1>
    <@form "" "reservationDateException" >
        <fieldset class="reservation_form">
            <#if reservationDateExceptions?has_content>
                <@border>
                    <thead>
                    <tr>
                        <th></th>
                        <th><@sortLink "startDate"><@_ "reservationDateException.startDate" ""/></@sortLink></th>
                        <th><@_ "reservationDateException.endDate" ""/></th>
                        <th><@_ "reservationDateException.description" ""/></th>
                    </tr>
                    </thead>
                    <#list reservationDateExceptions as exception>
                        <tr>
                            <td><input type="checkbox" name="checked" id=${exception.id?c} value=${exception.id?c}></td>
                            <td>${exception.startDate?string(prop_dateFormat)}</td>
                            <#if exception.endDate??>
                                <td>${exception.endDate?string(prop_dateFormat)}</td>
                            <#else><td>${"-"}</td>
                            </#if>
                            <td>${exception.getdescription()?html}</td>
                        </tr>
                    </#list>
                </@border>
            <#else><@_ "reservationDateExceptionOverview.empty" "No exception dates exist"/>
            </#if>
        </fieldset>
        <#if _sec.ifAnyGranted("ROLE_DATE_EXCEPTION_DELETE")>
            <#assign deleteLabel>
                <@_ "reservationDateExceptionOverview.submit" "Delete"/>
            </#assign>
            <#assign deleteConfirm>
                <@_ "reservationDateExceptionOverview.confirmDelete" "" />
            </#assign>
            <input type="submit" name="deleteDateException" value="${deleteLabel}" onClick="return confirm('${deleteConfirm}');"/>
        </#if>
    </@form>
</@body>
