<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<!--
		WHorizontalRule is merely presentational with no semantic meaning.
		
		WSeparator (as a child of WMenu or WSubMenu) is also transformed to a hr
		element but it has a role of separator. This difference can be used to
		differentially style separators and horizontal rules.
		
		We include html:hr and non-namespace hr here as there is a bug in IE (up to and
		including IE9) which will cause the XSL processor to output \<hr/\>\</hr/\> and
		then recurse over the hr's parent's siblings if it is passed through with a
		copy. Simply transforming a hr to a hr overcomes this issue.
	-->
	<xsl:template match="ui:hr|html:hr|hr">
		<xsl:element name="hr" >
			<xsl:if test="$isDebug=1">
				<xsl:call-template name="thisIsNotAllowedHere-debug">
					<xsl:with-param name="testForPhraseOnly" select="1"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>