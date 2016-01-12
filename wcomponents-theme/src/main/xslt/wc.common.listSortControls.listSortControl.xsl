<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<!--
		Outputs each shuffle control as a HTML BUTTON element.
		
		param id: The id of the SELECT being controlled. This is not necessarily the id
		of the component which owns the sort controls.
		param value: The value of the button "top", "bottom", "up", "down"
		param toolTip: The text used to populate the button title.
	-->
	<xsl:template name="listSortControl">
		<xsl:param name="id"/>
		<xsl:param name="value"/>
		<xsl:param name="toolTip"/>
		<button class="wc_sorter wc_ibtn" type="button" value="{$value}" aria-controls="{$id}" title="{$toolTip}">
			<xsl:call-template name="disabledElement">
				<xsl:with-param name="isControl" select="1"/>
			</xsl:call-template>
		</button>
	</xsl:template>
</xsl:stylesheet>
