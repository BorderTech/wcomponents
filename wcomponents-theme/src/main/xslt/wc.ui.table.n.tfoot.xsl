<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Creates a tfoot element to hold actions and pagination controls. Called
		from the transform for ui:table.

		You probably do not need to override this.

		See transform for ui:table in wc.ui.table.xsl for information about the parameters.
	-->
	<xsl:template name="tfoot">
		<xsl:param name="addCols" select="0"/>

		<xsl:variable name="showPagination">
			<xsl:choose>
				<xsl:when test="ui:pagination and not(ui:pagination/@controls='top')">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="ui:actions or $showPagination=1">
			<xsl:variable name="numCols">
				<xsl:choose>
					<xsl:when test="ui:thead/ui:th">
						<xsl:value-of select="count(ui:thead/ui:th)"/>
					</xsl:when>
					<xsl:when test="ui:tbody/ui:tr[1]/ui:td">
						<xsl:value-of select="count(ui:tbody/ui:tr[1]/ui:th|ui:tbody/ui:tr[1]/ui:td)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="1"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="colSpan">
				<xsl:value-of select="$addCols + $numCols"/>
			</xsl:variable>
			<tfoot>
				<xsl:if test="$showPagination=1">
					<tr>
						<td class="wc_table_pag_cont" colspan="{$colSpan}">
							<xsl:apply-templates select="ui:pagination"/>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="ui:actions">
					<tr>
						<td colspan="{$colSpan}">
							<xsl:apply-templates select="ui:actions"/>
						</td>
					</tr>
				</xsl:if>
			</tfoot>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
