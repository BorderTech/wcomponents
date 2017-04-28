<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:output encoding="UTF-8" indent="no" method="html" omit-xml-declaration="yes"/>

	<xsl:template match="kung">
		<xsl:element name="omg">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="fu">
		<xsl:element name="wtf">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>