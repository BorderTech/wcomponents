<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<!--
		Make an abbr element. If the component does not have a description make 
		a span because an abbr without a title is worse than useless.
		
		If the ui:abbr has no content do not output anything.
	-->
	<xsl:template match="ui:abbr">
		<xsl:if test="text()">
			<xsl:choose>
				<xsl:when test="@description">
					<xsl:element name="abbr">
						<xsl:if test="@description">
							<xsl:attribute name="title">
								<xsl:value-of select="@description"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="$isDebug=1">
							<xsl:call-template name="nesting-debug">
								<xsl:with-param name="testNonPhrase" select="1"/>
							</xsl:call-template>
							<xsl:if test="@description=''">
								<xsl:call-template name="makeDebugAttrib-debug">
									<xsl:with-param name="name" select="'data-wc-debugwarn'"/>
									<xsl:with-param name="text" select="'WAbbreviatedText should have a description'"/>
								</xsl:call-template>
							</xsl:if>
						</xsl:if>
						<xsl:value-of select="."/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="$isDebug=1">
					<xsl:element name="span">
						<xsl:call-template name="nesting-debug">
							<xsl:with-param name="testNonPhrase" select="1"/>
						</xsl:call-template>
						<xsl:call-template name="makeDebugAttrib-debug">
							<xsl:with-param name="name" select="'data-wc-debugwarn'"/>
							<xsl:with-param name="text" select="'WAbbreviatedText should have a description'"/>
						</xsl:call-template>
						<xsl:value-of select="."/>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>