<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
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
		<xsl:element name="div">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<!--
				attribute aria-describedby
				The content is controlled by the SUMMARY element but is described by the content
				of the SUMMARY element which will be the WDecoratedLabel child of the WCollapsible.
			-->
			<xsl:attribute name="aria-describedby">
				<xsl:value-of select="../ui:decoratedLabel/@id"/>
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test="$mode='dynamic' or $mode='eager' or ($mode='lazy' and ../@collapsed)">
					<xsl:attribute name="class">
						<xsl:text>wc_magic</xsl:text>
						<xsl:if test="$mode='dynamic'">
							<xsl:text> wc_dynamic</xsl:text>
						</xsl:if>
					</xsl:attribute>
					<xsl:attribute name="data-wc-ajaxalias">
						<xsl:value-of select="../@id"/>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="$mode='server'">
					<xsl:attribute name="class">
						<xsl:text>wc_lame</xsl:text>
					</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
