<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml"
	version="1.0">
	
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	
	<!-- 
		Obsolete attributes on HTML elements. Here just for strict HTML5 compliance. Non-conforming elements are not
		transformed at all.
	-->
	<xsl:template match="html:img[@border='0']|html:a[@name]|html:input[@type='number' and @maxlength]">
		<xsl:element name="span">
			<xsl:call-template name="makeDebugAttrib-debug">
				<xsl:with-param name="name" select="'data-wc-debugerr'"/>
				<xsl:with-param name="text">
					<xsl:value-of select="local-name(.)"/>
					<xsl:text> element </xsl:text>
					<xsl:choose>
						<xsl:when test="self::html:img">border attribute is obsolete</xsl:when>
						<xsl:when test="self::html:a">name attribute is obsolete</xsl:when>
						<xsl:when test="self::html:input">type 'number' maxlength attribute is not supported</xsl:when>
					</xsl:choose>
					<xsl:text> and you should remove this attribute.</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:variable name="elName" select="local-name()"/>
			<xsl:element name="{$elName}">
				<xsl:apply-templates select="@*"/>
				<xsl:apply-templates select="node()"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>