<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.constants.xsl"/>

	<!--
	 The content of the tab. Wrapped in a DIV element.
	-->
	<xsl:template match="ui:tabcontent">
		<xsl:param name="tabset"/>
		
		<xsl:variable name="open">
			<xsl:if test="../@open">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		
		<xsl:variable name="mode" select="../@mode"/>
		<xsl:variable name="contentHeight" select="$tabset/@contentHeight"/>
		
		<div id="{@id}" role="tabpanel">
			<xsl:attribute name="class">
				<xsl:text>wc-tabcontent</xsl:text>
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

			<xsl:if test="../@disabled or $tabset/@disabled">
				<xsl:attribute name="aria-disabled">
					<xsl:copy-of select="$t"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$contentHeight and $contentHeight!=''">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('height:',$contentHeight,';overflow-y:auto;')"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:apply-templates/>
		</div>
	</xsl:template>
</xsl:stylesheet>
