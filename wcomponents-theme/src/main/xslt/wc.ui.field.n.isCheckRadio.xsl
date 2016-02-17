<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
 		If the child of the ui:input is a WCheckBox or WRadioButton then
 		the label must be placed after the control and any
 		ui:fieldindicator placed after the label.
	-->
	<xsl:template name="fieldIsCheckRadio">
		<xsl:variable name="labelFor" select="ui:label/@for"/>
		<xsl:variable name="labelledElementLocalName">
			<xsl:if test="ui:label/@for">
				<xsl:value-of select="local-name(key('labelableElementKey',$labelFor)[1])"/>
			</xsl:if>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$labelledElementLocalName='radiobutton' or $labelledElementLocalName='checkbox' or $labelledElementLocalName='selecttoggle'">
				<xsl:number value="1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:number value="0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
