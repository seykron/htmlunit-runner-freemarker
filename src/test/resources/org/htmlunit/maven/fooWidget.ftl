<#-- Widget to display a list of names. -->
<#macro dummy names>
  <ul>
    <#list names as name>
      <li>${name}</li>
    </#list>
  </ul>
</#macro>
