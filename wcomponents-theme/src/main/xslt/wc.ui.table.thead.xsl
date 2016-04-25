<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.offscreenSpan.xsl"/>

	<!--
		Template for ui:thead. Simple generation of the HTML thead element. If the table needs row selection or expansion
		columns these are added then the ui:th elements are applied.
	-->
	<xsl:template match="ui:thead">
		<xsl:param name="hasRole" select="0"/>
		<thead>
			<tr>
				<xsl:if test="$hasRole &gt; 0">
					<xsl:attribute name="role">
						<xsl:text>row</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="../ui:rowselection">
					<th class="wc_table_sel_wrapper" scope="col">
						<xsl:if test="$hasRole &gt; 0">
							<xsl:attribute name="role">
								<xsl:text>columnheader</xsl:text>
							</xsl:attribute>
						</xsl:if>
						<xsl:choose>
							<xsl:when test="../ui:rowselection/@selectAll = 'control'">
								<xsl:apply-templates select="../ui:rowselection"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:attribute name="aria-hidden">true</xsl:attribute>
								<xsl:text>&#xa0;</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</th>
				</xsl:if>
				<xsl:if test="../ui:rowexpansion">
					<th class="wc_table_rowexp_container" scope="col">
						<xsl:if test="$hasRole &gt; 0">
							<xsl:attribute name="role">
								<xsl:text>columnheader</xsl:text>
							</xsl:attribute>
						</xsl:if>
						<xsl:call-template name="offscreenSpan">
							<xsl:with-param name="text" select="$$${wc.ui.table.string.expandCollapse}"/>
						</xsl:call-template>
					</th>
				</xsl:if>
				<xsl:apply-templates select="ui:th" mode="thead">
					<xsl:with-param name="hasRole" select="$hasRole"/>
				</xsl:apply-templates>
			</tr>
		</thead>
	</xsl:template>
</xsl:stylesheet>
