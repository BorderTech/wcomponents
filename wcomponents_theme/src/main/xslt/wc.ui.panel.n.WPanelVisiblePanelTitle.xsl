<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
<!--
	Helper for determining the title element of WPanel Types CHROME and ACTION which
	output visible headings. We treat them separately because the status of each 
	type is in constant flux. This allows implementations to change an output element
	of one using an ANT property without changing the output element of the other or 
	having to over-ride this template.
	
	Called from matched template for ui:panel.
-->	
	<xsl:template name="WPanelVisiblePanelTitle">
		<xsl:param name="type"/>
		<xsl:if test="($type='chrome' or $type='action')">
			<xsl:element name="h1">
				<xsl:value-of select="@title"/>
				<xsl:if test="not(@title)">
					<!-- 
						H1 content must be palpable. It is strongly recommended
						that this fallback title be of a form which makes application
						designers, sponsors, owners etc really angry so they get
						their title specifications right! The default (in English)
						is "Untitled Panel".
					-->
					<xsl:value-of select="$$${wc.ui.panel.i18n.missingTitle}"/>
				</xsl:if>
			</xsl:element>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>