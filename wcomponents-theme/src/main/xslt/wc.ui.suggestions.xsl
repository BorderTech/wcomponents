<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.fauxOption.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		This is a transform for WSuggestions. This component is designed to convert a text input element into a combo.
		
		NOTE: we are still using the combo polyfill because the behaviour of dynamically generated and updated native 
		input[@list]/datalist pairs gives sub-optimal UX.
	-->
	<xsl:template match="ui:suggestions">
		<ul id="{@id}" role="listbox" hidden="hidden">
			<xsl:call-template name="makeCommonClass"/>
			<xsl:if test="@min">
				<xsl:attribute name="${wc.ui.combo.list.attrib.minChars}">
					<xsl:value-of select="@min"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@data">
				<xsl:attribute name="${wc.ui.selectLoader.attribute.dataListId}">
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
						<li role="option" hidden="hidden"></li>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="aria-busy">
							<xsl:copy-of select="$t"/>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:apply-templates select="*"/>
		</ul>
	</xsl:template>

	<xsl:template match="ui:suggestion">
		<xsl:call-template name="fauxOption"/>
	</xsl:template>
</xsl:stylesheet>
