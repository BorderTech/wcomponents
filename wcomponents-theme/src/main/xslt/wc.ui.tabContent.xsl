<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.constants.xsl"/>

	<!--
	 The content of the tab. Wrapped in a DIV element.
	-->
	<xsl:template match="ui:tabContent">
		<xsl:param name="open"/>
		<xsl:variable name="id">
			<xsl:value-of select="@id"/>
		</xsl:variable>
		<xsl:variable name="mode" select="../@mode"/>
		<xsl:element name="div">
			<xsl:attribute name="id">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:attribute name="role">
				<xsl:text>tabpanel</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>tabContent</xsl:text>
				<xsl:if test="$mode='server'">
					<xsl:text> wc_lame</xsl:text>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="$open=1">
						<xsl:if test="$mode='dynamic'">
							<xsl:text> wc_magic wc_dynamic</xsl:text>
						</xsl:if>
					</xsl:when>
					<xsl:when test="($mode='lazy') or ($mode='eager') or ($mode='dynamic')">
						<xsl:text> wc_magic</xsl:text>
						<xsl:if test="$mode='dynamic'">
							<xsl:text> wc_dynamic</xsl:text>
						</xsl:if>
					</xsl:when>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="$open!=1">
				<xsl:call-template name="hiddenElement"/>
			</xsl:if>
			<xsl:if test="($mode='lazy') or ($mode='eager') or ($mode='dynamic')">
				<xsl:attribute name="data-wc-ajaxalias">
					<xsl:value-of select="../@id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="setARIALive"/>
			<xsl:variable name="contentHeight" select="ancestor::ui:tabset[1]/@contentHeight"/>
			<xsl:if test="$contentHeight and $contentHeight!=''">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('height:',$contentHeight,';overflow-y:auto;')"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="../@disabled or ancestor::ui:tabset[1]/@disabled">
				<xsl:attribute name="aria-disabled">
					<xsl:copy-of select="$t"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
