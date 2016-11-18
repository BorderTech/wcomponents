<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.ui.table.n.tableAjaxController.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl" />

	<!-- 
		ui:th inside the ui:thead element.

		Structural: do not override.
	-->
	<xsl:template match="ui:th" mode="thead">
		
		<xsl:variable name="tableId" select="../../@id"/>
		
		<th id="{concat($tableId,'_thh', position())}" scope="col" data-wc-columnidx="{position() - 1}">
			
			<xsl:call-template name="makeCommonClass"/>
			
			<xsl:if test="@sortable">
				<xsl:variable name="sortControl" select="../../ui:sort"/>
				
				<xsl:if test="$sortControl">
					<xsl:attribute name="tabindex">0</xsl:attribute>
					<xsl:variable name="sortDesc" select="$sortControl/@descending"/>
					
					<xsl:variable name="isSorted">
						<xsl:choose>
							<xsl:when test="position() - 1 eq number($sortControl/@col)">
								<xsl:number value="1"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="0"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					
					<xsl:if test="number($isSorted) eq 1">
						<xsl:attribute name="sorted">
							<xsl:if test="$sortDesc eq $t">
								<xsl:text>reversed </xsl:text>
							</xsl:if>
							<xsl:text>1</xsl:text>
						</xsl:attribute>
					</xsl:if>
					
					<xsl:attribute name="aria-sort">
						<xsl:choose>
							<xsl:when test="number($isSorted) eq 0">
								<xsl:text>none</xsl:text>
							</xsl:when>
							<xsl:when test="$sortDesc eq $t">
								<xsl:text>descending</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>ascending</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:if test="../../@disabled"><!-- WDataTable only: to be removed. -->
						<xsl:attribute name="aria-disabled">
							<xsl:value-of select="$t"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
			</xsl:if>
			<xsl:apply-templates select="ui:decoratedlabel">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
		</th>
	</xsl:template>

	<!--
		Transform of a ui:th element within a ui:tr. This is a row header and is a 1:1 map with a HTML th element.

		Structural: do not override.
	-->
	<xsl:template match="ui:th">
		<xsl:param name="myTable"/>

		<xsl:variable name="tableId" select="$myTable/@id"/>
		<xsl:variable name="myHeader" select="$myTable/ui:thead/ui:th[1]"/>

		<th id="{concat($tableId,'_trh',../@rowIndex)}" scope="row">
			<xsl:if test="$myHeader">
				<xsl:attribute name="headers">
					<xsl:value-of select="concat($tableId,'_thh','1')"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="$myHeader">
						<xsl:value-of select="concat('wc-align-', $myHeader/@align)"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates />
		</th>
	</xsl:template>
</xsl:stylesheet>
