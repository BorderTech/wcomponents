<?xml version="1.0"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:x="https://github.com/bordertech/wcomponents/namespace/ui/dummy"
	xmlns:doc="http://www.oxygenxml.com/ns/doc/xsl"
	version="2.0">
<!--**
build_strip_debug.xsl

 Transform to remove debug mode specific XSLT. Called as part of the compression
 stage after the JAVA based XSLT compression.

* output

 We are outputting XSLT which is XML. Leave indent as "no" because we are transforming
 the XSLT which will be compressed.
-->
	<xsl:output method="xml" indent="no" omit-xml-declaration="no"/>
	<xsl:strip-space elements="*"/>
<!--**
* Template match="@*|node()"

 Generic match template for all unmatches nodes and attributes. Copies the input
 to the output.
-->
	<xsl:template match="@*|node()|processing-instruction()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()|processing-instruction()"/>
		</xsl:copy>
	</xsl:template>

<!--**
* Template match="xsl:template[@mode='debug' or contains(@mode, '-debug') or contains(@name, '-debug')]|xsl:param[@name='isDebug']|xsl:include[@href='${xslt.target.debug.file.name}']|*[contains(@test,'$isDebug')]"

 This template will remove:
    
    * any template with a mode of 'debug' or a mode which contains '-debug' or a name 
    which includes '-debug'; 
    
    * the 'isDebug' xsl:param;
    
    * the xsl:include for the debugInfo xslt; and
    
    * elements with a test attribute containing '$isDebug' (in practice these are 
      all currently <xsl:if test="$isDebug=1"> but the more generic test should
      cover xsl:when tests (see template for xsl:choose with xsl:when with 
      $isDebug test below).
	
-->
	<xsl:template match="xsl:template[@mode='debug' or contains(@mode, '-debug') or contains(@name, '-debug')]|xsl:param[@name='isDebug']|xsl:include[@href='${xslt.target.debug.file.name}']|*[contains(@test,'$isDebug')]"/>

<!--**
* Template match="xsl:choose[xsl:when[contains(@test,'$isDebug')]]"

 When we have a choose which contains a test for isDebug=1 as a when then we have
 to determine if there are other <xsl:when>s because if their aren't we have to
 convert the otherwise (if any) and remove the choose. NOTE: this tansform also
 allows for the dumb situation where we have an xsl:choose which contains only a
 single xsl:when and no xsl:otherwise and that xsl:when is a debug test. These
 should never exist but get transformed to nothing.
-->
	<xsl:template match="xsl:choose[xsl:when[contains(@test,'$isDebug')]]">
		<xsl:choose>
			<xsl:when test="count(xsl:when) &gt; 1">
				<xsl:copy>
					<xsl:apply-templates select="@*|node()"/>
				</xsl:copy>
			</xsl:when>
			<xsl:when test="xsl:otherwise">
				<xsl:apply-templates select="xsl:otherwise" mode="otherwiseToCertainty"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

<!--**
* Template match="xsl:otherwise" mode="otherwiseToCertainty"

 If an xsl:choose contains a single xsl:when which is a debug test then the
 xsl:otherwise becaomes a certainty when debug is off. There is really no excuse
 for this and if you find yourself writing these sort of choices please stop.
-->
	<xsl:template match="xsl:otherwise" mode="otherwiseToCertainty">
		<xsl:apply-templates/>
	</xsl:template>

<!--
 NOTE: experimental template. This is pretty safe given the current state of
 WComponents XSLT but is not guaranteed to stay that way. It DOES give a pretty
 big reduction in the size of the alingual XSLT (approx 5.8% of space stripped
 xslt using our largest current implementation). The predicate is just for my
 neurosis and without it appears to work fine but does replace character
 entities with their value in the charset. The marginal reduction in output is
 pretty much negligible (0.0003% of the largest implementation). So I say keep
 the predicate to be safe!.
-->
	<xsl:template match="xsl:text[not(contains(text(),'&amp;'))]">
		<xsl:value-of select="."/>
	</xsl:template>
	
<!-- 
	Remove everything in the doc namespace (XSLT documentation)
-->
	<xsl:template match="doc:*" />
</xsl:stylesheet>