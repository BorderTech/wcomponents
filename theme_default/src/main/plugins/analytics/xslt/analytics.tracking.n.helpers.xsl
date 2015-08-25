<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Complex tracking info component for any trackable WComponent.
		the ui:tracking element is used if element tracking is to track anything other than the componentId.
	-->
	
	<xsl:import href="../../../xslt/wc.constants.xsl"/>
	
	<xsl:template name="analytics_trackingHelper">
		<xsl:param name="for"/>
		<xsl:param name="name"/>
		<xsl:param name="cat"/>
		<xsl:param name="params"/>
		<xsl:param name="isClickable" select="0"/>
		
		<!-- element tracking -->
		<xsl:text>{"id":"</xsl:text>
		<xsl:value-of select="$for"/>
		<xsl:text>",
			"name":"</xsl:text>
		<xsl:value-of select="$name"/>
		<xsl:text>",
			"cat":"</xsl:text>
		<xsl:value-of select="@cat"/>
		<xsl:text>",
			"params":[</xsl:text>
		<xsl:choose>
			<xsl:when test="$params and $params != ''">
				<xsl:value-of select="$params"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="ui:param" mode="analytics"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>]},</xsl:text>
		
		<!-- click tracking -->
		<xsl:if test="$isClickable=1">
			<xsl:call-template name="analytics_clickTrackingHelper">
				<xsl:with-param name="for" select="$for"/>
				<xsl:with-param name="name" select="$name"/>
			</xsl:call-template>
		</xsl:if>
				
		
	</xsl:template>
	
	<xsl:template name="analytics_clickTrackingHelper">
		<xsl:param name="for"/>
		<xsl:param name="name"/>
		
		<xsl:text>{"id":"</xsl:text>
		<xsl:value-of select="$for"/>
		<xsl:text>",
			"name":"</xsl:text>
		<xsl:value-of select="$name"/>
		<xsl:text>",
			"type":"${analytics.core.track.type.event}"},</xsl:text>
	</xsl:template>
	
	
	
	<xsl:template name="analytics_quickTrack">
		<xsl:param name="id" select="@id"/>
		<xsl:param name="clickable"/>
		<xsl:param name="name"/>
		
		<xsl:variable name="isClickable">
			<xsl:choose>
				<xsl:when test="$clickable !=''">
					<xsl:value-of select="$clickable"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="analytics_isClickable"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:text>{"id":"</xsl:text>
		<xsl:value-of select="$id"/>
		<xsl:text>","name":"</xsl:text>
		<xsl:value-of select="concat($id,$name)"/>
		<xsl:if test="$isClickable=1">
			<xsl:text>","type":"${analytics.core.track.type.event}</xsl:text>
		</xsl:if>
		<xsl:text>"},</xsl:text>
	</xsl:template>
	
	<xsl:template name="analytics_isClickable">
		<xsl:param name="el" select="."/>
		<xsl:variable name="clickables">
			<xsl:text> button collapsibleToggle menuItem printButton selectToggle submenu tab </xsl:text>
		</xsl:variable>
		<xsl:if test="@submitOnChange or contains($clickables, local-name($el)) or (name($el)=ui:link and (@type='button' or ui:windowAttributes[count(@*) &gt; 1]))">
			<xsl:number value="1"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>