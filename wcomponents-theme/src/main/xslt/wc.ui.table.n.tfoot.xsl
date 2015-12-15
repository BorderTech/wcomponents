<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Creates a tfoot element to hold actions and pagination controls. Called
		from the transform for ui:table.

		You probably do not need to override this.

		See transform for ui:table in wc.ui.table.xsl for information about the parameters.
	-->
	<xsl:template name="tfoot">
		<xsl:param name="addCols" select="0"/>
		<xsl:if test="ui:pagination or ui:actions">
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
			<!-- NOTE: colspan1 must include all padding columns etc -->
			<xsl:variable name="colSpan">
				<xsl:value-of select="$addCols + $numCols"/>
			</xsl:variable>
			<xsl:element name="tfoot">
				<xsl:if test="ui:pagination">
					<xsl:element name="tr">
						<xsl:element name="td">
							<xsl:attribute name="class">
								<xsl:text>wc_table_pag_cont right</xsl:text>
							</xsl:attribute>
							<xsl:attribute name="colspan">
								<xsl:value-of select="$colSpan"/>
							</xsl:attribute>
							<xsl:apply-templates select="ui:pagination"/>
						</xsl:element>
					</xsl:element>
				</xsl:if>
				<xsl:if test="ui:actions">
					<xsl:element name="tr">
						<xsl:element name="td">
							<xsl:attribute name="colspan">
								<xsl:value-of select="$colSpan"/>
							</xsl:attribute>
							<xsl:apply-templates select="ui:actions"/>
						</xsl:element>
					</xsl:element>
				</xsl:if>
			</xsl:element>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
