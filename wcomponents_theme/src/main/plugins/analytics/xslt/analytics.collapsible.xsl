<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Tracking of WCollapsible
	-->
	
	<xsl:include href="../../../xslt/wc.constants.xsl"/>
	<xsl:include href="analytics.tracking.n.helpers.xsl"/>
	
	
	<xsl:template match="ui:collapsible[@track]" mode="analytics">
		<xsl:if test="not(key('analytics_trackingKey',@id))">
			<!-- 'I am here' tracking - simple -->
			<xsl:call-template name="analytics_quickTrack">
				<xsl:with-param name="clickable" select="0"/>
			</xsl:call-template>
			<!-- click track on summary -->
			<xsl:call-template name="analytics_collapsibleClickHelper"/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="ui:collapsible" mode="analytics_tracking">
		<xsl:param name="name"/>
		<xsl:param name="cat"/>
		<xsl:param name="params"/>
		
		<xsl:call-template name="analytics_trackingHelper">
			<xsl:with-param name="for" select="@id"/>
			<xsl:with-param name="name" select="$name"/>
			<xsl:with-param name="cat" select="$cat"/>
			<xsl:with-param name="params" select="$params"/>
			<xsl:with-param name="isClickable" select="0"/>
		</xsl:call-template>
		<xsl:call-template name="analytics_collapsibleClickHelper">
			<xsl:with-param name="name" select="concat($name, ' - opener')"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="analytics_collapsibleClickHelper">
		<xsl:param name="name" select="concat(@id, ' - opener')"/>
		<xsl:call-template name="analytics_clickTrackingHelper">
			<xsl:with-param name="for" select="ui:decoratedLabel/@id"/>
			<xsl:with-param name="name" select="$name"/>
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>