<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.aria.live.xsl"/>
	<xsl:import href="wc.ui.panel.n.WPanelContainerElement.xsl"/>
	<xsl:import href="wc.ui.panel.n.WPanelAdditionalClass.xsl"/>
	<xsl:import href="wc.ui.panel.n.WPanelVisiblePanelTitle.xsl"/>
	<xsl:import href="wc.ui.panel.n.WPanelContentPrep.xsl"/>
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
<!--
		WPanel is the basic layout component in the framework. Genreally output as
		a "block" container (usually div).
	
		Child elements
		optional ui:margin and exactly one of:
			* ui:borderLayout
			* ui:columnLayout
			* ui:content
			* ui:flowLayout
			* ui:gridLayout
			* ui:listLayout
-->
	<xsl:template match="ui:panel">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="type" select="@type"/>
		<xsl:variable name="mode" select="@mode"/>
		
		<xsl:variable name="containerElement">
			<xsl:call-template name="WPanelContainerElement"/>
		</xsl:variable>
		<xsl:element name="{$containerElement}">
			<xsl:attribute name="id">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:if test="$isDebug=1">
				<xsl:call-template name="debugAttributes"/>
				<xsl:call-template name="thisIsNotAllowedHere-debug">
					<xsl:with-param name="testForPhraseOnly" select="1"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:attribute name="class">
				<xsl:text>panel</xsl:text>
				<xsl:if test="$type">
					<xsl:value-of select="concat(' ',$type)"/>
				</xsl:if>
				<xsl:if test="($mode='lazy' and @hidden=$t) or $mode='dynamic'">
					<xsl:text> wc_magic</xsl:text>
					<xsl:if test="$mode='dynamic'">
						<xsl:text> wc_dynamic</xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:call-template name="WPanelAdditionalClass"/>
			</xsl:attribute>
			<xsl:if test="@buttonId">
				<xsl:attribute name="${wc.common.attribute.button}">
					<xsl:value-of select="@buttonId"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$type ='header'">
				<xsl:attribute name="role">
					<xsl:text>banner</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$mode or key('targetKey',$id) or parent::ui:ajaxTarget[@action='replace']">
				<xsl:call-template name="setARIALive"/>
				<xsl:if test="$mode">
					<xsl:attribute name="data-wc-ajaxalias">
						<xsl:value-of select="$id"/>
					</xsl:attribute>
				</xsl:if>
			</xsl:if>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:apply-templates select="ui:margin"/>
			<xsl:if test="*[not(self::ui:margin)]/node() or not($mode='eager')">
				<!-- WPanelVisiblePanelTitle template outputs a visible title, not a title attribute -->
				<xsl:call-template name="WPanelVisiblePanelTitle">
					<xsl:with-param name="type" select="$type"/>
				</xsl:call-template>
				<!--
					We have split out preping the child elements into a helper template
					so that implementations can easily override the way templates are
					applied. Call this last.
				-->
				<xsl:call-template name="WPanelContentPrep"/>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>