<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.selectToggle.xsl"/>
	<!--
		This template creates the rowSelection (select all, select none) controls if required. It is called explicitly from
		the template match for ui:thead. If there are no selectable rows then nothing is output.
	
		NOTE: This template does not make the individual rows selectable. That is done in the transform of ui:tr.
	
		NOTE 2: Removed support for ui:rowSelection/@submitOnChange due to usability and accessibility failure. If the
		selection mode is single selection there is no way to select a row more than 1 row from the currently selected row
		without using a pointing device of some kind. The attribute ui:rowSelection/@submitOnChange is still used to create
		round-trip mode select/deselect all controls.
	-->
	<xsl:template match="ui:rowSelection">
		<xsl:if test="..//ui:tr[not(@unselectable)]">
			<xsl:variable name="selected">
				<xsl:choose>
					<xsl:when test="count(..//ui:tr[not(@unselectable)]) = count(..//ui:tr[@selected])">
						<xsl:text>all</xsl:text>
					</xsl:when>
					<xsl:when test="count(..//ui:tr[@selected])=0">
						<xsl:text>none</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>some</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="controlLabel">
				<xsl:if test="@selectAll='control'">
					<xsl:value-of select="$$${wc.ui.table.string.rowSelection.label}"/>
				</xsl:if>
			</xsl:variable>
			<xsl:variable name="bodyId" select="concat(../@id,'${wc.ui.table.id.body.suffix}')"/>
			<xsl:call-template name="selectToggle">
				<xsl:with-param name="for" select="$bodyId"/>
				<xsl:with-param name="id" select="$bodyId"/>
				<xsl:with-param name="selected" select="$selected"/>
				<xsl:with-param name="label" select="$controlLabel"/>
				<xsl:with-param name="roundTrip" select="@submitOnChange"/>
				<xsl:with-param name="type" select="@selectAll"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
