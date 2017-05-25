<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.icon.xsl"/>

	<xsl:template name="feedbackbox">
		<xsl:param name="type" select="@type"/>
		<xsl:param name="class" select="''"/>
		<section id="{@id}">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:value-of select="$class"/>
					<xsl:text> wc_msgbox</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
			<h1>
				<xsl:call-template name="icon">
					<xsl:with-param name="class">
						<xsl:text>fa-fw </xsl:text>
						<xsl:choose>
							<xsl:when test="$type eq 'error'">
								<xsl:text>fa-minus-circle</xsl:text>
							</xsl:when>
							<xsl:when test="$type eq 'warn'">
								<xsl:text>fa-exclamation-triangle</xsl:text>
							</xsl:when>
							<xsl:when test="$type eq 'info'">
								<xsl:text>fa-info-circle</xsl:text>
							</xsl:when>
							<xsl:when test="$type eq 'success'">
								<xsl:text>fa-check-circle</xsl:text>
							</xsl:when>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="element" select="'i'"/>
				</xsl:call-template>
				<span>
					<xsl:choose>
						<xsl:when test="@title">
							<xsl:value-of select="@title"/>
						</xsl:when>
						<xsl:when test="$type eq 'error'">
							<xsl:text>{{#i18n}}messagetitle_error{{/i18n}}</xsl:text>
						</xsl:when>
						<xsl:when test="$type eq 'warn'">
							<xsl:text>{{#i18n}}messagetitle_warn{{/i18n}}</xsl:text>
						</xsl:when>
						<xsl:when test="$type eq 'info'">
							<xsl:text>{{#i18n}}messagetitle_info{{/i18n}}</xsl:text>
						</xsl:when>
						<xsl:when test="$type eq 'success'">
							<xsl:text>{{#i18n}}messagetitle_success{{/i18n}}</xsl:text>
						</xsl:when>
					</xsl:choose>
				</span>
			</h1>
			<div class="wc_messages">
				<xsl:apply-templates select="*"/>
			</div>
		</section>
	</xsl:template>
</xsl:stylesheet>
