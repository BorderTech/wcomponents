<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<xsl:template match="ui:radiobutton">
		<xsl:choose>
			<xsl:when test="@readOnly">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="class">
						<xsl:text>wc-ro-input</xsl:text>
						<xsl:if test="@selected">
							<xsl:text> wc_ro_sel</xsl:text>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<span>
					<xsl:call-template name="commonInputWrapperAttributes"/>
					<xsl:element name="input">
						<xsl:call-template name="wrappedInputAttributes">
							<xsl:with-param name="type">
								<xsl:text>radio</xsl:text>
							</xsl:with-param>
							<xsl:with-param name="name">
								<xsl:value-of select="@groupName" />
							</xsl:with-param>
						</xsl:call-template>
						<xsl:if test="@selected">
							<xsl:attribute name="checked">
								<xsl:text>checked</xsl:text>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="@value">
							<xsl:attribute name="value">
								<xsl:value-of select="@value"/>
							</xsl:attribute>
						</xsl:if>
					</xsl:element>
					<xsl:if test="not(@readOnly)">
						<xsl:call-template name="hField">
							<xsl:with-param name="name" select="@groupName"/>
						</xsl:call-template>
					</xsl:if>
				</span>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
