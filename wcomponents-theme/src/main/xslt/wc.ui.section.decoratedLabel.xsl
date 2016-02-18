<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		WDecoratedLabel allows a labelling element to contain up to three independently
		stylable areas. The output element of the label and its children is dependent
		upon the content model of the containing element and defaults to span.
	
		Child elements
		* ui:labelhead (0..1)
		* ui:labelbody (required, exactly 1)
		* ui:labeltail (0..1)

		Output the WDecoratedLabel
		param output: A HTML element name. Default 'span'
	-->
	<xsl:template match="ui:decoratedlabel" mode="section">
		<xsl:element name="header">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:call-template name="ajaxTarget">
				<xsl:with-param name="live" select="'off'"/>
			</xsl:call-template>
			
			<xsl:apply-templates select="ui:labelhead">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="ui:labelbody">
				<xsl:with-param name="output" select="'h1'"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="ui:labeltail">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
