<#include "base.ftl"/>
<#include "form.ftl"/>

<#assign title><@_ "scan.title" "Scan Items"/></#assign>

<@base title>
<h1>${title}</h1>
  <@form_plain "" "scan">
  <label for="scanid">
    <@_ "scan.id" "ID:"/>
  </label>
  <input type="text" id="scanid" name="id" value=""/>
    <@submit "scan"/>

    <#if error??>
      <ul class="errors">
        <li>
          <@_ "scan.error."+error error />.
        </li>
      </ul>
    <#elseif holding??>
      <ul class="messages">
        <li>
          &quot;${holding.record.title?html} - ${holding.signature?html}&quot;
          <@_ "scan.changedFrom" "has changed from status" />
          &quot;<@_ "holding.statusType.${oldStatus?string}" oldStatus?string/>&quot;
          <@_ "scan.changedTo" "to" />
          &quot;<@_ "holding.statusType.${holding.status?string}" holding.status?string/>&quot;.
        </li>

        <#if reservationOnHold??>
          <li>
            The holding was placed on hold for reservation ${reservationOnHold.id?c}.
            The holding may now return to ${reservationOnHold.getVisitorName()}.
          </li>
        </#if>

        <#if reproductionOnHold??>
          <li>
            The holding was placed on hold for reproduction ${reproductionOnHold.id?c}.
            The holding may now return to repro.
          </li>
        </#if>
      </ul>

      <#if reproduction??>
      <h3><@_ "scan.assocReproduction" "Reproduction details"/></h3>

      <ul class="reproductionDetails">
        <li><span><@_ "reproduction.customerName" "Name"/></span> ${reproduction.customerName?html}</li>
        <li><span><@_ "reproduction.customerEmail" "E-mail"/></span> ${reproduction.customerEmail?html}</li>
        <li><span><@_ "reproduction.creationDate" "Date"/></span> ${reproduction.creationDate?string(prop_dateFormat)}
        </li>
        <li>
          <span><@_ "reproduction.status" "Status"/></span> <@_ "reproduction.statusType.${reproduction.status?string}" reproduction.status?string/>
        </li>

        <#list reproduction.holdingReproductions as hr>
          <#assign h = hr.holding>
          <#assign info = h.record.externalInfo>

          <li class="spacing">
            <#if hr.standardOption??>
              <h3>${hr.standardOption.optionName?html} - Holding ${h.id}</h3>
            <#else>
              <h3><@_ "reproduction.customReproduction" "Custom reproduction"/> - Holding ${h.id}</h3>
            </#if>

            <ul class="holdingDetails">
              <li>
                <span><@_ "record.externalInfo.title" "Title"/></span>
              ${info.title?html}
              </li>

              <#if info.author??>
                <li>
                  <span><@_ "record.externalInfo.author" "Author"/></span>
                ${info.author?html}
                </li>
              </#if>

              <#if info.displayYear??>
                <li>
                  <span><@_ "record.externalInfo.displayYear" "Year"/></span>
                ${info.displayYear?html}
                </li>
              </#if>

              <li class="spacing">
                <span><@_ "record.externalInfo.materialType" "Material Type"/></span>
                <@_ "record.externalInfo.materialType.${info.materialType}" ""/>
              </li>

              <li>
                <span><@_ "holding.signature" "Signature"/></span>
              ${h.signature?html}
              </li>

              <li>
                <span>PID</span>
                <#if _sec.ifAllGranted("ROLE_RECORD_MODIFY")>
                  <a target="_blank"
                     href="${rc.contextPath}/record/editform/${h.record.pid?url}">${h.record.pid?html}</a>
                <#else>
                ${h.record.pid?html}
                </#if>
              </li>

              <li>
                <span><@_ "holding.status" "Status"/></span>
                <@_ "holding.statusType.${h.status?string}" h.status?string/>
              </li>

              <li class="spacing">
                <span><@_ "reproductionStandardOption.price" "Price"/></span>
                <#if hr.price??>
                  &euro; ${hr.price?string("0.00")}
                <#else>
                  <@_ "tbd" "To be determined"/>
                </#if>
              </li>

              <li>
                <span><@_ "reproductionStandardOption.deliveryTime" "Estimated delivery time"/></span>
                <#if hr.deliveryTime??>
                ${hr.deliveryTime?html} <@_ "days" "days"/>
                <#else>
                  <@_ "tbd" "To be determined"/>
                </#if>
              </li>

              <#if hr.comment??>
                <li>
                  <span><@_ "holdingReproductions.comment" "Comment" /> </span>
                ${hr.comment?html}
                </li>
              </#if>

              <#if !hr.standardOption??>
                <li class="spacing">
                  <span><@_ "reproduction.customReproductionCustomer.backend" "Customer's wish" /> </span>
                  <em>${hr.customReproductionCustomer?html}</em>
                </li>

                <#if hr.customReproductionReply??>
                  <li class="spacing">
                    <span><@_ "reproduction.customReproductionReply" "Reply on wish" /> </span>
                    <em>${hr.customReproductionReply?html}</em>
                  </li>
                </#if>
              </#if>
            </ul>
          </li>
        </#list>
      </ul>
      </#if>

      <#if reservation??>
      <h3><@_ "scan.assocReservation" "Reservation details"/></h3>

      <ul class="reservationDetails">
        <li><span><@_ "reservation.visitorName" "Name"/></span> ${reservation.visitorName?html}</li>
        <li><span><@_ "reservation.visitorEmail" "E-mail"/></span> ${reservation.visitorEmail?html}</li>
        <li><span><@_ "reservation.date" "Date"/></span> ${reservation.date?string(prop_dateFormat)}</li>
        <li>
          <span><@_ "reservation.status" "Status"/></span> <@_ "reservation.statusType.${reservation.status?string}" reservation.status?string/>
        </li>
        <#if reservation.queueNo??>
          <li><span><@_ "reservation.queueNo" "Queue Number"/></span> ${reservation.queueNo?c}</li>
        </#if>
      </ul>

      <table class="records">
        <caption><@_ "reservation.holdings" "Holdings"/></caption>
        <thead>
        <tr>
          <th>ID</th>
          <th><@_ "record.title" "Titel"/></th>
          <th><@_ "holding.status" "Status"/></th>
        </tr>
        </thead>
        <tbody>
          <#list reservation.holdingReservations as hr>
            <#assign h = hr.holding>
          <tr>
            <td>${h.id?c}</td>
            <td>${h.record.title?html} - ${h.signature?html}<#if hr.comment??> - ${hr.comment}</#if></td>
            <td><@_ "holding.statusType.${h.status?string}" h.status?string/></td>
          </tr>
          </#list>
        </tbody>
      </table>
      </#if>
    </#if>
  </@form_plain>
</@base>
