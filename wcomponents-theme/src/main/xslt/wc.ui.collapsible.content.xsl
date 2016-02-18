<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		The ui:content child of the collapsible is transformed to a single container. 
		This is somewhat superfluous for the collapsing purposes in HTML5 as the 
		showing and hiding can be managed easily in CSS. However, it provides a 
		convenient means to wire up the ajax loading of content without needing any 
		special client side code for this component.
	-->
	<xsl:template match="ui:content" mode="collapsible">
		<xsl:variable name="mode" select="../@mode"/>
		<xsl:variable name="isAjax">
			<xsl:if test="$mode='dynamic' or $mode='eager' or ($mode='lazy' and ../@collapsed)">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		<!--
			attribute aria-describedby
			The content is controlled by the SUMMARY element but is described by the content
			of the SUMMARY element which will be the WDecoratedLabel child of the WCollapsible.
		-->
		<div id="{@id}" aria-describedby="{../ui:decoratedlabel/@id}">
			<xsl:attribute name="class">
				<xsl:text>wc-content</xsl:text>
			<xsl:choose>
				<xsl:when test="$isAjax=1">
					<xsl:text> wc_magic</xsl:text>
					<xsl:if test="$mode='dynamic'">
						<xsl:text> wc_dynamic</xsl:text>
					</xsl:if>
				</xsl:when>
				<xsl:when test="$mode='server'">
					<xsl:text> wc_lame</xsl:text>
				</xsl:when>
			</xsl:choose>
			</xsl:attribute>
			<xsl:if test="$isAjax=1">
				<xsl:attribute name="data-wc-ajaxalias">
					<xsl:value-of select="../@id"/>
				</xsl:attribute></xsl:if>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
</xsl:stylesheet>
