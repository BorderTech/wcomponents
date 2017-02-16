<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>

	<!-- Transform for WSection. -->
	<xsl:template match="ui:section">
		<xsl:variable name="mode" select="@mode"/>
		<section id="{@id}">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="@mode eq 'lazy' and @hidden">
						<xsl:text>wc_magic</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:if test="*[not(self::ui:margin)] or not($mode eq 'eager')">
				<xsl:apply-templates select="ui:decoratedlabel" mode="section"/>
				<xsl:apply-templates select="ui:panel">
					<xsl:with-param name="type" select="''"/>
				</xsl:apply-templates>
			</xsl:if>
		</section>
	</xsl:template>
</xsl:stylesheet>
