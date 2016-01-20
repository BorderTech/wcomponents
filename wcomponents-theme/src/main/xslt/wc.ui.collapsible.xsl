<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.aria.live.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
<!--
	WCollapsible is a container with hideable content.

	Each WCollapsible transforms to a DETAILS element. The WDecoratedLabel of the WCollapsible transforms to the SUMMARY
	element as the first child of the DETAILS element. The content is transformed to a DIV elemeent. This is for
	efficiency and convenience in making the DETAILS element behaviour consistent across user agents, many of which do
	not currently support native bevaiour on the DETAILS element and none of which currently support all accessible
	interactions on these elements.
-->
	<xsl:template match="ui:collapsible">
		<xsl:variable name="collapsed">
			<xsl:if test="@collapsed">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		<xsl:element name="${wc.dom.html5.element.details}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:if test="$collapsed != 1">
				<xsl:attribute name="open">
					<xsl:text>open</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="setARIALive">
				<xsl:with-param name="live">
					<xsl:choose>
						<xsl:when test="@mode='dynamic'">
							<xsl:text>assertive</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>polite</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:apply-templates select="ui:margin"/>
			<xsl:element name="${wc.dom.html5.element.summary}">
				<xsl:attribute name="tabIndex">
					<xsl:text>0</xsl:text>
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="@level">
						<xsl:element name="h{@level}">
							<xsl:apply-templates select="ui:decoratedLabel"/>
						</xsl:element>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="ui:decoratedLabel"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
			<xsl:apply-templates select="ui:content" mode="collapsible"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
