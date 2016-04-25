<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Creates the text-mode row select all/none, pagination controls and the expand all/none controls if required.
		Called from the transform for ui:table.
	-->
	<xsl:template name="topControls">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="hasExpandAll">
			<xsl:choose>
				<xsl:when test="ui:rowexpansion/@expandAll=$t and .//ui:subtr[ancestor::ui:table[1]/@id=$id]">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="hasRowSelection">
			<xsl:choose>
				<xsl:when test="ui:rowselection[@selectAll='text'] and ..//ui:tr[not(@unselectable=$t) and ancestor::ui:table[1]/@id=$id]">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="hasPagination">
			<xsl:choose>
				<xsl:when test="not(ui:pagination) or not(ui:pagination/@controls) or ui:pagination/@controls = 'bottom'">
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

		<xsl:if test="$hasExpandAll + $hasRowSelection + $hasPagination &gt; 0">
			<div class="wc_table_top_controls">
				<xsl:if test="$hasRowSelection = 1">
					<div class="wc_table_sel_cont">
						<xsl:apply-templates select="ui:rowselection"/>
					</div>
				</xsl:if>
				<xsl:if test="$hasExpandAll = 1">
					<div class="wc_table_exp_cont">
						<xsl:apply-templates select="ui:rowexpansion"/>
					</div>
				</xsl:if>
				<xsl:if test="$hasPagination = 1">
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
