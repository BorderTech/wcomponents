
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!-- Transform for WHeading. -->
	<xsl:template match="ui:heading">
		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:apply-templates select="ui:margin" mode="asclass"/>
		</xsl:variable>
		<xsl:element name="{concat('h',@level)}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space(concat('wc-heading ', $additional))"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
