<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.title.xsl"/>
	
	<!--
		Helper for common attributes for each common type of transform of 
		ui:label.
		
		param element: the XML element the ui:label is 'for' (not necessarily
		a labellable element and not necessarily set so must be tested).
		
		param style: passed in ultimately from the transform for ui:field. See
		wc.ui.field.xsl.
	-->
	<xsl:template name="labelCommonAttributes">
		<xsl:param name="element"/>
		<xsl:param name="style"/>
		
		<xsl:attribute name="id">
			<xsl:value-of select="@id"/>
		</xsl:attribute>
		
		<xsl:call-template name="title"/>
		
		<xsl:if test="$element">
			<xsl:if test="$element/@hidden">
				<xsl:call-template name="hiddenElement"/>
			</xsl:if>
		</xsl:if>
		
		<xsl:call-template name="ajaxTarget"/>
		
		<xsl:if test="$style != ''">
			<xsl:attribute name="style">
				<xsl:value-of select="$style"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
