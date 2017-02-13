<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.gapClass.xsl"/>
	<xsl:import href="wc.common.attributes.xsl"/>

	<!-- Transform for ui:listlayout which is one of the possible child elements of ui:panel. -->
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
			<xsl:if test="@gap">
				<xsl:call-template name="gapClass">
					<xsl:with-param name="gap" select="@gap"/>
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
			</xsl:if>
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

	<!--
		This template creates the HTML LI elements and applies the content. If there is no content the cell is omitted.
	-->
	<xsl:template match="ui:cell" mode="ll">
		<xsl:if test="node()">
			<li>
				<xsl:apply-templates />
			</li>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
