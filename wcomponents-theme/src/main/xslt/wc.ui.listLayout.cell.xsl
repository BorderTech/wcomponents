<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		This template creates the HTML LI elements and applies the content.  If
		there is no content the cell is omitted.
	-->
	<xsl:template match="ui:cell" mode="ll">
		<xsl:param name="hgap"/>
		<xsl:param name="vgap"/>
		<xsl:if test="node()">
			<xsl:element name="li">
				<!--
					A weakness in IE's (8 and earlier) CSS support prevents us from doing row striping
					based on the position of the list item in the list. We therefore have to apply
					a class to every second list item if the listLayout type is striped.
				-->
				<xsl:if test="../@type='striped' and position() mod 2 = 0">
					<xsl:attribute name="class">
						<xsl:text> wc_iestripe</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:variable name="gapStyle">
					<xsl:if test="position() &gt; 1">
						<xsl:choose>
							<xsl:when test="../@type='flat' and $hgap !=0">
									<xsl:text>margin-left:</xsl:text>
								<xsl:value-of select="$hgap"/>
								<xsl:text>;</xsl:text>
							</xsl:when>
							<xsl:when test="$vgap !=0">
									<xsl:text>margin-top:</xsl:text>
								<xsl:value-of select="$vgap"/>
								<xsl:text>;</xsl:text>
							</xsl:when>
						</xsl:choose>
					</xsl:if>
				</xsl:variable>
				<xsl:if test="$gapStyle!=''">
					<xsl:attribute name="style">
						<xsl:value-of select="$gapStyle"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
