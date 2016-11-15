<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.getHVGap.xsl"/>
	<xsl:import href="wc.common.column.xsl"/>
	<!--
		ui:columnlayout is one of the possible layout child elements of WPanel.
	-->
	<xsl:template match="ui:columnlayout">
		<div>
			<xsl:attribute name="class">
				<xsl:text>wc-columnlayout</xsl:text>
				<xsl:call-template name="getHVGapClass">
					<xsl:with-param name="isVGap" select="1"/>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:variable name="width">
				<xsl:choose>
					<xsl:when test="ui:column[1]/@width">
						<xsl:value-of select="ui:column[1]/@width"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="cols" select="count(ui:column)"/>
			<xsl:choose>
				<xsl:when test="number($cols) eq 1"><!-- I don't know why people do this, but they do -->
					<xsl:apply-templates select="ui:cell" mode="clRow">
						<xsl:with-param name="align" select="ui:column[1]/@align"/>
						<xsl:with-param name="width" select="$width"/>
						<xsl:with-param name="cols" select="$cols"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="ui:cell[position() mod number($cols) eq 1]" mode="clRow">
						<xsl:with-param name="align" select="ui:column[1]/@align"/>
						<xsl:with-param name="width" select="$width"/>
						<xsl:with-param name="cols" select="$cols"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<!--
		This template creates each row and the first column in the row. It then applies
		templates selecting the following-sibling::ui:cell[position lt $cols] to
		build the rest of the columns in the row. The params to this template are
		for convenience and speed since they could all be derived but it is better to
		only calculate them once.
		
		param align: the column align property
		param width: the width of the first column
		param cols: the number of columns in each row in the layout.
	-->
	<xsl:template match="ui:cell" mode="clRow">
		<xsl:param name="align"/>
		<xsl:param name="width"/>
		<xsl:param name="cols" select="0"/>
		<div>
			<xsl:attribute name="class">
				<xsl:text>wc-row</xsl:text>
				<xsl:call-template name="getHVGapClass">
					<xsl:with-param name="gap" select="../@hgap"/>
				</xsl:call-template>
				<xsl:if test="contains(ancestor::ui:panel[1]/@class, 'wc-respond')">
					<xsl:text> wc-respond</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:call-template name="column">
				<xsl:with-param name="align" select="$align"/>
				<xsl:with-param name="width" select="$width"/>
			</xsl:call-template>
			<xsl:if test="number($cols) gt 1">
				<xsl:apply-templates select="following-sibling::ui:cell[position() lt number($cols)]" mode="clInRow"/>
			</xsl:if>
		</div>
	</xsl:template>

	<!--
		This template creates columns within a row (except the first). Each column has
		to look up the alignment and width of its position equivalent ui:column.
	-->
	<xsl:template match="ui:cell" mode="clInRow">
		<!--
			variable colPos
			This variable is used to find the ui:column which holds the meta-data pertinent
			to the column being constructed.
			
			The columns built in this template are columns 2...n but are called from a
			sibling using following-siblings and therefore their position() is 1...n-1. 
			Therefore to match the equivalent ui:column we have to use position() + 1.
		-->
		<xsl:variable name="colPos" select="position() + 1"/>
		<!--
			variable myColumn
			This is a handle to the ui:column sibling of the cell which has position relative
 			to the parent element equal to the value of $colPos calculated above.
		-->
		<xsl:variable name="myColumn" select="../ui:column[position() eq number($colPos)]"/>
		<xsl:call-template name="column">
			<xsl:with-param name="align" select="$myColumn/@align"/>
			<xsl:with-param name="width" select="$myColumn/@width"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
