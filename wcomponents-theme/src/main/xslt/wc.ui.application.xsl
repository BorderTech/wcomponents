<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.debug.application.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<!--
		ui:application is the base component of each application. A screen may, however,
		contain 0 - n applications (though a screen with no applications is pretty
		useless). Therefore ui:application is not the screen root element.
	
		The ui:application transforms to a HTML form element. Therefore WApplications
		must not be nested.
	-->
	<xsl:template match="ui:application">
		<xsl:variable name="baseAjaxUrl">
			<xsl:value-of select="@ajaxUrl"/>
		</xsl:variable>
		<form action="{@applicationUrl}" method="POST" id="{@id}" data-wc-datalisturl="{@dataUrl}">
			<xsl:attribute name="data-wc-ajaxurl">
				<xsl:value-of select="$baseAjaxUrl"/>
				<xsl:if test="ui:param">
					<xsl:choose>
						<xsl:when test="contains($baseAjaxUrl, '?')">
							<xsl:text>&amp;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>?</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:apply-templates select="ui:param" mode="get"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test="@unsavedChanges or .//ui:button[@unsavedChanges] or .//ui:menuItem[@unsavedChanges]">
				<xsl:attribute name="class">
					<xsl:text>wc_unsaved</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<!-- this ANT property sets the formnovalidate attribute -->
			${wc.ui.application.xslt.HTML5clientSideValidation}
			<xsl:call-template name="ajaxTarget"/>
			<xsl:if test="$isDebug=1">
				<xsl:call-template name="application-debug"/>
			</xsl:if>
			<xsl:apply-templates/>
			<xsl:apply-templates select=".//ui:dialog[ui:content][1]" mode="withcontent"/>
		</form>
	</xsl:template>

	<!--
		If you have managed to ignore all advice and nest a WApplication inside
		either another WApplication or a HTML FORM element, well, you deserve 
		what you get. You deserve nothing.
	-->
	<xsl:template match="ui:application[ancestor::ui:application]"/>
</xsl:stylesheet>
