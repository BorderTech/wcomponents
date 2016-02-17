<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Helper template to create each cell in a gridLayout.
		
		param width: The width of the cells in percent.
		 
		param hgap: The horizontal space (if any) between each cell in a row.
		
		param vgap: The vertical space (if any) between each row (only passed in when the 'grid' has only one column).
		 
		param inRow: If the cell is not a row start cell then this is 1. For all cells apart from the rows start the 
		hgap is applied to padding-left as well as padding-right. This makes the cells closer to equal size than would 
		be the case if the gap was placed only to the left of each cell after the first.
	-->
	<xsl:template name="gridCell">
		<xsl:param name="width" />
		<xsl:param name="hgap" select="0"/>
		<xsl:param name="vgap" select="0"/>
		<xsl:param name="inRow"/>
		<xsl:variable name="style">
			<xsl:if test="$width !=''">
				<xsl:value-of select="concat('width:',$width,';')"/>
			</xsl:if>
			<xsl:if test="$vgap != 0">
				<xsl:value-of select="concat('padding-top:',$vgap,';')"/>
			</xsl:if>
			<xsl:if test="$hgap != 0">
				<xsl:if test="position() != last() or $inRow!=1">
					<xsl:value-of select="concat('padding-right:',$hgap,';')" />
				</xsl:if>
				<xsl:if test="$inRow = 1">
					<xsl:value-of select="concat('padding-left:',$hgap,';')" />
				</xsl:if>
			</xsl:if>
		</xsl:variable>
		<div class="{local-name(.)} wc-column">
			<xsl:if test="$style!=''">
				<xsl:attribute name="style">
					<xsl:value-of select="$style"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
</xsl:stylesheet>
