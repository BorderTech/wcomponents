<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
<!--
 Helper template used to output values for the class attribute on the element 
 with a role of tab. This is a null template in the core but is here for easy 
 overriding in implementations.
 
 Called from transform for ui:tab. The parameter firstOpenTab is passed in from 
 the call. This is the element which is the first tab which is 'open' in the
 tabset.
-->
	<xsl:template name="tabClass"/>
</xsl:stylesheet>
