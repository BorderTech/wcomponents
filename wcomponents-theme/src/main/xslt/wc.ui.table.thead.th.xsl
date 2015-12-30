<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.table.n.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:template match="ui:th" mode="thead">
		<xsl:variable name="tableId" select="../../@id"/>
		
		
		<th id="{concat($tableId,'${wc.ui.table.id.thead.th.suffix}', position())}" scope="col">
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
			<xsl:attribute name="data-wc-columnidx">
				<xsl:value-of select="position() - 1"/>
			</xsl:attribute>
			
			<xsl:if test="@sortable=$t">
				<xsl:variable name="sortControl" select="../../ui:sort"/>
				
				<xsl:if test="$sortControl">
					<xsl:attribute name="tabindex">0</xsl:attribute>
					
					<xsl:variable name="sortDesc" select="$sortControl/@descending"/>
					<xsl:variable name="sortCol" select="$sortControl/@col"/>
					
					<xsl:variable name="isSorted">
						<xsl:choose>
							<xsl:when test="position()-1 = $sortCol">
								<xsl:number value="1"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="0"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					
					<xsl:if test="$isSorted=1">
						<xsl:attribute name="sorted">
							<xsl:if test="$sortDesc=$t">
								<xsl:text>reversed </xsl:text>
							</xsl:if>
							<xsl:text>1</xsl:text>
						</xsl:attribute>
					</xsl:if>
					
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
					
					<xsl:attribute name="title">
						<xsl:choose>
							<xsl:when test="$isSorted=0">
								<xsl:value-of select="$$${wc.ui.table.string.notSorted}"/>
							</xsl:when>
							<xsl:when test="$sortDesc=$t">
								<xsl:value-of select="$$${wc.ui.table.string.sortedDesc}"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$$${wc.ui.table.string.sortedAsc}"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					
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
					
					<xsl:if test="$sortMode='dynamic'">
						<xsl:call-template name="tableAjaxController">
							<xsl:with-param name="tableId" select="$tableId"/>
						</xsl:call-template>
					</xsl:if>
					
					<xsl:attribute name="data-wc-sortmode">
						<xsl:value-of select="$sortMode"/>
					</xsl:attribute>
				</xsl:if>
			</xsl:if>
			<xsl:apply-templates select="ui:decoratedLabel">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
		</th>
	</xsl:template>
</xsl:stylesheet>
