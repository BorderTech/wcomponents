<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<!--
		Creates the control which is used to collapse and expand rows. Called from
		the transform for ui:tr.
		param myTable: nearest ancestor table element
	-->
	<xsl:template name="tableCollapserElement">
		<xsl:param name="myTable"/>
		<xsl:param name="id"/>

		<xsl:variable name="tableId" select="$myTable/@id"/>
		<xsl:variable name="expMode" select="$myTable/ui:rowExpansion/@mode"/>
		<xsl:variable name="isOpen">
			<xsl:if test="ui:subTr/@open=$t">
				<xsl:value-of select="1"/>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="expansionMode">
			<xsl:choose>
				<xsl:when test="($expMode='lazy' or $expMode='eager') and $isOpen=1">
					<xsl:text>client</xsl:text>
				</xsl:when>
				<xsl:when test="$expMode='eager'">
					<xsl:text>lazy</xsl:text>
				</xsl:when>
				<xsl:when test="$expMode">
					<xsl:value-of select="$expMode"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>client</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:element name="button">
			<xsl:attribute name="id">
				<xsl:value-of select="concat($id,'${wc.ui.table.rowExpansion.id.suffix}')"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>wc_table_rowexp_ctrl wc_btn_nada</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="type">
				<xsl:choose>
					<xsl:when test="$expansionMode='server'">
						<xsl:text>submit</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>button</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:value-of select="$$${wc.ui.table.rowExpansion.message.collapser}"/>
			</xsl:attribute>
			<xsl:attribute name="aria-expanded">
				<xsl:choose>
					<xsl:when test="ui:subTr/@open=$t and (ancestor::ui:subTr[not(@open) or @open='false'])">
						<xsl:text>false</xsl:text>
					</xsl:when>
					<xsl:when test="ui:subTr/@open=$t">
						<xsl:copy-of select="$t"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test="@disabled">
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="isControl" select="1"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="isControl" select="1"/>
						<xsl:with-param name="field" select="ancestor::ui:table[1]"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>

			<xsl:if test="$expansionMode='lazy' or $expansionMode='dynamic'">
				<xsl:attribute name="data-wc-ajaxalias">
					<xsl:value-of select="$tableId"/>
				</xsl:attribute>
				<xsl:if test="$expansionMode='lazy'">
					<xsl:attribute name="data-wc-expmode">
						<xsl:value-of select="$expansionMode"/>
					</xsl:attribute>
				</xsl:if>
			</xsl:if>

			<xsl:attribute name="aria-controls">
				<xsl:choose>
					<xsl:when test="ui:subTr/ui:tr">
						<!--
							NOTE: we need the controlled row list to include any other rowExpansion controls
							in the subTr's trs. This is why we have ui:subTr/ui:tr/ui:subTr
						-->
						<xsl:apply-templates select="ui:subTr/ui:tr/ui:subTr|ui:subTr/ui:tr" mode="subRowControlIdentifier">
							<xsl:with-param name="tableId" select="$tableId"/>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="ui:subTr/ui:content">
						<xsl:value-of select="concat($tableId,'${wc.ui.table.id.subTr.content.suffix}',@rowIndex)"/>
					</xsl:when>
					<xsl:when test="ui:subTr">
						<xsl:value-of select="concat($tableId,'${wc.ui.table.id.subTr.suffix}',@rowIndex)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$tableId"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
