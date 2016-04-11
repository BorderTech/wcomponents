<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.title.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	
	<!--
		Helper for common attributes for each common type of transform of 
		ui:label.
		
		param element: the XML element the ui:label is 'for' (not necessarily
		a labellable element and not necessarily set so must be tested).
	-->
	<xsl:template name="labelCommonAttributes">
		<xsl:param name="element"/>
		
		<xsl:attribute name="id">
			<xsl:value-of select="@id"/>
		</xsl:attribute>
		<xsl:call-template name="title"/>
		<xsl:if test="$element and $element/@hidden">
			<xsl:call-template name="hiddenElement"/>
		</xsl:if>
		<xsl:call-template name="ajaxTarget"/>
	</xsl:template>
</xsl:stylesheet>
