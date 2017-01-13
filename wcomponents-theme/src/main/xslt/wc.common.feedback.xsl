<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
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
			<h1 class="wc-icon">
				<span>
					<xsl:choose>
						<xsl:when test="@title">
							<xsl:value-of select="@title"/>
						</xsl:when>
						<xsl:when test="$type eq 'error'">
							<xsl:text>{{t 'messagetitle_error'}}</xsl:text>
						</xsl:when>
						<xsl:when test="$type eq 'warn'">
							<xsl:text>{{t 'messagetitle_warn'}}</xsl:text>
						</xsl:when>
						<xsl:when test="$type eq 'info'">
							<xsl:text>{{t 'messagetitle_info'}}</xsl:text>
						</xsl:when>
						<xsl:when test="$type eq 'success'">
							<xsl:text>{{t 'messagetitle_success'}}</xsl:text>
						</xsl:when>
					</xsl:choose>
				</span>
			</h1>
			<div class="wc_messages">
				<xsl:apply-templates />
			</div>
		</section>
	</xsl:template>
</xsl:stylesheet>
