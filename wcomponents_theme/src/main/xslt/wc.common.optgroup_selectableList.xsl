<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Tranforms the optgroups of a list into HTML optgroup elements.
	-->
	<xsl:template match="ui:optgroup" mode="selectableList">
		<xsl:element name="optgroup">
			<xsl:attribute name="label">
				<xsl:value-of select="@label"/>
			</xsl:attribute>
			<xsl:apply-templates mode="selectableList"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
