<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		This template builds the basic tabset. The tabset is a wrapper container. It
		has a list of tabs and content. The order of these is dependent upon the tabset
		type.
	-->
	<xsl:template match="ui:tabset">
		<div id="{@id}">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:value-of select="concat('wc_', @type)"/>
				</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="disabledElement"/>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:call-template name="ajaxTarget"/>

			<xsl:apply-templates select="ui:margin"/>
			
			<div role="tablist">
				<xsl:if test="@type='accordion'">
					<xsl:attribute name="aria-multiselectable">
						<xsl:choose>
							<xsl:when test="@single">false</xsl:when>
							<xsl:otherwise>true</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates select="ui:tab|ui:tabgroup/ui:tab">
					<xsl:with-param name="tabset" select="."/>
					<xsl:with-param name="numAvailTabs" select="count(ui:tab[@open and not(@disabled)]|ui:tabgroup/ui:tab[@open and not(@disabled)])"/>
				</xsl:apply-templates>
			</div>
			<xsl:if test="not(@type='accordion')">
				<xsl:apply-templates select="ui:tab|ui:tabgroup/ui:tab" mode="content">
					<xsl:with-param name="tabset" select="."/>
				</xsl:apply-templates>
			</xsl:if>
		</div>
	</xsl:template>
</xsl:stylesheet>
