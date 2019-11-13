
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!--
		Transform of WPopup. This component opens a new window on page load.

		Popup windows are often considered harmful to accessibility If WPopup must be used then any WButton which results in a screen with a WPopup
		must have its popup property set true.

		It has no explicit HTML artefact in the UI and therefore has a null template.
	-->
	<xsl:template match="ui:popup"/>
	<!--
		This template creates the JSON objects required to create the popup on page load.
	-->
	<xsl:template match="ui:popup" mode="JS">
		<xsl:text>["</xsl:text>
		<xsl:value-of select="@url"/>
		<xsl:text>","</xsl:text>
		<xsl:choose>
			<xsl:when test="@targetWindow">
				<xsl:value-of select="@targetWindow"/>
			</xsl:when>
			<xsl:otherwise>
				<!-- It is safe to keep this generated id, it is not used as an element id but as a stand in for window name-->
				<xsl:value-of select="generate-id(.)"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>","</xsl:text>
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
		<xsl:text>"]</xsl:text>
		<xsl:if test="position() ne last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
