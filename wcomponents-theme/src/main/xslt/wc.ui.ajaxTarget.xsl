<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml"
	version="1.0">

	<xsl:import href="wc.common.registrationScripts.xsl"/>
	<!--
		ui:ajaxtarget is a child of ui:ajaxresponse (wc.ui.ajaxResponse.xsl).

		The main point of this template is a simple pass-through to output the contained
		elements. The order of application is important here. We have to apply all
		templates then build any included dialogs and then run the registration scripts
		to wire up new onload functionality.
	-->
	<xsl:template match="ui:ajaxtarget">
		<xsl:element name="div">
			<xsl:attribute name="class">wc-ajaxtarget</xsl:attribute>
			<xsl:attribute name="data-id"><xsl:value-of select="@id"/></xsl:attribute>
			<xsl:attribute name="data-action"><xsl:value-of select="@action"/></xsl:attribute>
			<xsl:apply-templates />
			<xsl:call-template name="registrationScripts" />
		</xsl:element>
	</xsl:template>

	<!--
		This mode is invoked in the faux-ajax used to do inline multi file uploads. It
		is only required to pass through to output the contained elements. You
		may want to take a look at wc.ui.fileUpload.xsl and wc.ui.fileUpload.js
	-->
	<xsl:template match="ui:ajaxtarget" mode="pseudoAjax">
		<xsl:apply-templates />
	</xsl:template>
</xsl:stylesheet>
