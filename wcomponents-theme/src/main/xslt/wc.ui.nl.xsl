<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		creates a newline character.
		 
		NOTE: The allowed line separators (http://dev.w3.org/html5/markup/terminology.html)
		are:
		* a U+000D CARRIAGE RETURN (CR) character
		* a U+000A LINE FEED (LF) character
		* a U+000D CARRIAGE RETURN (CR) followed by a U+000A LINE FEED (LF) character
		
		However:
		* Using &#xD; by itself does not work in Chrome (on Windows at least);
		* Using &#xA; by itself does not work in IE, 
		* Using &#xD;&#xA; does work in IE, Firefox (3.6+), Chrome (at least 19+
		but maybe earlier), Opera (at least 11.61+ but maybe earlier) and Safari
		(Windows 5.0.1+, maybe earlier) but should be tested on non-windows platforms. 
	 -->
	<xsl:template match="ui:nl">
		<xsl:text>&#xD;&#xA;</xsl:text>
	</xsl:template>
</xsl:stylesheet>
