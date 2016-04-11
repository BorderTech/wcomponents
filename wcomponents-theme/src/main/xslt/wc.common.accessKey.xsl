<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.toolTip.xsl"/>
	<!--
		Common helper template for generating the attributes for accessKey implementation
		including the WAI-ARIA property to add extended description information.

		Implementing components

		Every input component implements accessKey and displays its toolTip on
		its associated WLabel (see wc.ui.label.xsl)	element or a HTML Link
		element will implement accessKey directly and will output their own
		toolTip within their HTML artefact.
		wc.ui.button.xsl
		wc.ui.fieldSet.xsl
		wc.ui.link.xsl
		wc.ui.label.xsl
		wc.ui.link.xsl
		wc.ui.button.xsl
		wc.ui.tab.xsl

		accesskey is a global attribute in HTML5 ({{http://www.w3.org/TR/html5/dom.html#global-attributes}})
		but as at July 2013 there are still problems with accesskey on non-input controls
		in some browsers. As support firms up we expect to be able to move all accesskey
		support directly to the implementing component.

		*** READ THIS ***
		If you are changing the XSLT: If you call this template without this
		parameter set 1 then this MUST (ABSOLUTELY MUST) be the last attribute
		on the element since the tooltip helper template will add content to the element.
	-->
	<xsl:template name="accessKey">
		<xsl:param name="useToolTip" select="1"/>
		<xsl:if test="@accessKey">
			<xsl:attribute name="accesskey">
				<xsl:value-of select="@accessKey"/>
			</xsl:attribute>
			<xsl:if test="$useToolTip=1">
				<xsl:attribute name="aria-describedby">
					<xsl:value-of select="concat(@id,'_wctt')"/>
				</xsl:attribute>
				<xsl:call-template name="tooltip"/>
			</xsl:if>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
