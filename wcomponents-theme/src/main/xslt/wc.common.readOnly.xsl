<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!-- Common helper template to output the readOnly state of many form control components. -->
	<xsl:template name="readOnlyControl">
		<xsl:param name="class" select="''"/>
		<xsl:param name="applies" select="''"/>
		<xsl:param name="useReadOnlyMode" select="0"/>
		<xsl:param name="toolTip" select="''"/>
		<xsl:param name="isList" select="0"/>
		<xsl:variable name="elementName">
			<xsl:choose>
				<xsl:when test="number($isList) eq 1">
					<xsl:text>ul</xsl:text>
				</xsl:when>
				<xsl:when test="self::ui:textarea and ./ui:rtf">
					<xsl:text>div</xsl:text>
				</xsl:when>
				<xsl:when test="self::ui:textarea">
					<xsl:text>pre</xsl:text>
				</xsl:when>
				<xsl:when test="self::ui:datefield[@date and not(@allowPartial)]">
					<xsl:text>time</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>span</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$elementName}">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="class">
					<xsl:value-of select="$class"/>
					<xsl:if test="number($isList) eq 1">
						<xsl:text> wc_list_nb</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:if test="$toolTip != ''">
				<xsl:call-template name="title">
					<xsl:with-param name="title" select="$toolTip"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:call-template name="roComponentName"/>
			<xsl:if test="self::ui:checkbox or self::ui:radiobutton or self::ui:togglebutton or self::ui:numberfield or self::ui:datefield[@allowPartial and @date]">
				<xsl:attribute name="data-wc-value">
					<xsl:choose>
						<xsl:when test="self::ui:datefield">
							<xsl:value-of select="@date"/>
						</xsl:when>
						<xsl:when test="self::ui:numberfield">
							<xsl:value-of select="text()"/>
						</xsl:when>
						<xsl:when test="@selected">
							<xsl:text>true</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>false</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="self::ui:datefield[@date and not(@allowPartial)]">
				<xsl:attribute name="datetime">
					<xsl:value-of select="@date"/>
				</xsl:attribute>
			</xsl:if>
			<!-- NOTE applies must use non-typed comparison as list components may pass in a list of nodeLists or list of nodes -->
			<xsl:if test="$applies != 'none'">
				<xsl:choose>
					<xsl:when test="self::ui:datefield">
						<xsl:if test="not(@date)">
							<xsl:value-of select="."/>
						</xsl:if>
					</xsl:when>
					<xsl:when test="self::ui:textarea[not(ui:rtf)]">
						<xsl:apply-templates xml:space="preserve"/>
					</xsl:when>
					<xsl:when test="$applies != '' and number($useReadOnlyMode) eq 1">
						<xsl:apply-templates select="$applies" mode="readOnly">
							<xsl:with-param name="single" select="1 - number($isList)"/>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="number($useReadOnlyMode) eq 1">
						<xsl:apply-templates select="*" mode="readOnly">
							<xsl:with-param name="single" select="1 - number($isList)"/>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="$applies != ''">
						<xsl:apply-templates select="$applies"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<xsl:template name="roComponentName">
		<xsl:attribute name="data-wc-component">
			<xsl:value-of select="local-name()"/>
		</xsl:attribute>
	</xsl:template>
</xsl:stylesheet>
