<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		This template creates each row and the first column in the row. It then applies
		templates selecting the following-sibling::ui:cell[position &lt; $cols] to 
		build the rest of the columns in the row. The params to this template are
		for convenience and speed since they could all be derived but it is better to
		only calculate them once.
 
		param align: the column align property
		param width: the width of the first column
		param hgap: the horizontal space (if any) between columns.
		param vgap: the vertical space (if any) between rows.
		param cols: the number of columns in each row in the layout.
	-->
	<xsl:template match="ui:cell" mode="clRow">
		<xsl:param name="align"/>
		<xsl:param name="width"/>
		<xsl:param name="hgap"/> 
		<xsl:param name="vgap"/>
		<xsl:param name="cols"/>
		<div class="{local-name(.)} wc_row">
			<xsl:if test="position() &gt; 1 and $vgap !=0">
				<xsl:attribute name="style">
					<xsl:text>margin-top:</xsl:text>
					<xsl:value-of select="$vgap"/>
					<xsl:text>;</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<div>
				<xsl:attribute name="class">
					<xsl:choose>
						<xsl:when test="$align and $align!=''">
							<xsl:value-of select="$align"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>${wc.common.align.std}</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:if test="$width != 0 or $hgap != 0">
					<xsl:attribute name="style">
						<xsl:if test="$width != 0">
							<xsl:value-of select="concat('width:', $width, '%;')"/>
						</xsl:if>
						<xsl:if test="$hgap != 0">
							<xsl:value-of select="concat('padding-right:', $hgap, ';')"/>
						</xsl:if>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates/>
			</div>
			<xsl:if test="$cols &gt; 1">
				<xsl:apply-templates select="following-sibling::ui:cell[position() &lt; $cols]" mode="clInRow">
					<xsl:with-param name="hgap" select="$hgap"/>
				</xsl:apply-templates>
			</xsl:if>
		</div>
	</xsl:template>
</xsl:stylesheet>
