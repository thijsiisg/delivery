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
    <@_ "reservation.create" "Create Reservation"/>
</#assign>

<#-- Build the page -->
<@preamble title>
<script type="text/javascript">
$(document).ready(function(){
    $(".reservation_form .date").datepicker({
        "dateFormat": "yy-mm-dd",
        showOn: "both",
        buttonImageOnly: true,
        buttonImage: "${rc.contextPath}/css/images/calendar.png",
        buttonText: '',
        <#if permission??>
        "minDate": "${permission.dateFrom?string("yy-MM-dd")}",
        "maxDate": "${permission.dateTo?string("yy-MM-dd")}",
        <#else>
        "minDate": "0",
        "maxDate": "+${delivery.reservationMaxDaysInAdvance}",
        </#if>
        "beforeShowDay": $.datepicker.noWeekends
    });
});
</script>
</@preamble>
<@userHeading />
<@body>
<h1>${title}</h1>

<#if warning??>
    <p class="warning"><@_ "reservation.warning."+warning warning /></p>
</#if>

<section>
    <@form "" "reservation" "create">
        <heading>
            <hgroup>
                <fieldset>
                    <legend><@_ "reservation.records" ""/></legend>
                    <#assign idx = 0>
                    <#assign skip = []>
                    <#list reservation.holdingReservations as hr>
                        <#assign h = hr.holding>
                        <#assign r = h.record>
                        <#assign info = h.record.externalInfo>

                        <#if !skip?seq_contains(r.pid)>
                            <#if !r.parent??>
                                <input name="holdingReservations[${idx}].holding" type="hidden" value="${h.id?c}"/>
                                <#assign idx = idx + 1>
                            </#if>

                            <h3>${info.title?html}</h3>

                            <ul class="holdingDetails">
                                <li>
                                    <span><@_ "record.externalInfo.materialType" "Material Type"/></span>
                                    <@_ "record.externalInfo.materialType.${info.materialType}" ""/>
                                </li>
                                <#if r.parent??>
                                    <li>
                                        <span><@_ "record.items" "Items"/></span>

                                        <#assign items = []>
                                        <#list reservation.holdingReservations as childHr>
                                            <#assign childR = childHr.holding.record>
                                            <#if childR.parent?? && childR.parent.pid == r.parent.pid>
                                                <#assign skip = skip + [childR.pid]>

                                                <input name="holdingReservations[${idx}].holding"
                                                       type="hidden" value="${childHr.holding.id?c}"/>
                                                <#assign idx = idx + 1>
                                                <#assign items = items + [childHr.holding.signature?html]>
                                            </#if>
                                        </#list>

                                        ${items?join(', ')}
                                    </li>
                                <#else>
                                    <li>
                                        <span><@_ "holding.signature" "Signature"/></span> ${h.signature?html}
                                    </li>
                                </#if>
                                <#if info.author??>
                                    <li>
                                        <span><@_ "record.externalInfo.author" "Author"/></span> ${info.author?html}
                                    </li>
                                </#if>
                                <#if info.displayYear??>
                                    <li>
                                        <span><@_ "record.externalInfo.displayYear" "Year"/></span> ${info.displayYear?html}
                                    </li>
                                </#if>
                                <#if info.materialType == "SERIAL">
                                    <li>
                                        <span><@_ "holdingReservations.comment" "" /></span>
                                        <@input_nolabel "reservation.holdingReservations[${idx}].comment" />
                                        <#if h.externalInfo.serialNumbers??>
                                            ( <strong><@_ "reservationCreate.serialAvailable" ""/>:</strong>
                                        ${h.externalInfo.serialNumbers} )
                                        </#if>
                                    </li>
                                </#if>
                            </ul>
                        </#if>
                    </#list>
                </fieldset>
            </hgroup>
        </heading>

        <div class="reservation_form">
            <fieldset>
                <@input "reservation.visitorName" ""/>
                <@input "reservation.visitorEmail" ""/>
                <@date "reservation.date" ""/>

                <label for="captcha_response_field" class="field">
                    <@_ "captcha.explanation" "Type the following word to prevent spam" />
                </label>

                <div id="captcha_widget_div" class="field">
                    <input type="text" id="captcha_response_field" name="captcha_response_field"
                           value="" class="field" autocomplete="off"/>
                    <img src="<@spring.url relativeUrl="/captcha"/>" id="captcha_image"/>
                    <a href="#" class="refreshCaptcha">
                        <@_ "captcha.refresh" "Refresh captcha" />
                    </a>
                </div>

                <#if captchaError?? >
                    <ul class="errors">
                        <li>
                            <b>${captchaError?html}</b>
                        </li>
                    </ul>
                </#if>
            </fieldset>

            <@buttons>
                <@submit "reservation" />
            </@buttons>
        </div>
    </@form>
</section>
</@body>