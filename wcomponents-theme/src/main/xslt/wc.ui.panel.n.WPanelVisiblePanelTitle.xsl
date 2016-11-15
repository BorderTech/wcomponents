<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
<!--
	Helper for determining the title element of WPanel Types CHROME and ACTION which output visible headings. Called 
	from matched template for ui:panel.
-->	
	<xsl:template name="WPanelVisiblePanelTitle">
		<xsl:if test="(@type eq 'chrome' or @type eq 'action')">
			<h1>
				<xsl:variable name="title">
					<xsl:value-of select="normalize-space(@title)"/>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$title eq ''">
						<!-- 
						H1 content must be palpable. It is strongly recommended that this fallback title be of a form 
						which makes application designers, sponsors, owners etc really angry so they get their title 
						specifications right!
					-->
						<xsl:text>Accessibility Error</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$title"/>
					</xsl:otherwise>
				</xsl:choose>
			</h1>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
