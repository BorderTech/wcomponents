<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!-- WApplication -->
	<xsl:template match="ui:application">
		<xsl:variable name="baseAjaxUrl">
			<xsl:value-of select="@ajaxUrl"/>
		</xsl:variable>
		<form action="{@applicationUrl}" method="POST" id="{@id}" data-wc-datalisturl="{$baseAjaxUrl}" novalidate="novalidate" hidden="hidden">
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
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="@unsavedChanges or .//ui:button[@unsavedChanges] or .//ui:menuitem[@unsavedChanges]">
						<xsl:text> wc_unsaved</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="ajaxTarget"/>
			<xsl:apply-templates/>
		</form>
	</xsl:template>

	<!-- If you have managed to ignore all advice and nest a WApplication inside either another WApplication you get nothing. -->
	<xsl:template match="ui:application[ancestor::ui:application]"/>
	<!-- Application parameters, output as hidden input elements -->
	<xsl:template match="ui:application/ui:param">
		<xsl:element name="input">
			<xsl:attribute name="type">
				<xsl:text>hidden</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="@name"/>
			</xsl:attribute>
			<xsl:attribute name="value">
				<xsl:value-of select="@value"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>

	<!-- Application parameters, output as get url name:value pairs. -->
	<xsl:template match="ui:application/ui:param" mode="get">
		<xsl:value-of select="concat(@name,'=',@value)"/>
		<xsl:if test="position() ne last()">
			<xsl:text>&amp;</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
