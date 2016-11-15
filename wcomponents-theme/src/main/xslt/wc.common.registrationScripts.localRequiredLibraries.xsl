<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Template for adding implementation specific AMD/require.js requires. It is for
		ease of implementation override and is empty by default.

		The output is a list of comma separated strings where each string is that which
		is used as the module name in define().

		NOTE: Include the terminal comma as it is stripped out when building the list
		of requires.
	-->
	<xsl:template name="localRequiredLibraries"/>
</xsl:stylesheet>
