<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.table.thead.th.n.tSortControls.xsl"/>
	<!--
		param maxIndent: default=0
		    This is the number of columns required in the span of the first column to
		    allow for the indentation of child rows throughout the entire table. See
			comments in transform of ui:table in wc.ui.table.xsl.
	-->
	<xsl:template match="ui:th" mode="thead">
		<xsl:param name="maxIndent" select="0"/>
		<xsl:variable name="tableId" select="../../@id"/>
		<xsl:variable name="sortControl" select="../../ui:sort"/>
		<xsl:variable name="sortDesc" select="$sortControl/@descending"/>
		<xsl:variable name="sortCol" select="$sortControl/@col"/>
		<xsl:variable name="isSorted">
			<xsl:choose>
				<xsl:when test="@sortable=$t and position()-1 = $sortCol">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="isDescSort">
			<xsl:choose>
				<xsl:when test="$isSorted=0 or not($sortDesc=$t)">
					<xsl:number value="0"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="th">
			<xsl:attribute name="id">
				<xsl:value-of select="concat($tableId,'${wc.ui.table.id.thead.th.suffix}',position())"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="@align">
						<xsl:value-of select="@align"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>${wc.common.align.std}</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="@sortable=$t">
				<xsl:attribute name="sorted">
					<xsl:if test="$isSorted=1">
						<xsl:if test="$isDescSort=1">
							<xsl:text>reversed </xsl:text>
						</xsl:if>
						<xsl:text>1</xsl:text>
					</xsl:if>
				</xsl:attribute>
				<xsl:attribute name="aria-sort">
					<xsl:choose>
						<xsl:when test="$isSorted=0">
							<xsl:text>none</xsl:text>
						</xsl:when>
						<xsl:when test="$sortDesc=$t">
							<xsl:text>descending</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>ascending</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$maxIndent &gt; 0 and position()=1">
				<xsl:attribute name="colspan">
					<xsl:value-of select="$maxIndent + 1"/><!-- + 1 for the expander column -->
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@sortable=$t and $sortControl">
				<xsl:attribute name="title">
					<xsl:choose>
						<xsl:when test="$isSorted=0">
							<xsl:value-of select="$$${wc.ui.table.string.notSorted}"/>
						</xsl:when>
						<xsl:when test="$isDescSort=1">
							<xsl:value-of select="$$${wc.ui.table.string.sortedDesc}"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$$${wc.ui.table.string.sortedAsc}"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="ui:decoratedLabel">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
			<xsl:if test="@sortable=$t and $sortControl">
				<xsl:variable name="sortMode">
					<xsl:choose>
						<xsl:when test="$sortControl/@mode">
							<xsl:value-of select="$sortControl/@mode"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>dynamic</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:element name="span">
					<xsl:attribute name="class">
						<xsl:text>wc_table_sort_container</xsl:text>
					</xsl:attribute>
					<xsl:call-template name="tSortControl">
						<xsl:with-param name="sortMode" select="$sortMode"/>
						<xsl:with-param name="tableId" select="$tableId"/>
						<xsl:with-param name="title">
							<xsl:choose>
								<xsl:when test="$isSorted=0">
									<xsl:value-of select="$$${wc.ui.table.sort.message.sortcontrol.asc}"/>
								</xsl:when>
								<xsl:when test="$isDescSort=1">
									<xsl:value-of select="$$${wc.ui.table.sort.message.sortcontrol.toggleasc}"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$$${wc.ui.table.sort.message.sortcontrol.toggledesc}"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
						<xsl:with-param name="sorted" select="$isSorted"/>
						<xsl:with-param name="sortDown" select="$isSorted * (1 - $isDescSort)"/>
					</xsl:call-template>
					<xsl:if test="$isSorted=0">
						<xsl:call-template name="tSortControl">
							<xsl:with-param name="sortMode" select="$sortMode"/>
							<xsl:with-param name="tableId" select="$tableId"/>
							<xsl:with-param name="title">
								<xsl:value-of select="$$${wc.ui.table.sort.message.sortcontrol.desc}"/>
							</xsl:with-param>
							<xsl:with-param name="sortDown" select="1"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
