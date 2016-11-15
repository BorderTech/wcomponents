<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Output a track element inside a video element.
	-->
	<xsl:template match="ui:track">
		<track src="{@src}">
			<xsl:if test="@lang">
				<xsl:attribute name="srclang">
					<xsl:value-of select="@lang"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@desc">
				<xsl:attribute name="label">
					<xsl:value-of select="@desc"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@kind">
				<xsl:attribute name="kind">
					<xsl:value-of select="@kind"/>
				</xsl:attribute>
			</xsl:if>
		</track>
	</xsl:template>

	<!--
	 Output an A element linking to a track file.
	-->
	<xsl:template match="ui:track" mode="link">
		<a href="{@src}" class="wc-track wc-icon">
			<xsl:if test="@lang">
				<xsl:attribute name="lang">
					<xsl:value-of select="@lang"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="data-wc-attach">
				<xsl:text>data-wc-attach</xsl:text>
			</xsl:attribute>
			<xsl:if test="@desc">
				<xsl:value-of select="@desc"/>
			</xsl:if>
			<xsl:if test="@kind">
				<xsl:text> (</xsl:text>
				<xsl:value-of select="@kind"/>
				<xsl:text> )</xsl:text>
			</xsl:if>
		</a>
		<xsl:if test="position() ne last()">
			<xsl:value-of select="' '"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
