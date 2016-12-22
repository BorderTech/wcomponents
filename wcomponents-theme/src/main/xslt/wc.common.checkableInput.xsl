<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<!-- WCheckBox and WRadioButton -->
	<xsl:template match="ui:checkbox|ui:radiobutton">
		<xsl:variable name="type">
			<xsl:choose>
				<xsl:when test="self::ui:checkbox or not(@groupName)">
					<xsl:text>checkbox</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>radio</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="id">
			<xsl:value-of select="@id"/>
		</xsl:variable>
		<xsl:variable name="name">
			<xsl:choose>
				<xsl:when test="@groupName and self::ui:radiobutton">
					<xsl:value-of select="@groupName"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$id"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="@readOnly">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="class">
						<xsl:text>wc-icon</xsl:text>
						<xsl:if test="@selected">
							<xsl:text> wc_ro_sel</xsl:text>
						</xsl:if>
					</xsl:with-param>
					<xsl:with-param name="toolTip">
						<xsl:choose>
							<xsl:when test="@selected">
								<xsl:text>{{t 'input_selected'}}</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>{{t 'input_unselected'}}</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="input">
					<xsl:attribute name="type">
						<xsl:value-of select="$type"/>
					</xsl:attribute>
					<xsl:call-template name="commonControlAttributes">
						<xsl:with-param name="name" select="$name"/>
					</xsl:call-template>
					<!-- Fortunately commonControlAttributes will only output a value attribute if
						the XML element has a value attribute; so we can add the ui:checkbox value
						here without changing the called template. -->
					<xsl:if test="self::ui:checkbox">
						<xsl:attribute name="value">
							<xsl:text>true</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@groupName and self::ui:checkbox">
						<xsl:attribute name="data-wc-group">
							<xsl:value-of select="@groupName"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@selected">
						<xsl:attribute name="checked">
							<xsl:text>checked</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="self::ui:radiobutton and not(@readOnly)">
			<xsl:call-template name="hField">
				<xsl:with-param name="name" select="$name"/>
			</xsl:call-template></xsl:if>
	</xsl:template>
</xsl:stylesheet>
