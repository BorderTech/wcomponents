<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		called from transform for ui:table
	-->
	<xsl:template name="commonClassHelper">
		<xsl:value-of select="local-name()"/>
		<xsl:if test="@class">
			<xsl:value-of select="concat(' ', @class)"/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="makeCommonClass">
		<xsl:attribute name="class">
			<xsl:call-template name="commonClassHelper"/>
		</xsl:attribute>
	</xsl:template>
</xsl:stylesheet>
