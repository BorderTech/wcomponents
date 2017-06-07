<?xml version='1.0'?>
<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
	<xsl:param name="foo" select="'crocodile'"/>
	<xsl:template match='/ | @* | node()'><xsl:copy><xsl:apply-templates select='@* | node()'/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="htmlInput">
		<xsl:element name="input">
			<xsl:attribute name="name">
				<xsl:value-of select="$foo"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>