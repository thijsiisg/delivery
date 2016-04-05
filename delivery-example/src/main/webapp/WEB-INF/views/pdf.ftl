<#macro _ ident msg>${msgResolver.getMessage(ident, msg)}</#macro>

<#macro pdf title default>
<?xml version="1.0" encoding="UTF-8"?>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <fo:layout-master-set>
    <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21.0cm" margin="2cm">
      <fo:region-body margin-top="3cm"/>
      <fo:region-before region-name="header"/>
    </fo:simple-page-master>
  </fo:layout-master-set>

  <fo:page-sequence master-reference="A4" initial-page-number="1">
    <fo:title>
        <@_ title default/>
    </fo:title>

    <fo:static-content flow-name="header">
      <fo:table table-layout="fixed" width="100%" display-align="center">
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell>
              <fo:block>
                <fo:external-graphic src="url('iish_logo_en.jpg')" content-height="40px"/>
              </fo:block>
            </fo:table-cell>

            <fo:table-cell text-align="right" text-transform="uppercase" letter-spacing="3pt"
                           color="#5e92c2" font-weight="bold" font-size="1.2em" margin-right="1cm">
              <fo:block>
                  <@_ title default/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:static-content>

    <fo:flow flow-name="xsl-region-body" font-size="0.6em">
        <#nested>
    </fo:flow>
  </fo:page-sequence>
</fo:root>
</#macro>