<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		It is required that the labelBody will hold the core information in the label. 
		Therefore the content of the labelBody is describedBy the labelHead and 
		labelTail; and the content of the labelHead and labelTail is labelledBy the 
		labelBody. The label head and label tail are just wrappers for other content.
		 
		param output: an HTML element of this name is created and the content placed inside.
	-->
	<xsl:template match="ui:labelBody|ui:labelHead|ui:labelTail">
		<xsl:param name="output" select="'span'"/>
		<xsl:element name="{$output}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>wc_dlbl_seg </xsl:text>
				<xsl:value-of select="local-name(.)"/>
			</xsl:attribute>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
