<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		The transform for each option in the multiSelectPair.
		
		param readOnly: the read only state of the parent multiSelectPair.
	-->
	<xsl:template match="ui:option" mode="multiselectPair">
		<xsl:param name="readOnly" select="0"/>
		<xsl:choose>
			<xsl:when test="number($readOnly) ne 1">
				<option value="{@value}">
					<xsl:value-of select="normalize-space(.)"/>
				</option>
			</xsl:when>
			<xsl:otherwise>
				<li>
					<xsl:if test="parent::ui:optgroup">
						<xsl:attribute name="class">
							<xsl:text>wc_inoptgroup</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="normalize-space(.)">
							<xsl:value-of select="normalize-space(.)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@value"/>
						</xsl:otherwise>
					</xsl:choose>
				</li>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
