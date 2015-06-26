<#include "base.ftl"/>
<#include "form.ftl" />

<#-- Build the title -->
<#assign title>
  <@_ "reproductionList.title" "Reproduction Overview"/>
</#assign>

<#-- Build the page -->
<@base "${title}">
<h1>${title}</h1>

<ul class="filter-buttons">
  <li>
    <a href="${rc.contextPath}/reproduction/?date=${today?string("yyyy-MM-dd")}">
      <@_ "reproductionList.filterToday" "Show today's reproductions"/>
    </a>
  </li>
  <li>
    <a href="${rc.contextPath}/reproduction/?status=waiting_for_order">
      <@_ "reproductionList.filterWaitForOrder" "Show reproductions waiting for order"/>
    </a>
  </li>
  <li>
    <a href="${rc.contextPath}/reproduction/?status=pending">
      <@_ "reproductionList.filterPending" "Show reproductions pending for repro"/>
    </a>
  </li>
  <li>
    <a href="${rc.contextPath}/reproduction/">
      <@_ "reproductionList.filterEverything" "Show All Reproductions"/>
    </a>
  </li>
</ul>

<fieldset class="actions">
  <legend>Filter:</legend>

  <form action="" method="GET">
    <#-- Generate hidden input for already existing GET vars -->
    <#list RequestParameters?keys as k>
      <#if k!="search" && k!="from_date" && k!="to_date" && k!="status" &&
      k!="page_len" && k!="date" && k!="page" && k!="printed">
        <input type="hidden" name="${k?html}" value="${RequestParameters[k]?html}"/>
      </#if>
    </#list>

    <ul>
      <li>
        <#assign search_value>
          <#if RequestParameters["search"]??>
            ${RequestParameters["search"]?html}
          </#if>
        </#assign>

        <label for="search_filter">
          <@_ "reproductionList.search" "Search"/>
        </label>

        <input type="text" id="search_filter" name="search" value="${search_value?trim}"/>
      </li>

      <li>
        <label for="status_filter"><@_ "reproduction.status" "Status"/></label>
        <select id="status_filter" name="status">
          <option value=""
                  <#if !RequestParameters["status"]?? || RequestParameters["status"] == "">selected="selected"</#if>>
            <@_ "reproductionList.allStatus" "Status N/A"/>
          </option>

          <#list status_types?keys as k>
            <option value="${k?lower_case}"
              <#if (RequestParameters["status"]?? && RequestParameters["status"]?upper_case == k)>
                    selected="selected"</#if>>
              <@_ "reproduction.statusType.${k}" "${k}"/>
            </option>
          </#list>
        </select>
      </li>

      <li>
        <label for="printed_filter"><@_ "reproduction.printed" "Printed"/></label>
        <select id="printed_filter" name="printed">
          <option value=""
            <#if !RequestParameters["printed"]?? ||
            RequestParameters["printed"] == "">
                  selected="selected"</#if>>
            <@_ "reproductionList.allPrinted" "Printed N/A"/>
          </option>

          <option value="true"
            <#if (RequestParameters["printed"]?? &&
            RequestParameters["printed"]?lower_case == "true")>
                  selected="selected"</#if>>
            <@_ "yes" "Yes" />
          </option>

          <option value="false"
            <#if (RequestParameters["printed"]?? &&
            RequestParameters["printed"]?lower_case == "false")>
                  selected="selected"</#if>>
            <@_ "no" "No" />
          </option>
        </select>
      </li>

      <li>
        <#assign from_date_value>
          <#-- The date field has priority over from_date -->
          <#if RequestParameters["date"]??>
            ${RequestParameters["date"]?html}
          <#elseif RequestParameters["from_date"]??>
            ${RequestParameters["from_date"]?html}
          </#if>
        </#assign>

        <label for="from_date_filter">
          <@_ "reproductionList.dateFrom" "From"/>
        </label>

        <input type="text" maxlength="10" id="from_date_filter" name="from_date" value="${from_date_value?trim}"
               class="filter_date"/>
      </li>
      <li>
        <#assign to_date_value>
         <#-- The date field has priority over to_date -->
          <#if RequestParameters["date"]??>
           ${RequestParameters["date"]?html}
          <#elseif RequestParameters["to_date"]??>
            ${RequestParameters["to_date"]?html}
          </#if>
        </#assign>

        <label for="to_date_filter">
          <@_ "reproductionList.dateUpTo" "Up To"/>
        </label>

        <input type="text" maxlength="10" id="to_date_filter" name="to_date" value="${to_date_value?trim}"
               class="filter_date"/>
      </li>

      <li>
        <label for="page_len_filter">
          <@_ "pageListHolder.nrResultsPerPage" "Amount of Results per Page"/>
        </label>

        <select id="page_len_filter" name="page_len">
          <#list 1..(prop_requestMaxPageLen?number/prop_requestPageStepSize?number)?floor as i>
            <#assign pageSize = (i*prop_requestPageStepSize?number)?floor/>
            <option value="${pageSize}"
              <#if (RequestParameters["page_len"]?? && RequestParameters["page_len"]?number == pageSize) || (!RequestParameters["page_len"]?? && prop_requestPageLen?number == pageSize)>
                    selected="selected"</#if>>${pageSize}</option>
          </#list>
        </select>
      </li>
    </ul>

    <#assign searchLabel>
      <@_ "reproductionList.search" "Search"/>
    </#assign>

    <input type="submit" value="${searchLabel}"/>
  </form>
</fieldset>

<#assign submitURL>
  <@paramUrl {} "/reproduction/batchprocess"/>
</#assign>

<#if pageListHolder.pageList?size == 0>
  <span class="bignote">
    <@_ "search.notfound" "No results..."/>
  </span>
<#else>
  <form action="${submitURL}" method="POST">
    <table class="overview">
      <thead>
      <tr>
        <th></th>
        <th></th>
        <th><@_ "holding.record" "Item"/></th>
        <th><@sortLink "signature"><@_ "holding.signature" "Call nr."/></@sortLink></th>
        <th><@sortLink "customerName"><@_ "reproduction.customerName" "Name"/></@sortLink></th>
        <th><@sortLink "date"><@_ "reproduction.date" "Date"/></@sortLink></th>
        <th><@sortLink "printed"><@_ "reproduction.printed" "Printed"/></@sortLink></th>
        <th><@sortLink "status"><@_ "reproduction.extended.status.status" "Reproduction status"/></@sortLink></th>
        <th><@sortLink "holdingStatus"><@_ "holding.extended.status" "Item status"/></@sortLink></th>
      </tr>
      </thead>
      <tbody>
        <#list pageListHolder.pageList as holdingReproduction>
          <#assign holding = holdingReproduction.holding>
          <#assign reproduction = holdingReproduction.reproduction>

            <tr>
              <td><input type="checkbox" name="checked" value="${reproduction.id?c}" class="checkItem"/></td>
              <td>
                <a href="${rc.contextPath}/reproduction/${reproduction.id?c}">
                  <@_ "reproductionList.show" "Show"/>
                </a>
                <#if  _sec.ifAllGranted("ROLE_REPRODUCTION_MODIFY")>
                  /
                  <a href="${rc.contextPath}/reproduction/edit/${reproduction.id?c}">
                    <@_ "reproductionList.edit" "Edit"/>
                  </a>
                </#if>
              </td>
              <td class="leftAligned">
              ${holding.record.title?html}
                <#if holdingReproduction.comment??> - ${holdingReproduction.comment}</#if>
              </td>
              <td>${holding.signature?html}</td>
              <td>${reproduction.customerName?html}</td>
              <td>${reproduction.creationDate?string(prop_dateFormat)}</td>

              <#assign yes>
                <@_ "yes" "Yes"/>
              </#assign>
              <#assign no>
                <@_ "no" "No"/>
              </#assign>

              <td>${reproduction.printed?string(yes, no)}</td>
              <td><@_ "reproduction.statusType.${reproduction.status?string}" "${reproduction.status?string}" /></td>
              <td><@_ "holding.statusType.${holding.status?string}" "${holding.status?string}" /></td>
            </tr>
        </#list>
      </tbody>
    </table>

    <div class="selectButtons">
      <#assign st><@_ "select_all" "Select All"/></#assign>
      <input type="button" value="${st}" class="selectAll"/>
      <#assign st><@_ "select_none" "Select None"/></#assign>
      <input type="button" value="${st}" class="selectNone"/>
    </div>

    <@pageLinks pageListHolder/>

    <#if _sec.ifAnyGranted("ROLE_REPRODUCTION_MODIFY,ROLE_REPRODUCTION_DELETE")>
      <fieldset class="actions">
        <legend><@_ "reproductionList.withSelected" "With Selected"/>:</legend>

        <#assign printLabel>
          <@_ "reproductionList.print" "Print"/>
        </#assign>
        <#assign printForceLabel>
          <@_ "reproductionList.printForce" "Print (Including already printed)"/>
        </#assign>
        <#assign deleteLabel>
          <@_ "reproductionList.delete" "Delete"/>
        </#assign>
        <#assign statusLabel>
          <@_ "reproductionList.toStatus" "Change Status"/>
        </#assign>
        <#assign deleteConfirm>
          <@_ "reproductionList.confirmDelete" "Deletion of reproductions is permanent. Are you sure you want to delete the selected reproductions?" />
        </#assign>
        <#assign printForceConfirm>
          <@_ "reproductionList.confirmPrintForce" "Are you sure you want to print already printed reproductions?"/>
        </#assign>

        <ul>
          <#if _sec.ifAllGranted("ROLE_REPRODUCTION_MODIFY")>
            <li>
              <input type="submit" name="print" value="${printLabel}"/>
              <input type="submit" name="printForce" value="${printForceLabel}"
                     onClick="return confirm('${printForceConfirm}');"/>
            </li>

            <li>
              <select name="newStatus">
                <#list status_types?keys as k>
                  <#if k != "PENDING"> <!-- TODO -->
                    <option value="${k}"
                      <#if RequestParameters["status"]?? && RequestParameters["status"]?upper_case == k>
                            selected="selected"</#if>>
                      <@_ "reproduction.statusType.${k}" "${k}"/>
                    </option>
                  </#if>
                </#list>
              </select>

              <input type="submit" name="changeStatus" value="${statusLabel}"/>

              <span class="note">
                <@_ "reproductionList.ignoreStatusBackwards" "Reproduction status can only be changed in forward order, i.e. Pending->Active->Completed, but not the other way around."/>
              </span>
            </li>
          </#if>
          <#if _sec.ifAllGranted("ROLE_REPRODUCTION_DELETE")>
            <li>
              <input type="submit" name="delete" value="${deleteLabel}" onClick="return confirm('${deleteConfirm}');"/>
            </li>
          </#if>
        </ul>
      </fieldset>
    </#if>
  </form>
</#if>

</@base>
