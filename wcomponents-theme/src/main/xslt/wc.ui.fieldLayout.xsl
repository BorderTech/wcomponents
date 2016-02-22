<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		WFieldLayout is intended for all layout of fields.

		Child elements
		* ui:field
	-->
	<xsl:template match="ui:fieldlayout">
		<xsl:variable name="element">
			<xsl:choose>
				<xsl:when test="@ordered">
					<xsl:text>ol</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>ul</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$element}">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="isWrapper" select="1"/>
			</xsl:call-template>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:value-of select="@layout"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:if test="@ordered and @ordered &gt; 1">
				<xsl:attribute name="start">
					<xsl:value-of select="@ordered"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@labelWidth">
				<xsl:attribute name="data-wc-labelwidth">
					<xsl:value-of select="@labelWidth"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="ui:margin"/>
			<xsl:apply-templates select="ui:field"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
