<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.table.pagination.n.paginationDescription.xsl"/>
	<xsl:import href="wc.ui.table.n.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	
	<xsl:template match="ui:rowsSelect">
		<xsl:param name="tableId"/>
		<xsl:param name="idSuffix"/>
		<xsl:variable name="rppChooserName">
			<xsl:value-of select="concat($tableId,'.rows', $idSuffix)"/>
		</xsl:variable>
		<xsl:element name="label">
			<xsl:attribute name="for">
				<xsl:value-of select="$rppChooserName"/>
			</xsl:attribute>
			<xsl:value-of select="$$${wc.ui.table.string.pagination.label.chooseRowsPerPage}"/>
		</xsl:element>
		<xsl:element name="select">
			<xsl:attribute name="id">
				<xsl:value-of select="$rppChooserName"/>
			</xsl:attribute>
			<!-- NOTE: do not use name or data-wc-name as we do not want to trigger an unsaved changes warning -->
			<xsl:attribute name="class">
				<xsl:text>wc_table_pag_rpp</xsl:text>
			</xsl:attribute>
			<xsl:call-template name="tableAjaxController">
				<xsl:with-param name="tableId" select="$tableId"/>
			</xsl:call-template>
			<xsl:call-template name="disabledElement">
				<xsl:with-param name="field" select="ancestor::ui:table[1]"/>
				<xsl:with-param name="isControl" select="1"/>
			</xsl:call-template>
			<xsl:apply-templates mode="rowsPerPage">
				<xsl:with-param name="rowsPerPage" select="../@rowsPerPage"/>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="ui:option" mode="rowsPerPage">
		<xsl:param name="rowsPerPage"/>
		<xsl:variable name="value" select="@value"/>
		<xsl:element name="option">
			<xsl:attribute name="value">
				<xsl:value-of select="$value"/>
			</xsl:attribute>
			<xsl:if test="$rowsPerPage=$value">
				<xsl:attribute name="selected">
					<xsl:text>selected</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$value='0'">
					<xsl:value-of select="$$${wc.ui.table.string.pagination.label.chooseAllRowsPerPage}"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$value"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
