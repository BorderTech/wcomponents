
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!-- WMessageBox -->
	<xsl:template match="ui:messagebox">
		<section id="{@id}" class="{normalize-space(concat('wc-messagebox wc-messagebox-type-', @type, ' ', @class))}">
			<h1>
				<xsl:variable name="iconclass">
					<xsl:text>fa-fw </xsl:text>
					<xsl:choose>
						<xsl:when test="@type eq 'error'">
							<xsl:text>fa-minus-circle</xsl:text>
						</xsl:when>
						<xsl:when test="@type eq 'warn'">
							<xsl:text>fa-exclamation-triangle</xsl:text>
						</xsl:when>
						<xsl:when test="@type eq 'info'">
							<xsl:text>fa-info-circle</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>fa-check-circle</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<i aria-hidden="true" class="fa {$iconclass}"></i>
				<span>
					<xsl:choose>
						<xsl:when test="@title">
							<xsl:value-of select="@title"/>
						</xsl:when>
						<xsl:when test="@type eq 'error'">
							<xsl:text>{{#i18n}}messagetitle_error{{/i18n}}</xsl:text>
						</xsl:when>
						<xsl:when test="@type eq 'warn'">
							<xsl:text>{{#i18n}}messagetitle_warn{{/i18n}}</xsl:text>
						</xsl:when>
						<xsl:when test="@type eq 'info'">
							<xsl:text>{{#i18n}}messagetitle_info{{/i18n}}</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>{{#i18n}}messagetitle_success{{/i18n}}</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</span>
			</h1>
			<div class="wc_messages">
				<xsl:apply-templates select="ui:message" />
			</div>
		</section>
	</xsl:template>

	<xsl:template match="ui:message">
		<div class="wc-message">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- WValidationErrors. -->
	<xsl:template match="ui:validationerrors|ui:error"/>

</xsl:stylesheet>
