<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		in WDefinition List there are a series of terms which contain their data. The 
		DT elements are determined by an attribute and their data are child elements
		
		<<NOTE:>> This is a major departure in the structure of a HTML definition list
		but makes sense since it enforces the obvious lack of parent:child relationship
		in the HTML spec.
	-->
	<xsl:template match="ui:term">
		<dt>
			<xsl:value-of select="@text"/>
		</dt>
		<xsl:apply-templates select="ui:data"/>
	</xsl:template>
</xsl:stylesheet>
