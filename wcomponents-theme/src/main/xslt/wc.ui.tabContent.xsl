<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
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
			<xsl:choose>
				<xsl:when test="../@open">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="mode" select="../@mode"/>
		
		<div id="{@id}" role="tabpanel">
			<xsl:attribute name="class">
				<xsl:text>wc-tabcontent</xsl:text>
				<xsl:choose>
					<xsl:when test="number($open) eq 1">
						<xsl:if test="$mode eq 'dynamic'">
							<xsl:text> wc_magic wc_dynamic</xsl:text>
						</xsl:if>
					</xsl:when>
					<xsl:when test="($mode eq 'lazy') or ($mode eq 'eager') or ($mode eq 'dynamic')">
						<xsl:text> wc_magic</xsl:text>
						<xsl:if test="$mode eq 'dynamic'">
							<xsl:text> wc_dynamic</xsl:text>
						</xsl:if>
					</xsl:when>
				</xsl:choose>
			</xsl:attribute>

			<xsl:if test="number($open) ne 1">
				<xsl:call-template name="hiddenElement"/>
			</xsl:if>

			<xsl:if test="($mode eq 'lazy') or ($mode eq 'eager') or ($mode eq 'dynamic')">
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

			<xsl:if test="$tabset/@contentHeight">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('height:',$tabset/@contentHeight,';overflow-y:auto;')"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:apply-templates/>
		</div>
	</xsl:template>
</xsl:stylesheet>
