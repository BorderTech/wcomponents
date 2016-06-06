<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
				xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	
	<!--
		Web Analytics
	-->
	<xsl:template name="webAnalytics"/>
	
	<!--
		This is a sample implementation which wires up Google Analytics based on an analytics clientId.
		For this example we assume the WComponents clientId is set to a Google Analytics property ID. We do not test
		this.

		Alternative:
		To use analytics per view (though why?) add a script element to the WApplication using addJsFile where your
		JsFile has something like the content of this template's script element (supply your own ID).

		Sensible alternative:
		Do not use this XSLT at all but add the Google (or whichever analytics tool you use) code to a JS file and
		use WApplication's setJsFile or set JsUrl methods.
	<xsl:template name="webAnalytics">
		<xsl:if test="ui:application/ui:analytic[@clientId]">
			<xsl:variable name="cid" select="ui:application/ui:analytic/@clientId"/>
			<xsl:if test="$cid and $cid != ''">
				<xsl:comment>start Google analytics</xsl:comment>
				<script type="text/javascript">
					<xsl:text>require(["wc/compat/compat!"], function(){
						(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)})(window,document,'script','https://www.google-analytics.com/analytics.js','ga');
ga('create', '</xsl:text>
				<xsl:value-of select="$cid"/>
				<xsl:text>', 'auto');
ga('send', 'pageview');
						});</xsl:text>
				</script>
				<xsl:comment>end Google analytics</xsl:comment>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	-->
</xsl:stylesheet>
