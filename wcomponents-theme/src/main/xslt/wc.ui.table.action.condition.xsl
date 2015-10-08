<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Guard wrapper for action conditions. these are not output in place but are part
		of the sibling button's attribute data.
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
		<xsl:text>","type":"</xsl:text>
		<xsl:value-of select="@type"/>
		<xsl:text>","message":"</xsl:text>
		<xsl:value-of select="@message"/>
		<xsl:text>"}</xsl:text>
		<xsl:if test="position()!=last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
