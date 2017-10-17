<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.offscreenSpan.xsl"/>
	<xsl:import href="wc.common.icon.xsl"/>

	<!--
		Transform for ui:fieldset which is the XML output of WFieldSet.
	-->
	<xsl:template match="ui:fieldset">
		<xsl:variable name="frame">
			<xsl:value-of select="@frame"/>
		</xsl:variable>
		<fieldset>
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="isWrapper" select="1"/>
				<xsl:with-param name="class">
					<xsl:if test="$frame eq 'noborder' or $frame eq 'none'">
						<xsl:text>wc_noborder</xsl:text>
					</xsl:if>
					<xsl:if test="@required">
						<xsl:text> wc_req</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="isInvalid"/>
			<legend>
				<xsl:if test="$frame eq 'notext' or $frame eq 'none'">
					<xsl:attribute name="class">
						<xsl:text>wc-off</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:call-template name="accessKey"/>
				<xsl:apply-templates select="ui:decoratedlabel"/>
				<xsl:call-template name="icon">
					<xsl:with-param name="class" select="'fa-asterisk'"/>
				</xsl:call-template>
			</legend>
			<xsl:apply-templates select="ui:content" mode="passthru"/>
			<xsl:apply-templates select="ui:fieldindicator"/>
		</fieldset>
	</xsl:template>
</xsl:stylesheet>
