<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.root.n.includeFavicon.xsl"/>
	<!--
		There may be occasions when you want to add very early meta elements to the head (such as adding an IE
		compatibility meta). This template is here for EXACTLY that purpose.
	-->
	<xsl:template name="addHeadMetaBeforeTitle">
		<!-- Works more reliably if it is first -->
		<xsl:call-template name="includeFavicon"/>
		<!--
		The format-detection is needed to work around issues in some very popular mobile browsers that will convert
		"numbers" into phone links (a elements) if they appear to be phone numbers, even if those numbers are the
		content of buttons or links. This breaks important stuff if you, for example, want to link or submit using
		a number identifier.

		If you want a phone number link in these (or any) browser use WPhoneNumberField set readOnly.
		-->
		<xsl:element name="meta">
			<xsl:attribute name="name">
				<xsl:text>format-detection</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="content">
				<xsl:text>telephone=no</xsl:text>
			</xsl:attribute>
		</xsl:element>
		<xsl:element name="meta">
			<xsl:attribute name="name"><xsl:text>viewport</xsl:text></xsl:attribute>
			<xsl:attribute name="content"><xsl:text>initial-scale=1</xsl:text></xsl:attribute>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
