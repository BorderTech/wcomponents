<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<!--
	Helper to determine if a tab should be hidden.
	-->
	<xsl:template name="hideTab">
		<xsl:choose>
			<xsl:when test="parent::ui:ajaxtarget">
				<!-- if a tab is the child of a ui:ajaxtarget then it is being replaced. Do not allow a tab to be hidden if it is open -->
				<xsl:if test="not(@open)">
					<xsl:call-template name="hideElementIfHiddenSet"/>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="hideElementIfHiddenSet"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
