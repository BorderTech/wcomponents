<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Table Actions
	-->
	<xsl:template match="ui:actions">
		<xsl:apply-templates select="ui:action"/>
	</xsl:template>
	
	<xsl:template match="ui:action">
		<xsl:apply-templates select="ui:button"/>
	</xsl:template>
	<!--
		Guard wrapper for action conditions. these are not output in place but are part
		of the sibling button's attribute data. The ui:action part of the match is to 
		differentiate from ui:subordinate's ui:condition.
	-->
	<xsl:template match="ui:action/ui:condition"/>
	<!--
		Outputs a comma spearated list of JSON objects stored in a button
		attribute which is used to determine whether the action's conditions are met
		before undertaking the action.
	-->
	<xsl:template match="ui:condition" mode="action">
		<xsl:text>{"min":"</xsl:text>
		<xsl:value-of select="@minSelectedRows"/>
		<xsl:text>","max":"</xsl:text>
		<xsl:value-of select="@maxSelectedRows"/>
		<xsl:text>"}</xsl:text>
		<xsl:if test="position()!=last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
