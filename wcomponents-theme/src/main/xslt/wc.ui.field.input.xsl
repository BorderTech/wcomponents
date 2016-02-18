<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		The input part of WField is a wrapper for other components. FieldIndicators
		should be output after the functional content of the input.

		param parentLayout (length 1)
		Used to determine if the WField has a parent WFieldLayout (which may not be true
		if the WField is in an AJAX response. We have to calculate this in ui:field so we may as well pass it in.

		param labelWidth
		The labelWidth property of the parent WFieldLayout (if known). We have to
		calculate this in ui:field so we may as well pass it in.

		param isCheckRadio (1/0)
		Indicates whether the WField is a container for a WCheckBox, WRadioButton
		or a WSelectToggle (renderAs not control) as the primary input control,
		if so we need to make a few adjustments to the output order. We have to
		calculate this in ui:field so we may as well pass it in.
	-->
	<xsl:template match="ui:input">
		<xsl:param name="parentLayout"/>
		<xsl:param name="labelWidth"/>
		<xsl:param name="isCheckRadio"/>
		<xsl:variable name="inputWidth" select="../@inputWidth"/>
		<div>
			<xsl:attribute name="class">
				<xsl:call-template name="commonClassHelper"/>
				<xsl:if test="$inputWidth">
					<xsl:text> wc_inputwidth</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<!--
				If we are part of an ajaxResponse with REPLACE_CONTENT and we don't have a parent ui:field we
				need to add a transient attribute to act as a flag for the ajax subscriber
			-->
			<xsl:variable name="inputContainerWidth">
				<xsl:if test="$labelWidth!=''">
					<xsl:value-of select="100 - format-number($labelWidth,'#')"/>
				</xsl:if>
			</xsl:variable>
			<xsl:variable name="inputStyle">
				<xsl:if test="$parentLayout and $labelWidth!=''">
					<xsl:choose>
						<xsl:when test="not($inputWidth)">
							<xsl:value-of select="concat('width:',$inputContainerWidth,'%;')"/>
							<xsl:value-of select="concat('max-width:',$inputContainerWidth,'%;')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat('max-width:',$inputContainerWidth,'%;')"/>
						</xsl:otherwise>
					</xsl:choose>
					<!-- MARGIN-LEFT:
						if labelWidth is 100: none
						otherwise:
							STACKED: always (since we are only here if labelWidth is set)
							FLAT: only if the label is hidden
					-->
					<xsl:if test="not($labelWidth='100') and ($parentLayout='stacked' or preceding-sibling::ui:label/@hidden)">
						<xsl:text>margin-left:</xsl:text>
						<xsl:value-of select="format-number($labelWidth,'#')"/>
						<xsl:text>%;</xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$inputWidth">
					<xsl:value-of select="concat('width:',$inputWidth,'%;')"/>
				</xsl:if>
			</xsl:variable>
			<xsl:if test="$inputStyle!=''">
				<xsl:attribute name="style">
					<xsl:value-of select="$inputStyle"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$isCheckRadio!=1">
					<xsl:apply-templates select="node()[not(self::ui:fieldindicator)]"/>
					<xsl:apply-templates select="ui:fieldindicator"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="ui:checkbox|ui:radiobutton|ui:selecttoggle"/>
					<xsl:apply-templates select="node()[not(self::ui:fieldindicator or self::ui:checkbox or self::ui:radiobutton or self::ui:selecttoggle)]"/>
					<xsl:apply-templates select="ui:fieldindicator"/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>
</xsl:stylesheet>
