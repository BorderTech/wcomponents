<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		HTML Conformance
	-->

	<!--
		Elements to be hoisted to the HEAD element:

		Remove link, base and meta elements completely from flow. These are all hoisted into the HTML head element in
		ui:root.
	-->
	<xsl:template match="html:link|html:base|html:meta"/>


	<!--
		html:link can appear in a ui:ajaxtarget and in this case cannot be moved to a HEAD element so we just output it 
		in-situ.
	-->
	<xsl:template match="html:link[ancestor::ui:ajaxtarget]">
		<xsl:copy-of select="."/>
	</xsl:template>
	
	<!-- Copy without XML namespaces. Also prevents double out-put of HML self-closing elements like `br` and `hr` -->
	<xsl:template name="copyHtml">
		<xsl:element name="{local-name(.)}">
			<xsl:apply-templates select="@*"/>
		</xsl:element>
	</xsl:template>

	<!--
		Copy link, base and meta elements in the head.
	-->
	<xsl:template match="html:link|html:base|html:meta" mode="inHead">
		<xsl:call-template name="copyHtml"/>
	</xsl:template>

	<xsl:template match="html:input|html:img|html:br|html:hr">
		<xsl:call-template name="copyHtml"/>
	</xsl:template>

	<!--
		Templates for non-conforming HTML elements and attributes
		see https://html.spec.whatwg.org/multipage/obsolete.html#non-conforming-features.
	-->
	<xsl:template match="html:acronym|html:applet|html:bgsound|html:dir|html:frame|html:frameset|html:noframes|html:isindex|html:listing|html:nextid|html:noembed|html:plaintext|html:rb|html:strike|html:xmp|html:basefont|html:big|html:blink|html:center|html:font|html:marquee|html:multicol|html:nobr|html:spacer|html:tt"/>

	<!--
		Conforming but problematic elements

		We can change some elements with known or suspect accessibilty issues:
			b to strong
			i to em
		You probably don't need this template.

	<xsl:template match="html:b|html:i">
		<xsl:variable name="elementName">
			<xsl:choose>
				<xsl:when test="self::html:b">strong</xsl:when>
				<xsl:when test="self::html:i">em</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$elementName}">
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	-->


	<!--
		Do not put a HTML form inside any ui:application.
	-->
	<xsl:template match="html:form" />
</xsl:stylesheet>
