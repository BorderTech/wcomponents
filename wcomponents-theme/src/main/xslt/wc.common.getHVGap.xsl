<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.getSpace.vars.xsl"/><!-- contains the gap limit definitions -->

	<!--
		Template to convert an integer (pixel) hgap or vgap to a class attribute value. If this template returns 
		anything it must return a string starting with a SPACE character as it will be used in building a class
		attribute.
		
		param gap: should only be set if the calling template is from an element which is not using its own @hgap/@vgap.
		param isVGap: ust be set to something other than 0 to set a vgap class value. If setting hgap then do not 
		include this param.
	-->
	<xsl:template name="getHVGapClass">
		<xsl:param name="gap" select="-1"/>
		<xsl:param name="isVGap" select="0"/>
		
		<xsl:variable name="mygap">
			<xsl:choose>
				<xsl:when test="$gap and number($gap) ge 0">
					<xsl:value-of select="$gap"/>
				</xsl:when>
				<xsl:when test="@gap">
					<xsl:value-of select="@gap"/>
				</xsl:when>
				<xsl:when test="$isVGap and number($isVGap) eq 1 and @vgap">
					<xsl:value-of select="@vgap"/>
				</xsl:when>
				<xsl:when test="$isVGap and number($isVGap) eq 1">
					<xsl:value-of select="-1"/>
				</xsl:when>
				<xsl:when test="@hgap">
					<xsl:value-of select="@hgap"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="-1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:if test="$mygap and number($mygap) gt 0">
			<xsl:text> wc-</xsl:text><!-- leading space is important -->
			<xsl:choose>
				<xsl:when test="number($isVGap) eq 1">
					<xsl:text>v</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>h</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>gap-</xsl:text>
			<xsl:call-template name="getSizeClassExtension">
				<xsl:with-param name="gap" select="number($mygap)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
