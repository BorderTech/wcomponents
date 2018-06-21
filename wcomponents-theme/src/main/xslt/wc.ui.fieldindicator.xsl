
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<xsl:template match="ui:fieldindicator">
		<span class="{normalize-space(concat('wc-fieldindicator wc-fieldindicator-type-', @type, ' ', @class))}" data-wc-dfor="{@for}">
		  <xsl:if test="@id">
				<xsl:attribute name="id">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:variable name="iconclass">
				<xsl:choose>
				  <xsl:when test="@type='error'">
				    <xsl:text>fa-times-circle</xsl:text>
				  </xsl:when>
				  <xsl:when test="@type='warn'">
				    <xsl:text>fa-exclamation-triangle</xsl:text>
				  </xsl:when>
				  <!-- NOTE: type info and type success should also be available -->
				  <xsl:when test="@type='info'">
				    <xsl:text>fa-info-circle</xsl:text>
				  </xsl:when>
				  <xsl:otherwise>
				    <xsl:text>fa-check-circle</xsl:text>
				  </xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<i aria-hidden="true" class="fa {$iconclass}"></i>
			<xsl:apply-templates select="ui:message" mode="fieldindicator" />
		</span>
	</xsl:template>

	<xsl:template match="ui:message" mode="fieldindicator">
		<span class="wc-message">
			<xsl:apply-templates />
		</span>
	</xsl:template>

</xsl:stylesheet>
