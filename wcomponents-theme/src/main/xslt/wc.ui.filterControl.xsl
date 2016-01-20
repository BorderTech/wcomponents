<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		TO BE DELETED PLEASE DO NOT USE:
		HERE FOR BACKWARDS COMPATIBILITY AND WILL BE REMOVED WITHOUT NOTICE!!!

		WFilter control undertakes client side table row filtering. When a filter is
		enabled only rows with a filter value containing (whole word) the filter
		controls filter will be shown. A more appropriate mechanism for table content
		filtering is to use ajax to filter on the server. WMenu or similar could then
		be used to filter by actual column content.
	-->
	<xsl:template match="ui:filterControl"/>
	<!-- 
	
		If your theme needs to retain support for WDataTable and WFilterControl you will need the following:
		
	<xsl:template match="ui:filterControl">
		<button id="{@id}" type="button" aria-control="{@for}" data-wc-filter="{@value}">
			<xsl:attribute name="class">
				<xsl:call-template name="commonClassHelper"/>
				<xsl:text> wc_btn_link</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="aria-pressed">
				<xsl:choose>
					<xsl:when test="@active">true</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:apply-templates select="ui:decoratedLabel"/>
		</button>
	</xsl:template>
	-->
</xsl:stylesheet>
