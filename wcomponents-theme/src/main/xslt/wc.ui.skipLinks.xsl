<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		"Skip links" are a set of links designed to improve aplication accessibilty by
		providing keyboard shortcuts to various parts of an application. The skipLinks 
		in WComponents are links to and WPanel with both an accessKey and title. The 
		accessKey sets the skipLink shortcut key and the title provides the content of 
		the actual link.
	-->
	<xsl:template match="ui:skipLinks">
		<xsl:variable name="containerList" select="ancestor::ui:application[1]//ui:panel[@accessKey and @title]"/>
		<xsl:if test="$containerList">
			<xsl:element name="${wc.dom.html5.element.nav}">
				<xsl:call-template name="makeCommonClass"/>
				<xsl:apply-templates select="$containerList" mode="skipLinks"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
