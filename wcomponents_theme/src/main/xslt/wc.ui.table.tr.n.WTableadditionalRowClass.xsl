<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<!--
		Called from the transform for ui:tr. The call passes parameters for
		certain calculations so that they do not have to be re-done in the
		helper.
		param myTable: the nearest ancestor ui:table element
		param parentIsClosed: Integer: 1 if the row is in a closed sub-row otherwise 0.
		param topRowIsStriped: Integer: 1 if the top level row (ir this row if it is a top-level row) is striped otherwise 0.
		param removeRow: Integer 1 if the current row is hidden otherwise 0.
	-->
	<xsl:template name="WTableAdditionalRowClass"/>
</xsl:stylesheet>