<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml"
	version="1.0">
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
				<xsl:when test="@output='percent'">
					<xsl:value-of select="concat($percentage,'%')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(@value,'/',@max)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="${wc.dom.html5.element.progress}">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="isWrapper" select="1"/>
			</xsl:call-template>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:value-of select="@type"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="title"/>

			<xsl:attribute name="value">
				<xsl:value-of select="@value"/>
			</xsl:attribute>
			<xsl:attribute name="max">
				<xsl:value-of select="@max"/>
			</xsl:attribute>
			<xsl:element name="span">
				<xsl:attribute name="role">
					<xsl:text>progressbar</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="aria-valuemax">
					<xsl:value-of select="@max"/>
				</xsl:attribute>
				<xsl:attribute name="aria-valuenow">
					<xsl:value-of select="@value"/>
				</xsl:attribute>
				<xsl:attribute name="aria-valuetext">
					<xsl:value-of select="$barText"/>
				</xsl:attribute>
				<xsl:element name="span">
					<xsl:attribute name="role">
						<xsl:text>presentation</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="style">
						<xsl:value-of select="concat('width: ',$percentage,'%;')"/>
					</xsl:attribute>
					<xsl:text>&#160;</xsl:text>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="role">
						<xsl:text>presentation</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="$barText"/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
