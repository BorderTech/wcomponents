<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Make the skipLink links to panels which have accessKey and title attributes set.
	-->
	<xsl:template match="ui:panel" mode="skiplinks">
		<a href="#{@id}">
			<xsl:value-of select="@title"/>
		</a>
	</xsl:template>
</xsl:stylesheet>
