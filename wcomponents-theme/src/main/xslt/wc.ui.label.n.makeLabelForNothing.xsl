<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.label.n.WLabelHint.xsl"/>
	<xsl:import href="wc.ui.label.n.labelClassHelper.xsl"/>
	<xsl:import href="wc.ui.label.n.labelCommonAttributes.xsl"/>
	
	<!--
		This helper will make a HTML artifact for a ui:label where that XML
		element:
			* does not have a for attribute OR
			  the for attribute is ''
			  AND
			  the ui:label does not have a single labellable element descendant
			
			OR
			
			* the for attribute is the id of a ui:application (since this 
			  indicates that the actual component with which the label is
			  associated is not in the render tree).
		In all of these cases the ui:label does not actually label anything.
		
		param style: passed in ultimately from the transform for ui:field. See
		wc.ui.field.xsl.
	-->
	<xsl:template name="makeLabelForNothing">
		<xsl:param name="style"/>
		<xsl:element name="span">
			<xsl:call-template name="labelCommonAttributes">
				<xsl:with-param name="element" select="false()"/>
				<xsl:with-param name="style" select="$style"/>
			</xsl:call-template>
			
			<xsl:call-template name="labelClassHelper"/>
			
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:apply-templates/>
			<xsl:call-template name="WLabelHint"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
