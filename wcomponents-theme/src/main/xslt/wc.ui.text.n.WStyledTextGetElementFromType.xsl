<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	
	<!--
		Determines the HTML element appropriate for any give WStyledText based on
		the tyoe attribute of ui:text.
	-->
	<xsl:template name="WStyledTextGetElementFromType">
		<xsl:param name="type"/>
		<xsl:choose>
			<xsl:when test="$type='emphasised' or $type='highPriority'">
				<xsl:text>strong</xsl:text>
			</xsl:when>
			<xsl:when test="$type='mediumPriority'">
				<xsl:text>em</xsl:text>
			</xsl:when>
			<xsl:when test="$type='insert'">
				<xsl:text>ins</xsl:text>
			</xsl:when>
			<xsl:when test="$type='delete'">
				<xsl:text>del</xsl:text>
			</xsl:when>
			<xsl:when test="self::ui:text or ($type!='' and $type !='plain')">
				<xsl:text>span</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
