<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.feedback.xsl"/>
	<!-- WValidationErrors. -->
	<xsl:template match="ui:validationerrors">
		<xsl:call-template name="feedbackbox">
			<xsl:with-param name="type">
				<xsl:text>error</xsl:text>
			</xsl:with-param>
			<xsl:with-param name="class">
				<xsl:text>wc-messagebox-type-error</xsl:text>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<!-- The error -->
	<xsl:template match="ui:error">
		<div class="wc-error">
			<a href="{concat('#',@for)}">
				<xsl:apply-templates />
			</a>
		</div>
	</xsl:template>
</xsl:stylesheet>
