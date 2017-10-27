<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:template name="icon">
		<xsl:param name="class" select="''"/>
		<xsl:variable name="normClass" select="normalize-space($class)"/>
		<i aria-hidden="true" class="fa {$normClass}"></i>
	</xsl:template>
</xsl:stylesheet>
