<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.ui.root.n.replaceString.xsl"/>

	<!--
		NOTE: this template was created to move the nested variables from the
		variable "xslPath" because it causes problems with libxsl's XSLTProc
		which *really* doesn't like nested variables.
	-->
	<xsl:template name="getXslPath">
		<xsl:variable name="field">
			<xsl:value-of select="//processing-instruction('xml-stylesheet')"/>
		</xsl:variable>
		<xsl:variable name="stripStartQuote">
			<xsl:value-of select="substring-after($field, 'href=&quot;')"/>
		</xsl:variable>
		<xsl:variable name="qsunescaped">
			<xsl:call-template name="replaceString">
				<xsl:with-param name="text" select="$stripStartQuote"/>
				<!-- This looks odd but is right! Leave it alone! -->
				<xsl:with-param name="replace" select="'&amp;amp;'"/>
				<xsl:with-param name="with" select="'&amp;'"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:value-of select="substring-before($qsunescaped, '&quot;')"/>
	</xsl:template>
</xsl:stylesheet>
