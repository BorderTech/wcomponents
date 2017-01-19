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
			<xsl:call-template name="disabledElement">
				<xsl:with-param name="isControl" select="0"/>
			</xsl:call-template>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:if test="@groupName">
				<xsl:attribute name="data-wc-group">
					<xsl:value-of select="@groupName"/>
				</xsl:attribute>
			</xsl:if>
			<div role="tablist">
				<xsl:choose>
					<xsl:when test="@type eq 'accordion'">
						<xsl:attribute name="aria-multiselectable">
							<xsl:choose>
								<xsl:when test="@single">false</xsl:when>
								<xsl:otherwise>true</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="@type eq 'left' or @type eq 'right'">
						<xsl:attribute name="aria-orientation">
							<xsl:text>vertical</xsl:text>
						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
				<xsl:apply-templates select="ui:tab">
					<xsl:with-param name="numAvailTabs" select="count(ui:tab[@open and not(@disabled)])"/>
				</xsl:apply-templates>
			</div>
			<xsl:if test="not(@type eq 'accordion')">
				<xsl:apply-templates select="ui:tab" mode="content"/>
			</xsl:if>
		</div>
	</xsl:template>
</xsl:stylesheet>
