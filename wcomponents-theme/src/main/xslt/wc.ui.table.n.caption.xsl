<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Creates a caption element if required. Called from the transform for ui:table.

		Safe to override.
	-->
	<xsl:template name="caption">
		<xsl:if test="@caption and not(@caption='') or ui:tbody/ui:nodata">
			<caption>
				<xsl:choose>
					<xsl:when test="ui:tbody/ui:nodata">
						<xsl:apply-templates select="ui:tbody/ui:nodata"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@caption"/>	
					</xsl:otherwise>
				</xsl:choose>
			</caption>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
