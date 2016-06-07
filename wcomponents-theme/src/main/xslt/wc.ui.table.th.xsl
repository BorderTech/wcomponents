<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.n.className.xsl" />
	<!--
		Transform of a ui:th element within a ui:tr. This is a row header and is a 1:1 map with a HTML th element.
		
		For th inside the thead see wc.ui.table.th_thead.xsl
	-->
	<xsl:template match="ui:th">
		<xsl:param name="myTable"/>
		<xsl:param name="hasRole" select="0"/>

		<xsl:variable name="tableId" select="$myTable/@id"/>
		<xsl:variable name="myHeader" select="$myTable/ui:thead/ui:th[1]"/>

		<th id="{concat($tableId,'_trh',../@rowIndex)}" scope="row">
			<xsl:if test="$hasRole &gt; 0">
				<xsl:attribute name="role">
					<xsl:text>rowheader</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$myHeader">
				<xsl:attribute name="headers">
					<xsl:value-of select="concat($tableId,'_thh','1')"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="$myHeader">
						<xsl:value-of select="$myHeader/@align"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates />
		</th>
	</xsl:template>
</xsl:stylesheet>
