<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<!--
		Creates the control which is used to collapse and expand rows. Called from
		the transform for ui:tr.
		param myTable: nearest ancestor table element
	-->
	<xsl:template name="tableCollapserElement">
		<xsl:param name="id"/>
		
		<!-- these attributes are applied to the td -->
		<xsl:attribute name="id">
			<xsl:value-of select="concat($id,'${wc.ui.table.rowExpansion.id.suffix}')"/>
		</xsl:attribute>
		
		<xsl:attribute name="role">button</xsl:attribute>

		<xsl:attribute name="aria-controls">
			<xsl:value-of select="$id"/>
		</xsl:attribute>
		
		<xsl:attribute name="aria-label">
			<xsl:value-of select="$$${wc.ui.table.rowExpansion.message.collapser}"/>
		</xsl:attribute>

		<xsl:attribute name="tabindex">0</xsl:attribute>
	</xsl:template>
</xsl:stylesheet>
