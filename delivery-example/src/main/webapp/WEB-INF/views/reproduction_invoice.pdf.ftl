<#include "pdf.ftl">
<@pdf "reproductionMail.invoice", "Invoice">
<fo:block font-weight="bold">
  <fo:block margin-bottom="2mm">
      <@_ "reproductionMail.customerName" "Customer" />: ${reproduction.name?xml}
  </fo:block>

  <fo:block margin-bottom="2mm">
      <@_ "reproductionMail.reproductionId" "Reproduction number" />: ${reproduction.id?c}
  </fo:block>

  <#if reproduction.order??>
    <fo:block margin-bottom="2mm">
        <@_ "reproductionMail.orderId" "Order number" />: ${reproduction.orderId?c}
    </fo:block>
  </#if>
</fo:block>

<fo:block margin-top="1cm">
  <fo:table table-layout="fixed" width="100%" display-align="center">
    <fo:table-column/>
    <fo:table-column column-width="1.6cm"/>
    <fo:table-column column-width="1.6cm"/>
    <fo:table-column column-width="1.6cm"/>
    <fo:table-column column-width="2cm"/>
    <fo:table-column column-width="3.2cm"/>

    <fo:table-header font-weight="bold" border-bottom="0.5pt solid black">
      <fo:table-cell padding="1mm">
        <fo:block>
            <@_ "reproduction.confirm.item" "Item"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell padding="1mm">
        <fo:block>
            <@_ "reproduction.confirm.price" "Price"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell padding="1mm">
        <fo:block>
            <@_ "reproduction.confirm.discount" "Discount"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell padding="1mm">
        <fo:block>
            <@_ "reproduction.confirm.price.total" "Total price"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell padding="1mm">
        <fo:block>
            <@_ "reproduction.confirm.btw" "BTW included"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell padding="1mm">
        <fo:block>
            <@_ "reproduction.confirm.delivery.time" "Estimated delivery time"/>
        </fo:block>
      </fo:table-cell>
    </fo:table-header>

    <fo:table-footer font-weight="bold" border-top="0.5pt solid black">
        <#assign btwPrices = reproduction.totalBTW/>
        <#list btwPrices?keys as btwPercentage>
            <#if btwPercentage?is_first>
              <fo:table-row>
                <fo:table-cell padding="1mm" number-rows-spanned="${btwPrices?size}">
                  <fo:block text-align="right">
                      <@_ "total" "Total"/>:
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell padding="1mm" number-rows-spanned="${btwPrices?size}">
                  <fo:block>
                    &#8364; ${reproduction.totalPrice?string("0.00")}
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell padding="1mm" number-rows-spanned="${btwPrices?size}">
                  <fo:block>
                    &#8364; -${reproduction.totalDiscount?string("0.00")}
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell padding="1mm" number-rows-spanned="${btwPrices?size}">
                  <fo:block>
                    &#8364; ${reproduction.totalPriceWithDiscount?string("0.00")}
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell padding="1mm">
                  <fo:block>
                    &#8364; ${btwPrices[btwPercentage]?string("0.00")} (${btwPercentage}&#37;)
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell padding="1mm" number-rows-spanned="${btwPrices?size}">
                  <fo:block>
                  ${reproduction.getEstimatedDeliveryTime()?xml} <@_ "days" "days"/>
                  </fo:block>
                </fo:table-cell>
              </fo:table-row>
            <#else>
              <fo:table-row>
                <fo:table-cell padding="1mm">
                  <fo:block>
                    &#8364; ${btwPrices[btwPercentage]?string("0.00")} (${btwPercentage}&#37;)
                  </fo:block>
                </fo:table-cell>
              </fo:table-row>
            </#if>
        </#list>
    </fo:table-footer>

    <fo:table-body>
        <#list reproduction.holdingReproductions as hr>
            <#assign h = hr.holding/>

          <fo:table-row>
            <fo:table-cell padding="1mm">
              <fo:block>
              ${h.record.externalInfo.title?xml} - ${h.signature?xml}
              </fo:block>
            </fo:table-cell>
            <fo:table-cell padding="1mm">
              <fo:block>
                &#8364; ${hr.completePrice?string("0.00")}
              </fo:block>
            </fo:table-cell>
            <fo:table-cell padding="1mm">
              <fo:block>
                &#8364; -${hr.discount?string("0.00")}
              </fo:block>
            </fo:table-cell>
            <fo:table-cell padding="1mm">
              <fo:block>
                &#8364; ${hr.completePriceWithDiscount?string("0.00")}
              </fo:block>
            </fo:table-cell>
            <fo:table-cell padding="1mm">
              <fo:block>
                &#8364; ${hr.btwPrice?string("0.00")} (${hr.btwPercentage}&#37;)
              </fo:block>
            </fo:table-cell>
            <fo:table-cell padding="1mm">
              <fo:block>
              ${hr.deliveryTime?xml} <@_ "days" "days"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </#list>

        <#if reproduction.getAdminstrationCosts() gt 0>
          <fo:table-row font-style="italic">
            <fo:table-cell padding="1mm">
              <fo:block text-align="right">
                  <@_ "reproduction.adminstrationCosts" "Adminstration costs"/>:
              </fo:block>
            </fo:table-cell>
            <fo:table-cell padding="1mm">
              <fo:block>
                &#8364; ${reproduction.adminstrationCosts?string("0.00")}
              </fo:block>
            </fo:table-cell>
            <fo:table-cell padding="1mm">
              <fo:block>
                &#8364; -${reproduction.adminstrationCostsDiscount?string("0.00")}
              </fo:block>
            </fo:table-cell>
            <fo:table-cell padding="1mm">
              <fo:block>
                &#8364; ${reproduction.adminstrationCostsWithDiscount?string("0.00")}
              </fo:block>
            </fo:table-cell>
            <fo:table-cell padding="1mm">
              <fo:block>
                &#8364; ${reproduction.adminstrationCostsBtwPrice?string("0.00")} (${reproduction.adminstrationCostsBtwPercentage}&#37;)
              </fo:block>
            </fo:table-cell>
            <fo:table-cell padding="1mm">
              <fo:block/>
            </fo:table-cell>
          </fo:table-row>
        </#if>
    </fo:table-body>
  </fo:table>
</fo:block>
</@pdf>
