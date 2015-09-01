<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		JSON object (comma separated list) for simple tracking of components with @track set.
	-->
	
	<xsl:import href="../../../xslt/wc.constants.xsl"/>
	<xsl:import href="analytics.tracking.n.helpers.xsl"/>
	
	<xsl:template match="*[@track]" mode="analytics">
		<xsl:if test="@id">
			<xsl:variable name="complexTrack" select="key('analytics_trackingKey',@id)"/>
			<xsl:if test="not($complexTrack)">
				<xsl:call-template name="analytics_quickTrack"/>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>