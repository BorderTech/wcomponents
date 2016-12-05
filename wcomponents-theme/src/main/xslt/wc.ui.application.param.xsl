<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	
	<!-- Application parameters, output as hidden input elements -->
	<xsl:template match="ui:application/ui:param">
		<xsl:element name="input">
			<xsl:attribute name="type">
				<xsl:text>hidden</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="@name"/>
			</xsl:attribute>
			<xsl:attribute name="value">
				<xsl:value-of select="@value"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>

	<!--
		Application parameters, output as get url name:value pairs.
		NOTE: we do not attempt URL encoding of these paramters in XSLT, it is up
		to the application to ensure that names and values are appropriately encoded.
		We used to do a lot of URL encoding but it is very inefficient in XSLT 1.
	-->
	<xsl:template match="ui:application/ui:param" mode="get">
		<xsl:value-of select="concat(@name,'=',@value)"/>
		<xsl:if test="position() ne last()">
			<xsl:text>&amp;</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
