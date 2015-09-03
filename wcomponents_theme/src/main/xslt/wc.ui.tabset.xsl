<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.ui.tabset.n.tabsetAdditionalClass.xsl"/>
	<xsl:import href="wc.ui.tabset.n.doTabList.xsl"/>
	<xsl:import href="wc.ui.tabset.n.tabsAfterContent.xsl"/>
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:import href="wc.debug.common.bestPracticeHelpers.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<!--
		This template builds the basic tabset. The tabset is a wrapper container. It
		has a list of tabs and content. The order of these is dependent upon the tabset
		type.
	-->
	<xsl:template match="ui:tabset">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="type" select="@type"/>
		<xsl:variable name="firstOpenTab" select="(ui:tab[@open=$t]|ui:tabGroup/ui:tab[@open=$t])[1]"/>

		<xsl:element name="div">
			<xsl:attribute name="id">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:if test="$isDebug=1">
				<xsl:call-template name="debugAttributes"/>
				<xsl:call-template name="thisIsNotAllowedHere-debug">
					<xsl:with-param name="testForNoInteractive" select="1"/>
					<xsl:with-param name="testForPhraseOnly" select="1"/>
				</xsl:call-template>
			</xsl:if>

			<xsl:attribute name="class">
				<xsl:value-of select="$type"/>
				<xsl:text> tabset</xsl:text>
				<xsl:if test="$type='left' or $type='right'">
					<xsl:text> wc_tab_lr</xsl:text><!-- convenience class to reduce CSS -->
				</xsl:if>
				<xsl:call-template name="tabsetAdditionalClass"/>
			</xsl:attribute>

			<xsl:call-template name="disabledElement"/>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:call-template name="ajaxTarget"/>
			<xsl:apply-templates select="ui:margin"/>
			<xsl:variable name="tabsAfterContent">
				<xsl:call-template name="tabsAfterContent"/>
			</xsl:variable>
			<xsl:if test="$tabsAfterContent!=1">
				<xsl:call-template name="doTabList">
					<xsl:with-param name="firstOpenTab" select="$firstOpenTab"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="not($type='accordion')">
				<xsl:element name="div">
					<xsl:attribute name="role">
						<xsl:text>presentation</xsl:text>
					</xsl:attribute>
					<xsl:apply-templates select="ui:tab|ui:tabGroup/ui:tab" mode="content">
						<xsl:with-param name="tabset" select="."/>
						<xsl:with-param name="tabsetId" select="$id"/>
						<xsl:with-param name="type" select="$type"/>
						<xsl:with-param name="firstOpenTab" select="$firstOpenTab"/>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
			<xsl:if test="$tabsAfterContent=1">
				<xsl:call-template name="doTabList">
					<xsl:with-param name="firstOpenTab" select="$firstOpenTab"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>