
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<xsl:template match="ui:suggestions">
		<span id="{@id}" role="listbox" class="wc-suggestions">
			<xsl:if test="@min">
				<xsl:attribute name="data-wc-minchars">
					<xsl:value-of select="@min"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@data">
				<xsl:attribute name="data-wc-list">
					<xsl:value-of select="@data"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@ajax">
				<xsl:attribute name="data-wc-chat">
					<xsl:value-of select="1"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@autocomplete">
				<xsl:attribute name="data-wc-auto">
					<xsl:value-of select="@autocomplete"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="not(*)">
				<xsl:attribute name="aria-busy">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="ui:suggestion"/>
		</span>
	</xsl:template>

	<xsl:template match="ui:suggestion">
		<xsl:variable name="value" select="@value"/>
		<span data-wc-value="{$value}" role="option" class="wc-suggestion wc-invite" tabIndex="0">
			<xsl:value-of select="$value"/>
		</span>
	</xsl:template>
</xsl:stylesheet>
