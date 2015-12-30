<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.offscreenSpan.xsl"/>

	<!--
		Template for ui:thead. Simple generation of the HTML thead element. If the table needs row selection or expansion
		columns these are added then the ui:th elements are applied.
	-->
	<xsl:template match="ui:thead">
		<thead>
			<tr>
				<xsl:if test="../ui:rowSelection">
					<th class="wc_table_sel_wrapper" scope="col">
						<xsl:choose>
							<xsl:when test="../ui:rowSelection/@selectAll = 'control'">
								<xsl:apply-templates select="../ui:rowSelection"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="offscreenSpan">
									<xsl:with-param name="text">
										<xsl:choose>
											<xsl:when test="../ui:rowSelection/@multiple">
												<xsl:value-of select="$$${wc.ui.table.rowSelect.multiselect.message}"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$$${wc.ui.table.rowSelect.singleselect.message}"/>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</th>
				</xsl:if>
				<xsl:if test="../ui:rowExpansion">
					<th class="wc_table_rowexp_container" scope="col">
						<xsl:call-template name="offscreenSpan">
							<xsl:with-param name="text" select="$$${wc.ui.table.string.expandCollapse}"/>
						</xsl:call-template>
					</th>
				</xsl:if>
				<xsl:apply-templates select="ui:th" mode="thead"/>
			</tr>
		</thead>
	</xsl:template>
</xsl:stylesheet>
