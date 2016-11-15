<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.collapsibleToggle.xsl"/>
	<!--
		ui:rowexpansion controls the mode of the expandable rows and whether the expand/collapse all controls are 
		visible. This template outputs those controls. It is called explicitly from the template name `topControls`.

		Structural: do not override.
	-->
	<xsl:template match="ui:rowexpansion">
		<xsl:variable name="tableId" select="../@id"/>
		<!--
			NOTE: the guard code testing for the existance of collapsible rows in this template is a belt-and-braces fix
			for slack front end developers. We have had genuine cases where applications have been built with 
			ui:rowexpansion with @expandAll set to show the collapse/expand controls but with no collapsible rows in
			the table and then bugs raised that the expand/collapse all controls don't seem to do anything!
		 -->
		<xsl:if test="..//ui:subtr[ancestor::ui:table[1]/@id eq $tableId]">
			<xsl:call-template name="collapsibleToggle">
				<xsl:with-param name="id">
					<xsl:value-of select="concat($tableId, '_texall')"/>
				</xsl:with-param>
				<xsl:with-param name="for">
					<xsl:value-of select="$tableId"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
