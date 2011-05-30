<#if authenticationException??>
	<#import "cycle.lib.ftl" as cycleLib/>
	<@cycleLib.printAuthenticationException authenticationException/>
<#else>

<#escape x as jsonUtils.encodeJSONString(x)>
{
	"connectorId": "${connectorId}",
	"nodeId": "${nodeId}",
	"renderInfo": "${renderInfo}",
	"contentRepresentationId": "${contentRepresentationId}",
	"contentType": "${contentType}"
	
	<#if renderInfo == "IMAGE" >
		<#-- For images we don't need to send the content since it will be requested through a URL and the content.get webscript. -->
	<#elseif renderInfo == "HTML">
		<#-- For HTML we don't need to send the content since it will be requested through a URL and the content.get webscript. -->
	<#elseif renderInfo == "BINARY">
		<#-- For BINARY we don't need to send the content since we are currently not displaying anything. -->
	<#elseif renderInfo == "HTML_REFERENCE">
		,"url": "${content}"
	<#else>
		<#-- Content for "CODE" or "TEXT_PLAIN" needs to be HTML escaped. -->
		,"content": "${content?html}"
	</#if>
}
</#escape>

</#if>