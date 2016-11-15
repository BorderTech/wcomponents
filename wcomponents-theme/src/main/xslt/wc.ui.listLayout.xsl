<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.getHVGap.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
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
		<xsl:variable name="additionalClasses">
			<xsl:if test="not(@align)">
				<xsl:text>wc-align-left</xsl:text>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="not(@separator) or @separator eq 'none'">
					<xsl:text> wc_list_nb</xsl:text>
				</xsl:when>
				<xsl:when test="not(@ordered)">
					<xsl:value-of select="concat(' wc-listlayout-separator-', @separator)"/>
				</xsl:when>
			</xsl:choose>
			<xsl:call-template name="getHVGapClass">
				<xsl:with-param name="isVGap">
					<xsl:choose>
						<xsl:when test="@type eq 'flat'">
							<xsl:number value="0"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:number value="1"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:element name="{$listElement}">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:value-of select="$additionalClasses"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates mode="ll"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
