<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Complex tracking info component for any trackable WComponent.
		the ui:tracking element is used if element tracking is to track anything other than the componentId.
	-->
	
	<xsl:include href="../../../xslt/wc.constants.xsl"/><!-- for $t -->
	<xsl:include href="analytics.tracking.n.helpers.xsl"/>
	
	
	<xsl:template match="ui:tracking" mode="analytics">
		<xsl:variable name="for" select="@for"/>
		<xsl:variable name="forElement" select="//*[@id=$for]"/>
		
		<xsl:variable name="name">
			<xsl:choose>
				<xsl:when test="@name">
					<xsl:value-of select="@name"/>
				</xsl:when>
				<xsl:when test="$forElement/@url">
					<xsl:value-of select="$forElement/@url"/>
				</xsl:when>
				<xsl:when test="name($forElement)='ui:button' or name($forElement)='ui:link'">
					<xsl:value-of select="$forElement"/>
				</xsl:when>
				<xsl:when test="$forElement/ui:decoratedLabel">
					<xsl:value-of select="$forElement/ui:decoratedLabel"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$forElement/@id"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="forName" select="name($forElement)"/>
		
		<xsl:variable name="trackInfo">
			<xsl:apply-templates select="$forElement" mode="analytics_tracking">
				<xsl:with-param name="name" select="$name"/>
				<xsl:with-param name="cat"><xsl:value-of select="@cat"/></xsl:with-param>
				<xsl:with-param name="params"><xsl:apply-templates select="ui:param" mode="analytics"/></xsl:with-param>
			</xsl:apply-templates>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$trackInfo != ''">
				<xsl:value-of select="$trackInfo"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="isClickable">
					<xsl:call-template name="analytics_isClickable">
						<xsl:with-param name="el" select="$forElement"/>
					</xsl:call-template>
				</xsl:variable>
				
				<xsl:call-template name="analytics_trackingHelper">
					<xsl:with-param name="for" select="$for"/>
					<xsl:with-param name="name" select="$name"/>
					<xsl:with-param name="cat"><xsl:value-of select="@cat"/></xsl:with-param>
					<xsl:with-param name="forElement" select="$forElement"/>
					<xsl:with-param name="isClickable" select="$isClickable"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<!-- do not output anything inline -->
	<xsl:template match="ui:tracking"/>
	
	<xsl:template match="*" mode="analytics_tracking"/>
</xsl:stylesheet>