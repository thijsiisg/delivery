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
<@mail reservation.visitorName>
<#if reservation.date?date?string == .now?date?string>
  <@_ "reservationMail.confirmationMessage" "With this email we confirm your reservation. Requested documents will be in the reading room within 30 minutes." />
<#else>
  <@_ "reservationMail.confirmationFutureMessage" "With this email we confirm your reservation. Requested documents will be in the reading room on the day of your visit as of 9.30 am." />
</#if>

--- <@_ "reservation.date" "Date"/> ---
${reservation.date?string(delivery.dateFormat)}

--- <@_ "reservation.records" "Records"/> ---
<#list reservation.holdingReservations as hr>
* ${hr.toString()}
</#list>
</@mail>