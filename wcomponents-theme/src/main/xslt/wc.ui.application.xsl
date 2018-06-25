
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!-- WApplication -->
	<xsl:template match="ui:application">
		<xsl:param name="nojs" select="0"/>
		<xsl:variable name="baseAjaxUrl">
			<xsl:value-of select="@ajaxUrl"/>
		</xsl:variable>
		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:if test="@unsavedChanges or .//html:button[@class and contains(@class, 'wc_unsaved')] or .//ui:menuitem[@unsavedChanges]">
				<xsl:text> wc_unsaved</xsl:text>
			</xsl:if>
		</xsl:variable>
		<form action="{@applicationUrl}" method="post" id="{@id}" data-wc-datalisturl="{$baseAjaxUrl}" novalidate="novalidate" class="{normalize-space(concat('wc-application ', $additional))}">
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
			<xsl:apply-templates />
		</form>
	</xsl:template>

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

	<xsl:template match="ui:css"/>

	<xsl:template match="ui:css" mode="inHead">
		<xsl:text>s.add("</xsl:text>
		<xsl:value-of select="@url"/>
		<xsl:text>");</xsl:text>
	</xsl:template>

	<xsl:template match="ui:js"/>

	<xsl:template match="ui:js" mode="inHead">
		<script type="text/javascript" src="{@url}" async="async"></script>
	</xsl:template>
</xsl:stylesheet>
