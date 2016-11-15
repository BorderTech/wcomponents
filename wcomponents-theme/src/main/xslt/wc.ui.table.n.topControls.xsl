<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Creates the text-mode row select all/none, pagination controls and the expand all/none controls if required.
		Called from the transform for ui:table.
		
		Reasonably safe to override this template so long as the class attribute values are retained. I would suggest,
		however, leaving it be other than tweaking the order in which the components appear.
	-->
	<xsl:template name="topControls">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="hasExpandAll">
			<xsl:choose>
				<xsl:when test="ui:rowexpansion/@expandAll and .//ui:subtr[ancestor::ui:table[1]/@id eq $id]">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="hasRowSelection">
			<xsl:choose>
				<xsl:when test="ui:rowselection[@selectAll] and ..//ui:tr[not(@unselectable) and ancestor::ui:table[1]/@id eq $id]">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="hasPagination">
			<xsl:choose>
				<xsl:when test="not(ui:pagination) or not(ui:pagination/@controls) or ui:pagination/@controls eq 'bottom'">
					<xsl:number value="0"/>
				</xsl:when>
				<xsl:when test="ui:pagination">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="$hasExpandAll + $hasRowSelection + $hasPagination gt 0">
			<div class="wc_table_top_controls">
				<xsl:if test="number($hasRowSelection) eq 1">
					<div class="wc_table_sel_cont">
						<xsl:apply-templates select="ui:rowselection"/>
					</div>
				</xsl:if>
				<xsl:if test="number($hasExpandAll) eq 1">
					<div class="wc_table_exp_cont">
						<xsl:apply-templates select="ui:rowexpansion"/>
					</div>
				</xsl:if>
				<xsl:if test="number($hasPagination) eq 1">
					<div class="wc_table_pag_cont">
						<xsl:apply-templates select="ui:pagination">
							<xsl:with-param name="idSuffix" select="'top'"/>
						</xsl:apply-templates>
					</div>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
