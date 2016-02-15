<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.table.pagination.n.paginationDescription.xsl"/>
	<xsl:import href="wc.ui.table.n.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	
	<xsl:template match="ui:rowsselect">
		<xsl:param name="tableId"/>
		<xsl:param name="idSuffix"/>
		<xsl:variable name="rppChooserName">
			<xsl:value-of select="concat($tableId,'.rows', $idSuffix)"/>
		</xsl:variable>
		<label for="{$rppChooserName}">
			<xsl:value-of select="$$${wc.ui.table.string.pagination.label.chooseRowsPerPage}"/>
			<select id="{$rppChooserName}" class="wc_table_pag_rpp">
				<!-- NOTE: do not use name or data-wc-name as we do not want to trigger an unsaved changes warning -->
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
			</select>
		</label>
	</xsl:template>
	
	<xsl:template match="ui:option" mode="rowsPerPage">
		<xsl:param name="rowsPerPage"/>
		<xsl:variable name="value" select="@value"/>
		<option value="{$value}">
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
		</option>
	</xsl:template>
</xsl:stylesheet>
