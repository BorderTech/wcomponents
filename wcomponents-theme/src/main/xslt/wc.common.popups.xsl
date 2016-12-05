<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Helper templates used by component which create pop ups.Outputs a String
		formatted as the attributes required by JavaScript window.open function.
		See:
			wc.ui.link.xsl
			wc.ui.popup.xsl
	-->
	<xsl:template name="getPopupSpecs">
		<xsl:if test="@top or @left or @width or @height or @showMenuBar or @showToolbar or @showLocation or @showStatus">
			<xsl:if test="@top">
				<xsl:value-of select="concat('top=',@top,'px,')"/>
			</xsl:if>
			<xsl:if test="@left">
				<xsl:value-of select="concat('left=',@left,'px,')"/>
			</xsl:if>
			<xsl:if test="@width">
				<xsl:value-of select="concat('width=',@width,'px,')"/>
			</xsl:if>
			<xsl:if test="@height">
				<xsl:value-of select="concat('height=',@height,'px,')"/>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="@showMenubar">
					<xsl:text>menubar=yes,</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>menubar=no,</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="@showToolbar">
					<xsl:text>toolbar=yes,</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>toolbar=no,</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="@showLocation">
					<xsl:text>location=yes,</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>location=no,</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="@showStatus">
					<xsl:text>status=yes,</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>status=no,</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>resizable=yes,</xsl:text>
			<xsl:text>scrollbars=yes</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
