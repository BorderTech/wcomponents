<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.getHVGap.vars.xsl"/><!-- contains the gap limit definitions -->

	<!--
		Template to convert an integer (pixel) hgap or vgap to a class attribute value. If this template returns 
		anything it must return a string starting with a SPACE character as it will be used in building a class
		attribute.
		
		param gap: should only be set if the calling template is from an element which is not using its own @hgap/@vgap.
		param isVGap: ust be set to something other than 0 to set a vgap class value. If setting hgap then do not 
		include this param.
	-->
	<xsl:template name="getHVGapClass">
		<xsl:param name="gap"/>
		<xsl:param name="isVGap" select="0"/>
		<xsl:variable name="mygap">
			<xsl:choose>
				<xsl:when test="$gap != ''">
					<xsl:value-of select="$gap"/>
				</xsl:when>
				<xsl:when test="$isVGap = 0">
					<xsl:value-of select="@hgap"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@vgap"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:if test="$mygap and $mygap != '' and $mygap != '0'">
			<xsl:text> wc_</xsl:text>
			<xsl:choose>
				<xsl:when test="$isVGap = 0">
					<xsl:text>h</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>v</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>gap_</xsl:text>
			<xsl:choose>
				<xsl:when test="$mygap &lt;= $smallgap">
					<xsl:text>sm</xsl:text>
				</xsl:when>
				<xsl:when test="$mygap &lt;= $medgap">
					<xsl:text>med</xsl:text>
				</xsl:when>
				<xsl:when test="$mygap &lt;= $lggap">
					<xsl:text>lg</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>xl</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- 
		The old getHVGap template for pixel specific gaps.

		This horrible little template is a helper for layout components hgap and vgap attributes. It has been 
		through a number of iterations. This is simple but ugly as it forces the gaps to be px.

		This has been removed as part of fixing https://github.com/BorderTech/wcomponents/issues/571 which also 
		addressed the most salient points of https://github.com/BorderTech/wcomponents/issues/179. It is here just in
		case someone ever wants to re-implement pixel based hgap/vgap. Doing so is ****DISCOURAGED****.

	<xsl:template name="getHVGap">
		<xsl:param name="gap" select="@hgap"/>
		<xsl:param name="divisor" select="1"/>
		<xsl:choose>
			<xsl:when test="$gap">
				<xsl:variable name="px" select="format-number($gap,'0')"/>
				<xsl:value-of select="$px div $divisor"/>
				<xsl:text>px</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:number value="0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	-->
</xsl:stylesheet>
