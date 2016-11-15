<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Common helper template for marking interactive controls as required if
		its required attribute is set 'true'. This template must never be
		excluded.

		param field:
			The element to test for required-ness. This is usually, though not
			always, the current node. Default . (current node)
		param useNative:
			Indicates whther to use the attribute "required" which is supported by
			form controls or set to 0 to use "aria-required. Default 1.
	-->
	<xsl:template name="requiredElement">
		<xsl:param name="field" select="."/>
		<xsl:param name="useNative" select="1"/>
		<xsl:if test="$field/@required">
			<xsl:choose>
				<xsl:when test="number($useNative) eq 1">
					<xsl:attribute name="required">
						<xsl:text>required</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="aria-required">
						<xsl:copy-of select="$t"/>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
