<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Helper template to create each cell in a gridLayout.
		
		param width: The width of the cells in percent or '' if cols &lt;= 12.
	-->
	<xsl:template name="gridCell">
		<xsl:param name="width" />
		<div class="wc-cell wc-column">
			<xsl:if test="$width!=''">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('width:',$width,';')"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
</xsl:stylesheet>
