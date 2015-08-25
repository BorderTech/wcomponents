<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<!--
		Outputs each shuffle control as a HTML BUTTON element.
		
		param id: The id of the SELECT being controlled. This is not necessarily the id
		of the component which owns the sort controls.
		param value: The value of the button "top", "bottom", "up", "down"
		param toolTip: The text used to populate the button title.
	-->
	<xsl:template name="listSortControl">
		<xsl:param name="id"/>
		<xsl:param name="value"/>
		<xsl:param name="toolTip"/>
		<xsl:element name="button">
			<xsl:attribute name="class">
				<xsl:text>wc_sorter wc_ibtn</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="type">
				<xsl:text>button</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="value">
				<xsl:value-of select="$value"/>
			</xsl:attribute>
			<xsl:attribute name="aria-controls">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:value-of select="$toolTip"/>
			</xsl:attribute>
			<xsl:call-template name="disabledElement">
				<xsl:with-param name="isControl" select="1"/>
			</xsl:call-template>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
