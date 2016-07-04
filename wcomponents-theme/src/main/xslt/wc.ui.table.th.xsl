<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.table.n.tableAjaxController.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl" />

	<!-- 
		ui:th inside the ui:thead element.

		Structural: do not override.
	-->
	<xsl:template match="ui:th" mode="thead">
		<xsl:param name="hasRole" select="0"/>
		
		<xsl:variable name="tableId" select="../../@id"/>
		
		<th id="{concat($tableId,'_thh', position())}" scope="col" data-wc-columnidx="{position() - 1}">
			<xsl:if test="$hasRole &gt; 0">
				<xsl:attribute name="role">
					<xsl:text>columnheader</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="makeCommonClass"/>
			
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
					
					<xsl:call-template name="tableAjaxController">
						<xsl:with-param name="tableId" select="$tableId"/>
					</xsl:call-template>
					
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
		<xsl:param name="hasRole" select="0"/>

		<xsl:variable name="tableId" select="$myTable/@id"/>
		<xsl:variable name="myHeader" select="$myTable/ui:thead/ui:th[1]"/>

		<th id="{concat($tableId,'_trh',../@rowIndex)}" scope="row">
			<xsl:if test="$hasRole &gt; 0">
				<xsl:attribute name="role">
					<xsl:text>rowheader</xsl:text>
				</xsl:attribute>
			</xsl:if>
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
