<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.fauxOption.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<xsl:template match="ui:suggestions">
		<xsl:if test="parent::ui:ajaxtarget">
			<xsl:call-template name="suggestions"/>
		</xsl:if>
	</xsl:template>
	<!--
		This is a transform for WSuggestions. This component is designed to convert a text input element into a combo.
		
		NOTE: we are still using the combo polyfill because the behaviour of dynamically generated and updated native 
		input[@list]/datalist pairs gives sub-optimal UX.
	-->
	
	<xsl:template match="ui:suggestions" mode="inline">
		<xsl:call-template name="suggestions"/>
	</xsl:template>
	
	<xsl:template name="suggestions">
		<span id="{@id}" role="listbox">
			<xsl:call-template name="makeCommonClass"/>
			<xsl:if test="@min">
				<xsl:attribute name="data-wc-minchars">
					<xsl:value-of select="@min"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@data">
				<xsl:attribute name="data-wc-list">
					<xsl:value-of select="@data"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@ajax">
				<xsl:attribute name="data-wc-chat">
					<xsl:value-of select="1"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="not(*)">
				<xsl:choose>
					<xsl:when test="not(@ajax) or parent::ui:ajaxtarget">
						<span role="option" hidden="hidden"></span>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="aria-busy">
							<xsl:copy-of select="$t"/>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:apply-templates select="ui:suggestion"/>
		</span>
	</xsl:template>

	<xsl:template match="ui:suggestion">
		<xsl:call-template name="fauxOption"/>
	</xsl:template>
</xsl:stylesheet>
