<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.toggleElement.xsl"/>

	<!-- Transform for WSelectToggle. -->
	<xsl:template match="ui:selecttoggle">
		<xsl:choose>
			<xsl:when test="@renderAs eq 'control'">
				<span>
					<xsl:call-template name="commonAttributes">
						<xsl:with-param name="class">
							<xsl:text>wc-input-wrapper</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="selectToggle">
						<xsl:with-param name="id" select="concat(@id, '_input')"/>
						<xsl:with-param name="for" select="@target"/>
						<xsl:with-param name="name" select="@id"/>
						<xsl:with-param name="selected" select="@selected"/>
						<xsl:with-param name="type">
							<xsl:text>control</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
				</span>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="selectToggle">
					<xsl:with-param name="for" select="@target"/>
					<xsl:with-param name="name" select="@id"/>
					<xsl:with-param name="selected" select="@selected"/>
					<xsl:with-param name="type">
						<xsl:text>text</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		Template match="ui:selecttoggle" mode="JS"
		
		This template creates JSON objects required to register named group 
		controllers.
	-->
	<xsl:template match="ui:selecttoggle" mode="JS">
		<xsl:text>{"identifier":"</xsl:text>
		<xsl:value-of select="@id"/>
		<xsl:text>","groupName":"</xsl:text>
		<xsl:value-of select="@target"/>
		<xsl:text>"}</xsl:text>
		<xsl:if test="position() ne last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
