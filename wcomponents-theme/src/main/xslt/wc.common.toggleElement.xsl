<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!-- Helper templates and keys for common state toggling elements. -->
	<xsl:template name="toggleElement">
		<xsl:param name="name" select="''"/>
		<xsl:param name="value" select="''"/>
		<xsl:param name="text" select="''"/>
		<xsl:param name="class" select="''"/>
		<xsl:param name="selected" select="0"/>
		<xsl:variable name="localClass">
			<xsl:text>wc-linkbutton</xsl:text>
			<xsl:if test="$class ne ''">
				<xsl:value-of select="concat(' ',$class)"/>
			</xsl:if>
		</xsl:variable>
		<button role="radio" class="{$localClass}" data-wc-value="{$value}" type="button">
			<xsl:if test="$name ne ''">
				<xsl:attribute name="data-wc-name">
					<xsl:value-of select="$name"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="aria-checked">
				<xsl:choose>
					<xsl:when test="number($selected) eq 1">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="self::ui:selecttoggle"><!-- WCollapsibleToggle does not have a disabled state. -->
				<xsl:call-template name="disabledElement">
					<xsl:with-param name="isControl" select="1"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:value-of select="$text"/>
		</button>
	</xsl:template>
</xsl:stylesheet>
