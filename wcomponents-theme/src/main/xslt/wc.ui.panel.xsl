<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.aria.live.xsl"/>
	<!--
		WPanel is the basic layout component in the framework. Genreally output as
		a "block" container (usually div).

		Child elements
		optional ui:margin and exactly one of:
			* ui:borderlayout
			* ui:columnlayout
			* ui:content
			* ui:flowlayout
			* ui:gridlayout
			* ui:listlayout
	-->
	<xsl:template match="ui:panel">
		<xsl:param name="type" select="@type"/>
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="containerElement">
			<xsl:choose>
				<xsl:when test="$type eq 'chrome' or $type eq 'action'">
					<xsl:text>section</xsl:text>
				</xsl:when>
				<xsl:when test="$type eq 'header' or $type eq 'footer'">
					<xsl:value-of select="$type"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>div</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$containerElement}">
			<xsl:attribute name="id">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:choose>
						<xsl:when test="(@mode eq 'lazy' and @hidden)"><xsl:text> wc_magic</xsl:text></xsl:when>
						<xsl:when test="@mode eq 'dynamic'"><xsl:text> wc_magic wc_dynamic</xsl:text></xsl:when>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:if test="@buttonId">
				<xsl:attribute name="data-wc-submit">
					<xsl:value-of select="@buttonId"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$type eq 'header'">
				<xsl:attribute name="role">
					<xsl:text>banner</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@mode">
				<xsl:call-template name="setARIALive"/>
			</xsl:if>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:if test="*[not(self::ui:margin)]/node() or not(@mode eq 'eager')">
				<xsl:if test="(@type eq 'chrome' or @type eq 'action')">
					<h1>
						<xsl:value-of select="normalize-space(@title)"/>
					</h1>
				</xsl:if>
				<!--
					We have split out preping the child elements into a helper template
					so that implementations can easily override the way templates are
					applied. Call this last.
				-->
				<xsl:apply-templates select="*[not(self::ui:margin)]"/>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<!--
		Make the skipLink links to panels which have accessKey and title attributes set.
	-->
	<xsl:template match="ui:panel" mode="skiplinks">
		<a href="#{@id}" class="wc-skiplink">
			<xsl:value-of select="@title"/>
		</a>
	</xsl:template>
</xsl:stylesheet>
