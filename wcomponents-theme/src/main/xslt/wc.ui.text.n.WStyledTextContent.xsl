<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.text.n.WStyledTextGetElementFromType.xsl"/>
	
	<!-- Some combinations of space and type mean we end up with one or more inner
		elements brfore we get to the content. This template is called from
		a moded template for text nodes. The inner element is output bfore the
		text content if required.
	-->
	<xsl:template name="WStyledTextContent">
		<xsl:param name="type"/>
		<xsl:variable name="innerElem">
			<xsl:call-template name="WStyledTextGetElementFromType">
				<xsl:with-param name="type" select="$type"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$type='plain' or not($type)">
				<xsl:value-of select="."/>
			</xsl:when>
			<xsl:when test="$innerElem !=''">
				<xsl:element name="{$innerElem}">
					<xsl:attribute name="class">
						<xsl:value-of select="$type"/>
					</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
