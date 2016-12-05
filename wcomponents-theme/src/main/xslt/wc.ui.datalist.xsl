<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		This is a transform for the output of WDataListServlet. This component is
		designed to allow AJAX loading of HTML SELECT elements using a HTML5 DATALIST.
		Obviously as HTML5 support improved the datalist will be able to be used for
		other purposes.

		This component is a workaround for capacity issues found in IE. At this 
		stage this transform creates a nested SELECT element in the DATALIST and
		applies the options to that since IE cannot recognise OPTION elements unless
		they are in a select.
	
		child elements: ui:option
	-->
	<xsl:template match="ui:datalist">
		<select>
			<xsl:apply-templates select="ui:option" mode="selectableList"/>
		</select>
	</xsl:template>
</xsl:stylesheet>
