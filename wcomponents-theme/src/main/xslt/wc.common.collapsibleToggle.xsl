<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.toggleElement.xsl"/>

	<!-- Output expand all and collapse all buttons. -->
	<xsl:template name="collapsibleToggle">
		<xsl:param name="id" select="@id"/>
		<xsl:param name="for" select="@groupName"/>
		<xsl:variable name="mode">
			<xsl:choose>
				<xsl:when test="@mode and (@mode eq 'dynamic' or @mode eq 'lazy')">
					<xsl:value-of select="@mode"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>client</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!--
			WCollapsibleToggle has a mandatory groupName attribute.

			This may not point to a CollapsibleGroup (which is odd and should change) and in this case the WCollapsibleToggle should toggle every
			WCollapsible on the page.
		-->
		<xsl:variable name="toggleClass">
			<xsl:value-of select="concat('wc-icon wc_', local-name(.))"/>
		</xsl:variable>
		<ul id="{$id}" role="radiogroup">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:text>wc_coltog</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:attribute name="data-wc-group">
				<xsl:value-of select="$for"/>
			</xsl:attribute>
			<li>
				<xsl:call-template name="toggleElement">
					<xsl:with-param name="mode" select="$mode"/>
					<xsl:with-param name="name" select="$id"/>
					<xsl:with-param name="value" select="'expand'"/>
					<xsl:with-param name="text"><xsl:text>{{t 'expandall'}}</xsl:text></xsl:with-param>
					<xsl:with-param name="class" select="$toggleClass"/>
				</xsl:call-template>
			</li>
			<li>
				<xsl:call-template name="toggleElement">
					<xsl:with-param name="mode" select="'client'"/><!-- collapse all is never ajaxy -->
					<xsl:with-param name="name" select="$id"/>
					<xsl:with-param name="value" select="'collapse'"/>
					<xsl:with-param name="text"><xsl:text>{{t 'collapseall'}}</xsl:text></xsl:with-param>
					<xsl:with-param name="class" select="$toggleClass"/>
				</xsl:call-template>
			</li>
		</ul>
	</xsl:template>
</xsl:stylesheet>
