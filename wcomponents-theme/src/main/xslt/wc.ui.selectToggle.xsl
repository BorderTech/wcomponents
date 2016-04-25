<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.selectToggle.xsl"/>
	<!--
		Transform for WSelectToggle. This transform creates either:
			* A single control to toggle selection of the target(s); or
			* A pair of text controls to toggle selection of the target(s).
		
		As far as we are concerned these controls are all buttons and differ only in
		visual appearance and what they claim to be in WAI-ARIA.
	-->
	<xsl:template match="ui:selecttoggle">
		<!--
			see wc.common.selectToggle.xsl
		-->
		<xsl:call-template name="selectToggle">
			<xsl:with-param name="for" select="@target"/>
			<xsl:with-param name="name" select="@id"/>
			<xsl:with-param name="roundTrip" select="@roundTrip"/>
			<xsl:with-param name="selected" select="@selected"/>
			<xsl:with-param name="type">
				<xsl:choose>
					<xsl:when test="@renderAs='control'">
						<xsl:text>control</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>text</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:if test="@renderAs='control'">
			<xsl:variable name="myLabel" select="key('labelKey',@id)[1]"/>
			<xsl:if test="$myLabel">
				<xsl:apply-templates select="$myLabel" mode="checkable">
					<xsl:with-param name="labelableElement" select="."/>
				</xsl:apply-templates>
			</xsl:if>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
