<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	<!--
		Build the button controls used to re-order items in a list box as per wc.ui.shuffler.xsl and wc.ui.multiSelectPair.xsl.
	-->
	<xsl:template name="listSortControls">
		<xsl:param name="id" select="@id"/>
		<span class="wc_sortcont">
			<xsl:if test="self::ui:multiselectpair">
				<xsl:text>&#x00a0;</xsl:text>
			</xsl:if>
			<xsl:call-template name="listSortControl">
				<xsl:with-param name="id" select="$id"/>
				<xsl:with-param name="value" select="'top'"/>
				<xsl:with-param name="toolTip"><xsl:text>{{#i18n}}shuffle_top{{/i18n}}</xsl:text></xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="listSortControl">
				<xsl:with-param name="id" select="$id"/>
				<xsl:with-param name="value" select="'up'"/>
				<xsl:with-param name="toolTip"><xsl:text>{{#i18n}}shuffle_up{{/i18n}}</xsl:text></xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="listSortControl">
				<xsl:with-param name="id" select="$id"/>
				<xsl:with-param name="value" select="'down'"/>
				<xsl:with-param name="toolTip"><xsl:text>{{#i18n}}shuffle_down{{/i18n}}</xsl:text></xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="listSortControl">
				<xsl:with-param name="id" select="$id"/>
				<xsl:with-param name="value" select="'bottom'"/>
				<xsl:with-param name="toolTip"><xsl:text>{{#i18n}}shuffle_bottom{{/i18n}}</xsl:text></xsl:with-param>
			</xsl:call-template>
		</span>
	</xsl:template>

	<!-- Outputs each shuffle control as a HTML BUTTON element. -->
	<xsl:template name="listSortControl">
		<xsl:param name="id"/>
		<xsl:param name="value"/>
		<xsl:param name="toolTip"/>
		<button class="wc_sorter wc_btn_icon wc-invite" type="button" value="{$value}" aria-controls="{$id}" title="{$toolTip}">
			<xsl:call-template name="disabledElement"/>
			<xsl:call-template name="icon">
				<xsl:with-param name="class">
					<xsl:text>fa-fw </xsl:text>
					<xsl:choose>
						<xsl:when test="$value eq 'top'">fa-angle-double-up</xsl:when>
						<xsl:when test="$value eq 'up'">fa-angle-up</xsl:when>
						<xsl:when test="$value eq 'down'">fa-angle-down</xsl:when>
						<xsl:otherwise>fa-angle-double-down</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
		</button>
	</xsl:template>
</xsl:stylesheet>
