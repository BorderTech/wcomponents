<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.getHVGap.xsl"/>
<!--
		ui:flowlayout is one of the possible child elements of WPanel

		A flowLayout is used to place elements in a particular linear relationship to
		each other using the align property.

		The spacing between cells is determined by the properties HGAP and VGAP. HGAP
		and VGAP apply only between cells in the flow. They do not apply space between
		the FlowLayout and surrounding components.

		Child elements
		* ui:cell (minOccurs 0, maxOccurs unbounded). Each component placed into
		a flowLayout is output in a ui:cell. Empty cells are not ouput into the UI

		The actual flowLayout element is a placeholder/container so it is passed
		through and does not leave an UI artefact.

		The cells in a flow layout are again mere containers which are not individually
		addressable. For this reason we do not need to output any empty cells.
	-->
	<xsl:template match="ui:flowlayout">
		<div>
			<xsl:attribute name="class">
				<xsl:value-of select="concat('wc-flowlayout wc_fl_', @align)"/>
				<xsl:if test="@valign">
					<xsl:value-of select="concat('  wc_fl_', @valign)"/>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="@align='vertical'">
						<xsl:call-template name="getHVGapClass">
							<xsl:with-param name="isVGap" select="1"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="getHVGapClass"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:apply-templates select="ui:cell[node()]" mode="fl"/>
		</div>
	</xsl:template>
</xsl:stylesheet>
