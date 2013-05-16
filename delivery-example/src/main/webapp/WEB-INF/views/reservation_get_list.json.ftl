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

<#include "utils.ftl"/>
<#if callback??>
${callback}(
</#if>
[
<#list pageListHolder.pageList as reservation>
  {
    "visitorName" : "${reservation.visitorName?js_string}",
    "visitorEmail" : "${reservation.visitorEmail?js_string}",
    "status" : "${reservation.status?string}",
    "date" : "${reservation.date?string("yyyy-MM-dd")}",
    <#if reservation.returnDate??>"returnDate" : "${reservation.returnDate?string("yyyy-MM-dd")}",</#if>
    <#if reservation.queueNo??>"queueNo" : ${reservation.queueNo?c},</#if>
    "printed" : ${reservation.printed?string},
    "special" : ${reservation.special?string},
    <#if reservation.comment??>"comment" : "${reservation.comment?js_string}",</#if>
    "items" :
      <@generatePidToHoldingsJson reservation/>
  }
  <#if reservation_has_next>,</#if>
</#list>
]
<#if callback??>
);
</#if>
