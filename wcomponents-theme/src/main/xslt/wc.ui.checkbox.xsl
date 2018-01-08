<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.icon.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>

	<xsl:template match="ui:checkbox|ui:radiobutton">
		<span>
			<xsl:call-template name="commonInputWrapperAttributes"/>
			<xsl:element name="input">
				<xsl:call-template name="wrappedInputAttributes">
					<xsl:with-param name="type">
						<xsl:choose>
							<xsl:when test="self::ui:checkbox">
								<xsl:text>checkbox</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>radio</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:if test="@selected">
					<xsl:attribute name="checked">
						<xsl:text>checked</xsl:text>
					</xsl:attribute>
				</xsl:if>

				<xsl:choose>
					<xsl:when test="self::ui:checkbox">
						<xsl:attribute name="value">
							<xsl:text>true</xsl:text>
						</xsl:attribute>
						<xsl:if test="@groupName">
							<xsl:attribute name="data-wc-group">
								<xsl:value-of select="@groupName"/>
							</xsl:attribute>
						</xsl:if>
					</xsl:when>
					<xsl:when test="@value">
						<xsl:attribute name="value">
							<xsl:value-of select="@value"/>
						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
			</xsl:element>

			<xsl:choose>
				<xsl:when test="self::ui:checkbox">
					<xsl:apply-templates select="ui:fieldindicator"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="hField">
						<xsl:with-param name="name" select="@groupName"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</span>
	</xsl:template>
	
	<xsl:template match="ui:checkbox[@readOnly]|ui:radiobutton[@readOnly]">
		<span>
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="class">
					<xsl:text>wc-ro-input</xsl:text>
					<xsl:if test="@selected">
						<xsl:text> wc_ro_sel</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:attribute name="title">
				<xsl:choose>
					<xsl:when test="@selected">
						<xsl:text>{{#i18n}}input_selected{{/i18n}}</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>{{#i18n}}input_unselected{{/i18n}}</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:call-template name="roComponentName"/>
			<xsl:attribute name="data-wc-value">
				<xsl:choose>
					<xsl:when test="@selected">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:call-template name="icon">
				<xsl:with-param name="class">
					<xsl:choose>
						<xsl:when test="self::ui:checkbox and @selected">fa-check-square-o</xsl:when>
						<xsl:when test="self::ui:checkbox">fa-square-o</xsl:when>
						<xsl:when test="@selected">fa-dot-circle-o</xsl:when>
						<xsl:otherwise>fa-circle-o</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
		</span>
	</xsl:template>
</xsl:stylesheet>
