<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Placeholder template.
		Used to enable an implementation to add a style loader config without having to override makeRequireConfig.

		IMPORTANT:
		Your CONFIG object is already created, you just need to add your module config properties for
		* "ie":[array] and/or
		* "css":"object"
		BUT if you do your output MUST start with a comma.

		Example
		To include custom CSS for IE11 (using *.ie11.css) and IE10 (*.ie10.css) and for all versions of Firefox (*.ff.css)
		and for Safari 8 (*.saf8.css)  you would have a config like:

		<xsl:text>,"ie":["ie11","ie10"],"css":{"ff":"ff,"saf8":{"test":"safari","version":8}}</xsl:text>

		**** NOTE: your config MUST start with a comma. ****
	-->
	<xsl:template name="styleLoaderConfig"/>
</xsl:stylesheet>
