<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		"Hint" handling in ui:label/WLabel. This is unnecessarily complicated because of the way we allow input controls
		to have a submitOnChange mechanism. 
		
		In an ideal world the submitOnChange flag would simply not exist. It is a hangover from design practices of the
		past so we have to work with it.
		
		We add a VISIBLE warning to the label if the control has submitOnChange and is not an ajax trigger. At the 
		moment we do not force the containing label/surrogate to be visible (except in the case of LEGENDs) but that 
		will change.
		
		see:
		* wc.ui.label.n.labelHintHelper.xsl
		* wc.ui.label.n.makeLabelForNothing.xsl and
		* wc.ui.label_legend.xsl.
		
		param submitNotAjaxTrigger: this needs to be pre-calculated. If set it will be xsl:number 1.
	-->
	<xsl:template name="WLabelHint">
		<xsl:param name="submitNotAjaxTrigger" select="0"/>
		<xsl:if test="@hint or number($submitNotAjaxTrigger) eq 1">
			<span>
				<xsl:attribute name="class">
					<xsl:text>wc-label-hint</xsl:text>
				</xsl:attribute>
				<xsl:value-of select="@hint"/>
				<xsl:if test="number($submitNotAjaxTrigger) eq 1">
					<xsl:if test="@hint">
						<xsl:element name="br"/>
					</xsl:if>
					<xsl:text>{{t 'submitOnChange'}}</xsl:text>
				</xsl:if>
			</span>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
