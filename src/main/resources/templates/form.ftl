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

<#macro form page bind action="">
<form action="${page}" method="POST">
  <@spring.bind bind/>

  <#if spring.status.errorMessages?size != 0>
  <ul class="errors">
    <li>
      <@spring.showErrors "</li><li>"/>
    </li>
  </ul>
  </#if>
  <#if action != "">
  <input type="hidden" name="action" value="${action?html}"/>
  </#if>
  <#nested>
</form>
</#macro>

<#macro form_plain page action="">
<form action="${page}" method="POST">
  <#if action != "">
  <input type="hidden" name="action" value="${action?html}"/>
  </#if>
  <#nested>
</form>
</#macro>


<#macro input path prefix required=true pathPrefix = "" class="">
  <@spring.bind pathPrefix + path/>
    <label for="${spring.status.expression}" class="field">
      <#assign msgName = prefix + path/>
      <@spring.messageText msgName spring.status.expression/>
        <font color="red">*</font>
    </label>

    <@spring.formInput pathPrefix + path "class='field ${class}'" />

    <#if spring.status.errorMessages?size != 0>
    <ul class="errors">
      <li>
        <@spring.showErrors "</li><li>"/>
      </li>
    </ul>
    </#if>
</#macro>

<#macro input_nolabel path class="" helptext="">
  <@spring.bind path/>
    <@spring.formInput path "class='${class}'"/>

    <#if helptext?length gt 0>
      <@spring.messageText helptext helptext/>
    </#if>

    <#if spring.status.errorMessages?size != 0>
    <ul class="errors">
      <li>
        <@spring.showErrors "</li><li>"/>
      </li>
    </ul>
    </#if>
</#macro>

<#macro date path prefix required=true class="">
  <@spring.bind path/>
    <label for="${spring.status.expression}" class="field">
      <#assign msgName = prefix + path/>
      <@spring.messageText msgName spring.status.expression/>
        <font color="red">*</font>
    </label>

    <@spring.formInput path "class='field date ${class}'" />

    <#if spring.status.errorMessages?size != 0>
    <ul class="errors">
      <li>
        <@spring.showErrors "</li><li>"/>
      </li>
    </ul>
    </#if>
</#macro>

<#macro textarea path prefix class="">
  <@spring.bind path/>
    <label for="${spring.status.expression}" class="field">
      <#assign msgName = prefix + path/>
      <@spring.messageText msgName spring.status.expression/>
    </label>

    <@spring.formTextarea path "class='${class} field'"/>

    <#if spring.status.errorMessages?size != 0>
    <ul class="errors">
      <li>
        <@spring.showErrors "</li><li>"/>
      </li>
    </ul>
    </#if>
</#macro>

<#macro textarea path prefix required=true class="">
  <@spring.bind path/>
<label for="${spring.status.expression}" class="field">
  <#assign msgName = prefix + path/>
      <@spring.messageText msgName spring.status.expression/>
    <font color="red">*</font>
</label>

  <@spring.formTextarea path "class='${class} field'"/>

  <#if spring.status.errorMessages?size != 0>
  <ul class="errors">
    <li>
      <@spring.showErrors "</li><li>"/>
    </li>
  </ul>
  </#if>
</#macro>

<#macro textarea_nolabel path class="">
  <@spring.bind path/>
  <@spring.formTextarea path "class='${class} field'"/>

  <#if spring.status.errorMessages?size != 0>
  <ul class="errors">
    <li>
      <@spring.showErrors "</li><li>"/>
    </li>
  </ul>
  </#if>
</#macro>

<#macro buttons>
  <ul class="buttons">
    <li><#nested></li>
  </ul>
</#macro>

<#macro submit prefix>
  <#assign msgName = prefix + ".submit"/>
  <input type="submit" name="${prefix}" value="<@spring.messageText msgName
  "Submit"/>"/>
</#macro>

<#macro button page action name onclick="">
    <form action="${page}" method="POST">
      <#if action??>
        <input type="hidden" name="action" value="${action}"/>
      </#if>
      <input type="submit" value="<@spring.messageText name "Submit"/>"  <#if onclick != "">onClick="${onclick}"</#if>/>
    </form>
</#macro>

<#macro radio path prefix options>
  <@spring.bind path/>
    <label for="${spring.status.expression}0" class="field">
      <#assign msgName = prefix + path/>
      <@spring.messageText msgName spring.status.expression/>
    </label>

    <ul class="options">
      <#list options?keys as value>
      <li>
        <#assign id="${spring.status.expression}${value_index}">
        <input type="radio" id="${id}" name="${spring.status.expression}" value="${value?html}"<#if spring.stringStatusValue == value> checked="checked" </#if>/>
        <#assign label = prefix + path + "." + options[value]?html />
        <label for="${id}"><@spring.messageText label options[value]?html/></label>
      </li>
      </#list>
    </ul>

    <#if spring.status.errorMessages?size != 0>
    <ul class="errors">
      <li>
        <@spring.showErrors "</li><li>"/>
      </li>
    </ul>
    </#if>
</#macro>

<#macro select path prefix options>
  <@spring.bind path/>
    <label for="${spring.status.expression}" class="field">
      <#assign msgName = prefix + path/>
      <@spring.messageText msgName spring.status.expression/>
    </label>

    <select id="${spring.status.expression}" name="${spring.status.expression}" class="field">
      <#list options?keys as value>
       <#assign label = prefix + path + "." + options[value]?html />
      <option <#if spring.stringStatusValue?? && spring.stringStatusValue == value>selected="selected"</#if> value="${value?html}"><@spring.messageText label options[value]?html/></option>
      </#list>
    </select>

    <#if spring.status.errorMessages?size != 0>
    <ul class="errors">
      <li>
        <@spring.showErrors "</li><li>"/>
      </li>
    </ul>
    </#if>
</#macro>

<#macro select_nolabel path prefix options>
  <@spring.bind path/>

    <select id="${spring.status.expression}" name="${spring.status.expression}" class="field">
      <#list options?keys as value>
       <#assign label = prefix  + "." + options[value]?html />
      <option <#if spring.stringStatusValue?? && spring.stringStatusValue == value>selected="selected"</#if> value="${value?html}"><@spring.messageText label options[value]?html/></option>
      </#list>
    </select>

    <#if spring.status.errorMessages?size != 0>
    <ul class="errors">
      <li>
        <@spring.showErrors "</li><li>"/>
      </li>
    </ul>
    </#if>
</#macro>
