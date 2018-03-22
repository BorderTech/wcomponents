<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>

	<!--
		Transform for ui:fieldset which is the XML output of WFieldSet.
	-->
	<xsl:template match="ui:fieldset">
		<fieldset id="{@id}">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="@frame eq 'noborder' or @frame eq 'none'">
						<xsl:text>wc_noborder</xsl:text>
					</xsl:if>
					<xsl:if test="@required">
						<xsl:text> wc_req</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="ui:fieldindicator[not(@type='warn')]">
				<xsl:attribute name="aria-invalid">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<legend>
				<xsl:if test="@frame eq 'notext' or @frame eq 'none'">
					<xsl:attribute name="class">
						<xsl:text>wc-off</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@accessKey">
					<xsl:attribute name="accesskey">
						<xsl:value-of select="@accessKey"/>
					</xsl:attribute>
					<xsl:attribute name="aria-describedby">
						<xsl:value-of select="concat(@id,'_wctt')"/>
					</xsl:attribute>
					<span id="{concat(@id,'_wctt')}" role="tooltip" hidden="hidden">
						<xsl:value-of select="@accessKey"/>
					</span>
				</xsl:if>
				<xsl:apply-templates select="ui:decoratedlabel"/>
				<i aria-hidden="true" class="fa fa-asterisk"></i>
			</legend>
			<xsl:apply-templates select="ui:content" mode="passthru"/>
			<xsl:apply-templates select="ui:fieldindicator"/>
		</fieldset>
	</xsl:template>

	<xsl:template match="ui:content" mode="passthru">
		<xsl:apply-templates />
	</xsl:template>
</xsl:stylesheet>
