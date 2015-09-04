<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.ui.table.n.xsl"/>

	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
<!--
		Outputs a button element for column sorting. Called from transform of
		ui:thead/ui:th

		param sortMode: default 'submit' the sort mode used by the table (set on ui:sort).
		param tableId: The id attribute of the nearest ancestor ui:table.
		param title: The sort control's title string.
		param sortDown: Indicates that the button is for a descending sort. This is
		 required for setting the extra attributes needed for the second name:value
		 pair required by the server.
		param sorted: Indicates that the control is for a column which is already sorted.
	-->
	<xsl:template name="tSortControl">
		<xsl:param name="sortMode" select="'submit'"/>
		<xsl:param name="tableId"/>
		<xsl:param name="title"/>
		<xsl:param name="sortDown"/>
		<xsl:param name="sorted"/>
		<xsl:element name="button">
			<xsl:attribute name="type">
				<xsl:text>submit</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="id">
				<xsl:value-of select="concat($tableId,'${wc.ui.table.id.sort.suffix}',position())"/>
				<xsl:if test="$sortDown=1 and not($sorted=1)">
					<xsl:text>${wc.ui.table.id.sort.suffix.extension}</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="$tableId"/>
				<xsl:text>.sort</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="value">
				<xsl:value-of select="position() - 1"/>
			</xsl:attribute>
			<!-- yes, this is a button with role button, do no remove it!-->
			<xsl:attribute name="role">
				<xsl:text>button</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>wc_table_sort_ctrl wc_btn_nada</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:value-of select="$title"/>
			</xsl:attribute>
			<xsl:call-template name="disabledElement">
				<xsl:with-param name="isControl" select="1"/>
				<xsl:with-param name="field" select="ancestor::ui:table[1]"/>
			</xsl:call-template>
			<!-- sorting a table should not commit data -->
			<xsl:attribute name="formnovalidate">
				<xsl:text>formnovalidate</xsl:text>
			</xsl:attribute>
			<xsl:if test="$sortDown=1">
				<xsl:attribute name="data-wc-name">
					<xsl:value-of select="$tableId"/>
					<xsl:text>.sortDesc</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="data-wc-value">
					<xsl:copy-of select="$t"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$sorted=1">
				<xsl:attribute name="aria-pressed">
					<xsl:choose>
						<xsl:when test="$sortDown=1">
							<xsl:text>false</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of select="$t"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$sortMode='dynamic'">
				<xsl:call-template name="tableAjaxController">
					<xsl:with-param name="tableId" select="$tableId"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>