<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Tracking of WCollapsibleToggle
	-->
	
	<xsl:include href="../../../xslt/wc.constants.xsl"/>
	<xsl:include href="analytics.tracking.n.helpers.xsl"/>
	
	
	<xsl:template match="ui:collapsible[@track]" mode="analytics">
		<xsl:if test="not(key('analytics_trackingKey',@id))">
			<!-- click track on buttons -->
			<xsl:call-template name="analytics_collapsibleToggleClickHelper"/>
			<xsl:call-template name="analytics_collapsibleToggleClickHelper">
				<xsl:with-param name="expand" select="1"/>
			</xsl:call-template>
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
		<xsl:call-template name="analytics_collapsibleToggleClickHelper">
			<xsl:with-param name="name" select="$name"/>
		</xsl:call-template>
		<xsl:call-template name="analytics_collapsibleToggleClickHelper">
			<xsl:with-param name="name" select="$name"/>
			<xsl:with-param name="expand" select="1"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="analytics_collapsibleToggleClickHelper">
		<xsl:param name="expand" select="0"/>
		<xsl:param name="name"/>
		
		<xsl:variable name="localName">
			<xsl:choose>
				<xsl:when test="$name != @id">
					<xsl:value-of select="$name"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@id"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text> - </xsl:text>
		</xsl:variable>
		<xsl:call-template name="analytics_clickTrackingHelper">
			<xsl:with-param name="for">
				<xsl:value-of select="@id"/>
				<xsl:choose>
					<xsl:when test="$expand=1">
						<xsl:text>${wc.common.toggles.id.expand}</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>${wc.common.toggles.id.collapse}</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="name">
				<xsl:value-of select="$localName"/>
				<xsl:choose>
					<xsl:when test="$expand=1">
						<xsl:value-of select="$$${wc.common.i18n.expandAll}"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$$${wc.common.toggles.i18n.collapseAll}"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>