<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
<!--
 The error output in the message box. This must include a link to the component 
 in an error state.
-->
	<xsl:template match="ui:error">
		<xsl:element name="li">
			<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('#',@for)"/>
				</xsl:attribute>
				<xsl:apply-templates/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<!-- The inline error output for each ui:error for a component in a error state. -->
	<xsl:template match="ui:error" mode="inline">
		<xsl:element name="li">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
