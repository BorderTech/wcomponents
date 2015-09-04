<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.ui.label.n.WLabelHint.xsl"/>
	
	<!--
		Helper to make label hints which include a potential submitOnChange
		warning. 
		
		See wc.ui.label.n.makeLabel.xsl and wc.ui.label.makeFauxLabel.xsl
		
		param element: the XML element the label is 'for'. This should never be
		null but is best tested just in case.
		
		param readOnly: the previously calculated readOnly state of the element
		the label is 'for'.
	-->
	<xsl:template name="labelHintHelper">
		<xsl:param name="element"/>
		<xsl:param name="readOnly" select="0"/>
		<!--
			Submit on change warning
			
			This is a WCAG 2.0 Level A requirement that any element, other than a button or 
			link, which causes a change of context must expose this to all users
		-->
		
		<xsl:variable name="submitOnChange">
			<xsl:choose>
				<xsl:when test="$readOnly=1">
					<xsl:number value="0"/>
				</xsl:when>
				<xsl:when test="$element and $element/@submitOnChange">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:call-template name="WLabelHint">
			<xsl:with-param name="submitNotAjaxTrigger">
				<xsl:if test="$submitOnChange=1 and count(key('triggerKey',@for))=0">
					<xsl:number value="1"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>
