<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.aria.live.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.ui.section.n.WSectionClass.xsl"/>
	<!--
		Transform for WSection. It is simply a major content container with an exposed heading.
		
		Child elements
		* ui:margin (optional)
		* ui:decoratedlabel
		* ui:panel
	-->
	<xsl:template match="ui:section">
		<xsl:variable name="mode" select="@mode"/>
		<section id="{@id}">
			<xsl:attribute name="class">
				<xsl:call-template name="WSectionClass"/>
			</xsl:attribute>
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
