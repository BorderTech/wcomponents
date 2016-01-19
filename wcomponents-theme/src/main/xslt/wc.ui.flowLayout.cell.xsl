<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
<!--
 In order to apply flow styles to each cell in a consistent manner we wrap the
 cell content in a div element. This is then able to be styled independently
 of the actual content.
 
 param align: the FlowLayout align property, passed through to save continual
 lookups to the cell's parent.
 
param hgap: the horizontal space between cells in a horizontal flow (if any) .
 
param vgap: the vertical space between cells in a vertical flow (if any).
-->
	<xsl:template match="ui:cell" mode="fl">
		<xsl:param name="align"/>
		<xsl:param name="hgap"/>
		<xsl:param name="vgap"/>
		<xsl:variable name="style">
			<xsl:if test="position() &gt; 1">
				<xsl:choose>
					<xsl:when test="$align = 'vertical'">
						<xsl:if test="$vgap !=0">
							<xsl:text>margin-top:</xsl:text>
							<xsl:value-of select="$vgap"/>
							<xsl:text>;</xsl:text>
						</xsl:if>
					</xsl:when>
					<xsl:when test="$hgap != 0">
						<xsl:text>margin-left:</xsl:text>
						<xsl:value-of select="$hgap"/>
						<xsl:text>;</xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:if>
		</xsl:variable>
		<div class="{local-name(.)}">
			<xsl:if test="$style != ''">
				<xsl:attribute name="style">
					<xsl:value-of select="$style"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
</xsl:stylesheet>
