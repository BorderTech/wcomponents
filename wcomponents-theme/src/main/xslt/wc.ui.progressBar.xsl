<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml"
	version="2.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<!--
		Transform for WProgressBar. 
		This component generates a graphical indicator of static progess, not a timer.
	-->
	<xsl:template match="ui:progressbar">
		<xsl:variable name="percentage" select="round(100 * (@value div @max))"/>
		<xsl:variable name="barText">
			<xsl:choose>
				<xsl:when test="@output eq 'percent'">
					<xsl:value-of select="concat($percentage,'%')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(@value,'/',@max)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<progress>
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="isWrapper" select="1"/>
			</xsl:call-template>
			<xsl:call-template name="title"/>
			<xsl:attribute name="value">
				<xsl:value-of select="@value"/>
			</xsl:attribute>
			<xsl:attribute name="max">
				<xsl:value-of select="@max"/>
			</xsl:attribute>
			<span role="progressbar" aria-valuemax="{@max}" aria-valuenow="{@value}" aria-valuetext="{$barText}">
				<span role="presentation" style="{concat('width: ',$percentage,'%;')}">
					<xsl:text>&#160;</xsl:text>
				</span>
				<span role="presentation">
					<xsl:value-of select="$barText"/>
				</span>
			</span>
		</progress>
	</xsl:template>
</xsl:stylesheet>
