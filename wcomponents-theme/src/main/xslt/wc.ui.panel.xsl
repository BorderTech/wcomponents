<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.aria.live.xsl"/>
	<xsl:import href="wc.ui.panel.n.WPanelContainerElement.xsl"/>
	<xsl:import href="wc.ui.panel.n.WPanelClass.xsl"/>
	<xsl:import href="wc.ui.panel.n.WPanelVisiblePanelTitle.xsl"/>
	<xsl:import href="wc.ui.panel.n.WPanelContentPrep.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<!--
		WPanel is the basic layout component in the framework. Genreally output as
		a "block" container (usually div).

		Child elements
		optional ui:margin and exactly one of:
			* ui:borderlayout
			* ui:columnlayout
			* ui:content
			* ui:flowlayout
			* ui:gridlayout
			* ui:listlayout
	-->
	<xsl:template match="ui:panel">
		<xsl:param name="type" select="@type"/>
		<xsl:variable name="id" select="@id"/>

		<xsl:variable name="containerElement">
			<xsl:call-template name="WPanelContainerElement"/>
		</xsl:variable>
		<xsl:element name="{$containerElement}">
			<xsl:attribute name="id">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:call-template name="WPanelClass">
					<xsl:with-param name="type" select="$type"/>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:if test="@buttonId">
				<xsl:attribute name="data-wc-submit">
					<xsl:value-of select="@buttonId"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$type eq 'header'">
				<xsl:attribute name="role">
					<xsl:text>banner</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@mode or key('targetKey',$id) or parent::ui:ajaxtarget[@action eq 'replace']">
				<xsl:call-template name="setARIALive"/>
			</xsl:if>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:if test="*[not(self::ui:margin)]/node() or not(@mode eq 'eager')">
				<!-- WPanelVisiblePanelTitle template outputs a visible title, not a title attribute -->
				<xsl:call-template name="WPanelVisiblePanelTitle"/>
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
