<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!-- 
		Common helper template for all components which may be excluded from 
		POST data if they have no selection. The purpose of this field is to 
		report the existence of the checkable group if no options are selected.
		This is under review as it is one of the main weak points of WComponents.
		
		This template must never be
		excluded. Used by:
			wc.ui.checkableSelect.xsl
			wc.ui.checkableInput.xsl
			wc.ui.listbox.xsl
			wc.ui.menu.xsl
			wc.ui.multiSelectPair.xsl
			wc.ui.table.xsl
	-->
	<xsl:template name="hField">
		<xsl:param name="name" select="@id"/>
		<xsl:element name="input">
			<xsl:attribute name="type">
				<xsl:text>hidden</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="concat($name,'-h')"/>
			</xsl:attribute>
			<xsl:attribute name="value">
				<xsl:text>x</xsl:text>
			</xsl:attribute>
			<xsl:if test="@disabled='true'">
				<xsl:attribute name="disabled">
					<xsl:text>disabled</xsl:text>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
