<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!-- This is a transform for the output of WDataListServlet. -->
	<xsl:template match="ui:datalist">
		<select>
			<xsl:apply-templates select="ui:option" mode="selectableList"/>
		</select>
	</xsl:template>
</xsl:stylesheet>
