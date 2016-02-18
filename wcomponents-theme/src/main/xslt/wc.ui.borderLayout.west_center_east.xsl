<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.borderLayout.n.borderLayoutCell.xsl"/>
	<!--
	
		The transform for west, center and east elements within a ui:borderlayout.
		
		The space between the cells is set by the parent ui:borderlayout's hgap
		attribute. If set this is set half as a left margin on all but the leftmost 
		(in ltr	languages) cell and half as a right margin on all but the rightmost.
	-->
	<xsl:template match="ui:east|ui:west|ui:center">
		<xsl:param name="hgap" select="0"/>
		<xsl:call-template name="borderLayoutCell">
			<xsl:with-param name="hgap">
				<xsl:choose>
					<xsl:when test="$hgap = 0 or count(../ui:west|../ui:center|../ui:east) &gt; 1">
						<xsl:value-of select="$hgap"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
