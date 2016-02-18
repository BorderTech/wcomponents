<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!-- 
		Called from template match="ui:ajaxtrigger" mode="JS" in wc.ui.ajaxTrigger.xsl.
		This template may be replaced and the targets determined directly from 
		the DOM using the trigger's aria-controls attribute as populated in the 
		template match="ui:ajaxtargetid" mode="controlled".
		
	-->
	<xsl:template match="ui:ajaxtargetid">
		<xsl:text>"</xsl:text>
		<xsl:value-of select="@targetId"/>
		<xsl:text>"</xsl:text>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
	
	<!-- 
		This template outputs a space separated list of targetIds.
		NOTE: we add the space to every id and then normalize later; this is to cope with the situation where there are 
		multiple ui:ajaxtrigger elements with the same !triggerId.
	-->
	<xsl:template match="ui:ajaxtargetid" mode="controlled">
		<xsl:value-of select="@targetId"/>
		<xsl:value-of select="' '"/>
	</xsl:template>
</xsl:stylesheet>
