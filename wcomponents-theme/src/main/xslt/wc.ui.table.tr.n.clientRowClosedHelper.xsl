<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:template name="clientRowClosedHelper">
		<xsl:param name="myTable"/>
		<xsl:variable name="clientPaginationRows">
			<xsl:choose>
				<xsl:when test="$myTable/ui:pagination/@rowsPerPage">
					<xsl:value-of select="$myTable/ui:pagination/@rowsPerPage"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$myTable/ui:pagination/@rows"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="tableCurrentPage" select="$myTable/ui:pagination/@currentPage"/>
		<xsl:variable name="myPosition" select="count(preceding-sibling::ui:tr) + 1"/>
		<xsl:variable name="activeStart" select="($clientPaginationRows * $tableCurrentPage) + 1"/>
		<xsl:choose>
			<xsl:when test="(($myPosition &lt; $activeStart) or ($myPosition >= ($activeStart + $clientPaginationRows)))">
				<xsl:value-of select="1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
