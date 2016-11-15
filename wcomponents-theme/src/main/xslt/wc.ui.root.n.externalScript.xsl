<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:include href="wc.ui.root.variables.xsl"/>
	<!--
		Template to insert a script element that loads an external javascript file.
		Called from
			wc.ui.root.n.includeJs.xsl
			wc.ui.root.n.makeRequireConfig.xsl

		param scriptName: The raw script name without extension
	-->
	<xsl:template name="externalScript">
		<xsl:param name="scriptName"/><!-- The name of the script without ${debug.target.file.name.suffix} or .js -->
		<script type="text/javascript" src="{concat($resourceRoot, $scriptDir, '/', $scriptName, '.js?', $cacheBuster)}"></script>
	</xsl:template>
</xsl:stylesheet>
