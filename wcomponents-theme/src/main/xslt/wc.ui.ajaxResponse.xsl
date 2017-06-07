<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.registrationScripts.xsl"/>
	<!-- ui:ajaxresponse is the root element of a response to an ajax request.-->
	<xsl:template match="ui:ajaxresponse">
		<xsl:choose>
			<xsl:when test="ui:ajaxtarget/node()[not(self::ui:file)]">
				<div class="wc-ajaxresponse">
					<xsl:if test="@defaultFocusId">
						<xsl:attribute name="data-focusid"><xsl:value-of select="@defaultFocusId"/></xsl:attribute>
					</xsl:if>
					<xsl:apply-templates select="*"/>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<html lang="en"><!-- The lang is hardcodeed but the pseudo ajax stuff is pretty much dead -->
					<head>
						<title>
							<xsl:text>Pseudo AJAX iframe</xsl:text>
						</title>
					</head>
					<body>
						<xsl:apply-templates mode="pseudoAjax"/>
					</body>
				</html>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!--
		ui:ajaxtarget is a child of ui:ajaxresponse.
		
		The main point of this template is a simple pass-through to output the contained elementss and run the registration scripts to wire up new
		onload functionality.
	-->
	<xsl:template match="ui:ajaxtarget">
		<div class="wc-ajaxtarget" data-id="{@id}" data-action="{@action}">
			<xsl:apply-templates />
			<xsl:call-template name="registrationScripts" />
		</div>
	</xsl:template>
	
	<!--
		This mode is invoked in the faux-ajax used to do inline multi file uploads.
	-->
	<xsl:template match="ui:ajaxtarget" mode="pseudoAjax">
		<xsl:apply-templates />
	</xsl:template>
</xsl:stylesheet>
