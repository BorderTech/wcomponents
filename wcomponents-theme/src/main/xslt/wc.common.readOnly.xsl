<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!-- Common helper template to output the readOnly state of many form control components. -->
	<xsl:template name="readOnlyControl">
		<xsl:param name="class" select="''"/>
		<xsl:param name="applies" select="''"/>
		<xsl:param name="useReadOnlyMode" select="0"/>
		<xsl:param name="toolTip" select="''"/>
		<xsl:variable name="linkWithText">
			<xsl:choose>
				<xsl:when test="text() and (self::ui:phonenumberfield or self::ui:emailfield)">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"></xsl:number>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="elementName">
			<xsl:choose>
				<xsl:when test="self::ui:textarea and ./ui:rtf">
					<xsl:text>div</xsl:text>
				</xsl:when>
				<xsl:when test="self::ui:textarea">
					<xsl:text>pre</xsl:text>
				</xsl:when>
				<xsl:when test="number($linkWithText) eq 1">
					<xsl:text>a</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>span</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$elementName}">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="class">
					<xsl:text>wc_ro</xsl:text>
					<xsl:if test="normalize-space($class) ne ''">
						<xsl:value-of select="concat(' ', normalize-space($class))"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="title">
				<xsl:with-param name="title" select="$toolTip"/>
			</xsl:call-template>
			<xsl:call-template name="roComponentName"/>
			<xsl:if test="self::ui:checkbox or self::ui:radiobutton or self::ui:togglebutton or self::ui:numberfield">
				<xsl:attribute name="data-wc-value">
					<xsl:choose>
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
			<xsl:if test="number($linkWithText) eq 1">
				<xsl:attribute name="href">
					<xsl:choose>
						<xsl:when test="self::ui:emailfield">
							<xsl:text>mailto:</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>tel:</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$applies ne 'none'">
				<xsl:choose>
					<xsl:when test="self::ui:textarea[not(ui:rtf)]">
						<xsl:apply-templates xml:space="preserve"/>
					</xsl:when>
					<xsl:when test="$applies ne '' and number($useReadOnlyMode) eq 1">
						<xsl:apply-templates select="$applies" mode="readOnly"/>
					</xsl:when>
					<xsl:when test="$applies ne ''">
						<xsl:apply-templates select="$applies"/>
					</xsl:when>
					<xsl:when test="number($useReadOnlyMode) eq 1">
						<xsl:apply-templates select="*" mode="readOnly"/>
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
