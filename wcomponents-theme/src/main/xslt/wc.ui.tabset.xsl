<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!--
		This template builds the basic tabset. The tabset is a wrapper container. It has a list of tabs and content.
	-->
	<xsl:template match="ui:tabset">
		<div id="{@id}">
			<xsl:call-template name="makeCommonClass"/>
			<xsl:call-template name="disabledElement"/>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<div role="tablist">
				<xsl:if test="@type eq 'accordion'">
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
			<xsl:if test="not(@type eq 'accordion')">
				<xsl:apply-templates select="ui:tab|ui:tabgroup/ui:tab" mode="content">
					<xsl:with-param name="tabset" select="."/>
				</xsl:apply-templates>
			</xsl:if>
		</div>
	</xsl:template>

	<!--
		Tabs should not be grouped in the UI. There is no facility for subgroupings of elements with a role of tab in WAI-ARIA.
	-->	
	<xsl:template match="ui:tabgroup"/>
</xsl:stylesheet>
