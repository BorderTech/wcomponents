<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.gapClass.xsl"/>
	<!--
		WRow transform.
	-->
	<xsl:template match="ui:row">
		<xsl:variable name="gap">
			<xsl:if test="@gap">
				<xsl:call-template name="gapClass">
					<xsl:with-param name="gap" select="@gap"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:variable>
		<div id="{@id}">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional" select="$gap"/>
			</xsl:call-template>
			<xsl:apply-templates select="ui:column"/>
		</div>
	</xsl:template>
</xsl:stylesheet>
