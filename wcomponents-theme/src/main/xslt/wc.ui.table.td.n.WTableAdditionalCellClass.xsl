<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		 Allow implementations to add classe name(s) to tds. Called from the transform for
		 ui:td. The call passes through the following parameters:
	
	    [myTable] The nearest ancestor ui:table element.
	
	    [tbleColPos] The position of the column to which the cell belongs
	
	    [rowHeaderElement] The ui:th element in the cell's row (if any)
	
	    [alignedCol] the value of the column alignment as determined from the investigation
		of the cell's column header's align property.
	-->
	<xsl:template name="WTableAdditionalCellClass"/>
</xsl:stylesheet>
