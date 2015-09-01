<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.table.n.offscreenSpan.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

<!--
 Template for ui:thead tests whether the table requires selection and expansion 
 controls and if so applies these in a row. Then outputs the column headers in a
 separate row.

 parameters
    maxIndent and addCols: see comments in transform of ui:table in wc.ui.table.xsl

 The thead element is not hidden for a11y reasons. If the hidden attribute is "true"
 then only the row containing the column headers is hidden. In this instance hidden
 means rendered off screen.

 See
    wc.ui.table.thead.th.xsl
    wc.ui.table.thead.th_td_col.xsl
-->
	<xsl:template match="ui:thead">
		<xsl:param name="maxIndent" select="0"/>
		<xsl:param name="addCols" select="0"/>
		<xsl:param name="disabled" select="0"/>
		<xsl:variable name="tableId" select="../@id"/>
		<xsl:variable name="hasRowSelection">
			<xsl:choose>
				<xsl:when test="../ui:rowSelection[@selectAll='text'] and ..//ui:tr[not(@unselectable=$t)]">
					<xsl:value-of select="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="hasExpandAll">
			<xsl:choose>
				<xsl:when test="../ui:rowExpansion/@expandAll=$t and ..//ui:subTr[ancestor::ui:table[1]/@id=$tableId]">
					<xsl:value-of select="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="hasHeaderElements">
			<xsl:value-of select="$hasRowSelection + $hasExpandAll"/>
		</xsl:variable>
		<xsl:element name="thead">
			<xsl:if test="$hasHeaderElements &gt; 0">
				<xsl:variable name="numCols">
					<xsl:value-of select="count(ui:th)"/>
				</xsl:variable>
				<!-- NOTE: colspan1 must include all padding columns etc -->
				<xsl:variable name="colSpan1">
					<xsl:value-of select="$addCols + $maxIndent +  floor($numCols div $hasHeaderElements)"/>
				</xsl:variable>
				<xsl:element name="tr">
					<xsl:attribute name="class">
						<xsl:text>wc_table_func</xsl:text>
					</xsl:attribute>
					<xsl:if test="$hasRowSelection = 1">
						<xsl:element name="td">
							<xsl:attribute name="colspan">
								<xsl:value-of select="$colSpan1"/>
							</xsl:attribute>
							<xsl:apply-templates select="../ui:rowSelection"/>
						</xsl:element>
					</xsl:if>
					<xsl:if test="$hasExpandAll = 1">
						<xsl:element name="td">
							<xsl:attribute name="colspan">
								<xsl:choose>
									<xsl:when test="$hasRowSelection = 1">
										<xsl:value-of select="$numCols + $maxIndent + $addCols - $colSpan1"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$colSpan1"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<xsl:apply-templates select="../ui:rowExpansion"/>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:if>
			<xsl:element name="tr">
				<xsl:if test="../ui:rowSelection">
					<xsl:element name="th">
						<xsl:attribute name="class">
							<xsl:text>wc_table_sel_wrapper</xsl:text>
						</xsl:attribute>
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
					</xsl:element>
				</xsl:if>
				<xsl:if test="../ui:rowExpansion and $maxIndent = 0">
					<xsl:element name="th">
						<xsl:attribute name="class">
							<xsl:text>wc_table_rowexp_container</xsl:text>
						</xsl:attribute>
						<xsl:call-template name="offscreenSpan">
							<xsl:with-param name="text" select="$$${wc.ui.table.string.expandCollapse}"/>
						</xsl:call-template>
					</xsl:element>
				</xsl:if>
				<xsl:apply-templates select="ui:th" mode="thead">
					<xsl:with-param name="maxIndent" select="$maxIndent"/>
				</xsl:apply-templates>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>