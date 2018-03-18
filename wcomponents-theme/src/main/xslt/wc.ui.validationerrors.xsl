<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.icon.xsl"/>
	<!-- WValidationErrors. -->
	<xsl:template match="ui:validationerrors"><!--
		<section id="{@id}">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:text>wc-messagebox-type-error wc_msgbox</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
			<h1>
				<xsl:call-template name="icon">
					<xsl:with-param name="class">
						<xsl:text>fa-fw a-minus-circle</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
				<span>
					<xsl:choose>
						<xsl:when test="@title">
							<xsl:value-of select="@title"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>{{#i18n}}messagetitle_error{{/i18n}}</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</span>
			</h1>
			<div class="wc_messages">
				<xsl:apply-templates select="*"/>
			</div>
		</section>-->
	</xsl:template>
	
	<!-- The error -->
	<xsl:template match="ui:error">
		<!--<div class="wc-error">
			<a href="{concat('#',@for)}">
				<xsl:apply-templates />
			</a>
		</div>-->
	</xsl:template>
</xsl:stylesheet>
