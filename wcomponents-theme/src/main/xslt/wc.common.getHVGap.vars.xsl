<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	
	<!-- 
		Variables for getHVGap.
		Split out here to ease implementation overrides.
	-->

	<!-- an arbitrary number which defines the upper limit of when a gap is 'small'. 4px is ~ 0.25rem. -->
	<xsl:variable name="smallgap" select="4"/>
	<!-- an arbitrary number which defines the upper limit of when a gap is 'medium'. 8px is ~ 0.5rem. -->
	<xsl:variable name="medgap" select="8"/>
	<!-- an arbitrary number which defines the upper limit of when a gap is 'large'. 16px is ~ 1rem. -->
	<xsl:variable name="lggap" select="16"/>
	<!-- Any gap larger than $lggap is 'extra large' -->
</xsl:stylesheet>
