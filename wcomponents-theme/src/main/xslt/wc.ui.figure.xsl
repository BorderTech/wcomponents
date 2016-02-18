<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.aria.live.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Transform for WFigure. Direct map to Figure element. THe WDecoratedLabel child maps to Figcaption element.
	-->
	<xsl:template match="ui:figure">
		<xsl:variable name="mode" select="@mode"/>
		<xsl:element name="${wc.dom.html5.element.figure}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:call-template name="commonClassHelper"/>
				<xsl:if test="$mode='lazy' and @hidden">
					<xsl:text> wc_magic</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test="ui:decoratedlabel">
				<xsl:attribute name="aria-labelledby">
					<xsl:value-of select="ui:decoratedlabel/@id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="ui:margin"/>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:if test="*[not(self::ui:margin)] or not($mode='eager')">
				<xsl:if test="ui:content">
					<div class="content">
						<xsl:apply-templates select="ui:content"/>
					</div>
				</xsl:if>
				<xsl:element name="${wc.dom.html5.element.figcaption}">
					<xsl:apply-templates select="ui:decoratedlabel"/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
