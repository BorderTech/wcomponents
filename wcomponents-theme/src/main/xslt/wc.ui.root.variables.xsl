<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		README

		These are 'global' variables but are applicable only in the wc.ui.root.* templates.
		You probably do not want to over-ride these. In the cases where over-rides
		may be necessary or desirable you will find ANT properties which can be
		more easily manipulated on a theme-by-theme basis.
	-->
	
	<!--
		Debug flag. This is a global parameter as it is pulled out of the compressed XSLT and we do not want it renamed.
	-->
	<xsl:param name="isDebug" select="1"/>

	<!--
		Used to calculate the path to the libs based on the stylesheet processing instruction stripped is used in the 
		config object. Param so it can be overridden if necessary.
	-->
	<xsl:param name="xslPath" select="substring-before(replace(substring-after(//processing-instruction('xml-stylesheet'), 'href=&quot;'), '&amp;amp;', '&amp;'), '&quot;')"/>

	<xsl:variable name="scriptDir">
		<xsl:choose>
			<xsl:when test="number($isDebug) eq 1">
				<xsl:text>${script.debug.target.dir.name}</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>${script.target.dir.name}</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<!--
		this is the absolute or server relative path to the resources used to build the site calculated from the XSLT 
		processing instruction.
	-->
	<xsl:variable name="resourceRoot">
		<xsl:value-of select="substring-before($xslPath, '${xslt.target.dir.name}')"/>
	</xsl:variable>

	<!--
		This string is used to build a query string on all resources requested as part of a page.
	-->
	<xsl:variable name="cacheBuster">
		<xsl:value-of select="substring-after($xslPath, '?')"/>
	</xsl:variable>
</xsl:stylesheet>
