<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.ui.tabset.n.WTabsetClass.xsl"/>
	<xsl:import href="wc.ui.tabset.n.doTabList.xsl"/>
	<xsl:import href="wc.ui.tabset.n.tabsAfterContent.xsl"/>
	<!--
		This template builds the basic tabset. The tabset is a wrapper container. It
		has a list of tabs and content. The order of these is dependent upon the tabset
		type.
	-->
	<xsl:template match="ui:tabset">
		<xsl:variable name="firstOpenTab" select="(ui:tab[@open=$t]|ui:tabgroup/ui:tab[@open=$t])[1]"/>
		
		<xsl:variable name="tabsAfterContent">
			<xsl:call-template name="tabsAfterContent"/>
		</xsl:variable>

		<div id="{@id}">
			<xsl:attribute name="class">
				<xsl:call-template name="WTabsetClass"/>
			</xsl:attribute>

			<xsl:call-template name="disabledElement"/>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:call-template name="ajaxTarget"/>
			
			<xsl:apply-templates select="ui:margin"/>
			
			
			<xsl:if test="$tabsAfterContent!=1">
				<xsl:call-template name="doTabList">
					<xsl:with-param name="firstOpenTab" select="$firstOpenTab"/>
				</xsl:call-template>
			</xsl:if>
			
			<xsl:if test="not(@type='accordion')">
				<xsl:element name="div">
					<xsl:attribute name="role">
						<xsl:text>presentation</xsl:text>
					</xsl:attribute>
					<xsl:apply-templates select="ui:tab|ui:tabgroup/ui:tab" mode="content">
						<xsl:with-param name="tabset" select="."/>
						<xsl:with-param name="tabsetId" select="@id"/>
						<xsl:with-param name="type" select="@type"/>
						<xsl:with-param name="firstOpenTab" select="$firstOpenTab"/>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
			<xsl:if test="$tabsAfterContent=1">
				<xsl:call-template name="doTabList">
					<xsl:with-param name="firstOpenTab" select="$firstOpenTab"/>
				</xsl:call-template>
			</xsl:if>
		</div>
	</xsl:template>
</xsl:stylesheet>
