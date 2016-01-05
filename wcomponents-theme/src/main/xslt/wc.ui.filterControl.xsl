<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<!--
		TO BE DELETED PLEASE DO NOT USE:
		HERE FOR BACKWARDS COMPATIBILITY AND WILL BE REMOVED WITHOUT NOTICE!!!

		WFilter control undertakes client side table row filtering. When a filter is
		enabled only rows with a filter value containing (whole word) the filter
		controls filter will be shown. A more appropriate mechanism for table content
		filtering is to use ajax to filter on the server. WMenu or similar could then
		be used to filter by actual column content.
	-->
	<xsl:template match="ui:filterControl">
		<xsl:element name="button">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>filterControl wc_btn_link</xsl:text>
				<xsl:if test="@class">
					<xsl:value-of select="concat(' ', @class)"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:attribute name="type">
				<xsl:text>button</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="aria-controls">
				<xsl:value-of select="@for"/>
			</xsl:attribute>
			<xsl:attribute name="${wc.ui.filterControl.attribute.filter}">
				<xsl:value-of select="@value"/>
			</xsl:attribute>
			<xsl:attribute name="aria-pressed">
				<xsl:choose>
					<xsl:when test="@active">true</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:apply-templates select="ui:decoratedLabel"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
