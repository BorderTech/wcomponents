<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.popups.xsl"/>
	<!--
		Transform of WPopup. This component opens a new window on page load. 
		
		Popup windows are often considered harmful to accessibility due to the possibility
		of an unexpected change of context, and should be avoided. WLink accepts a
		windowAttributes child which will create a launch button for a pop up window.
		This launch button announces to compliant assistive technologies that it
		creates a pop up. This provides some mitigation to the pop up window accessibility
		problem, so if you must use a popup it is always better to do it with WLink than with WPopup.
	
		If WPopup must be used then any WButton which results in a screen with a
		WPopup must have its popup property set true.
		
		It has no explicit HTML artefact in the UI and therefore has a null template.
	-->
	<xsl:template match="ui:popup"/>
	
	<!--
		This template creates the JSON objects required to create the popup on page
		load and should not usualy need to be overridden.
	-->
	<xsl:template match="ui:popup" mode="JS">
		<xsl:text>["</xsl:text>
		<xsl:value-of select="@url"/>
		<xsl:text>","</xsl:text>
		<xsl:choose>
			<xsl:when test="@targetWindow">
				<xsl:value-of select="@targetWindow"/>
			</xsl:when>
			<xsl:otherwise>
				<!-- It is safe to keep this generated id, it is not used as an element id but as a stand in for window name-->
				<xsl:value-of select="generate-id(.)"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>","</xsl:text>
		<xsl:call-template name="getPopupSpecs"/>
		<xsl:text>"]</xsl:text>
		<xsl:if test="position()!=last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
