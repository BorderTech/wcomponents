<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<!-- WCheckBox -->
	<xsl:template match="ui:checkbox">
		<xsl:choose>
			<xsl:when test="@readOnly">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="class">
						<xsl:text>wc-icon wc-ro-input</xsl:text>
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
				<span>
					<xsl:call-template name="commonInputWrapperAttributes"/>
					<xsl:element name="input">
						<xsl:call-template name="wrappedInputAttributes">
							<xsl:with-param name="type">
								<xsl:text>checkbox</xsl:text>
							</xsl:with-param>
						</xsl:call-template>
						<xsl:if test="@selected">
							<xsl:attribute name="checked">
								<xsl:text>checked</xsl:text>
							</xsl:attribute>
						</xsl:if>
						<xsl:attribute name="value">
							<xsl:text>true</xsl:text>
						</xsl:attribute>
						<xsl:if test="@groupName">
							<xsl:attribute name="data-wc-group">
								<xsl:value-of select="@groupName"/>
							</xsl:attribute>
						</xsl:if>
					</xsl:element>
				</span>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
