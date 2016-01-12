<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Outputs an option emulator. This is a list item which, if it is a child
		of an optgroup element, is classed to be styled similar to a HTML option
		element nested in an optgroup element in a select element.
	-->
	<xsl:template match="ui:option" mode="readOnly">
		<xsl:param name="single" select="1"/>
		<xsl:param name="className"/>
		<xsl:choose>
			<xsl:when test="$single=1">
				<xsl:value-of select="."/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="class">
					<xsl:if test="parent::ui:optgroup">wc_inoptgroup</xsl:if>
					<xsl:if test="$className!=''">
						<xsl:value-of select="concat(' ',$className)"/>
					</xsl:if>
				</xsl:variable>
				<li>
					<xsl:if test="$class!=''">
						<xsl:attribute name="class">
							<xsl:value-of select="normalize-space($class)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="."/>
				</li>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
