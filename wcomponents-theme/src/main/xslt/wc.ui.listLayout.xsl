<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.getHVGap.xsl"/>
	<!--
		Transform for ui:listlayout which is one of the possible child elements of ui:panel.

		A panel with a listLayout will transform to a HTML ul or ol element depending
		upon the ordered attribute of the listLayout.

		Empty cells are not output.

		When the ordered property is true the list is ordered. At the moment this
		creates some limitations in display of other properties:
			* When type is FLAT no separator will be shown
			* When type is STACKED or STRIPED and separator is DOT or BAR then the
		normal numeric marker is shown

		The parent element (ol or ul) is created in the transform of the parent
		WPanel and it holds the identification and type attributes. The
		listLayout then merely passes through to the cells after determining gaps.
	-->
	<xsl:template match="ui:listlayout">
		<xsl:variable name="listElement">
			<xsl:choose>
				<xsl:when test="@ordered">
					<xsl:text>ol</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>ul</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$listElement}">
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space(concat('wc-listlayout wc_ll_',@type,' ', @align))"/>
				<xsl:if test="not(@align)">
					<xsl:text> left</xsl:text>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="not(@separator) or @separator='none'">
						<xsl:text> wc_list_nb</xsl:text>
					</xsl:when>
					<xsl:when test="not(@ordered)">
						<xsl:value-of select="concat(' wc_ll_', @separator)"/>
					</xsl:when>
				</xsl:choose>
				<xsl:call-template name="getHVGapClass">
					<xsl:with-param name="isVGap">
						<xsl:choose>
							<xsl:when test="@type='flat'">0</xsl:when>
							<xsl:otherwise>1</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:apply-templates mode="ll"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
