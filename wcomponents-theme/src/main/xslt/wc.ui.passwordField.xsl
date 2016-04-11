<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.readOnly.xsl"/>
	<!--
		Single line input controls
		
		Transform of:
		* WTextField
		* WPhoneNumberField
		* WEmailField
		* WPasswordField

		Outputs the HTML for the components based on read only status and the component
		being transformed. The majority of the transform for each of these components
		is identical, hence their agglomeration here.
   -->
	<xsl:template match="ui:passwordfield">
		<xsl:variable name="myLabel" select="key('labelKey',@id)[1]"/>
		<xsl:choose>
			<xsl:when test="@readOnly">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="label" select="$myLabel"/>
					<xsl:with-param name="applies" select="'none'"/><!-- NEVER allow text content to appear as it may be assumed to be a value. -->
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="isError" select="key('errorKey',@id)"/>
				<xsl:if test="not($myLabel)">
					<xsl:call-template name="checkLabel">
						<xsl:with-param name="force" select="1"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:element name="input">
					<xsl:call-template name="commonControlAttributes">
						<xsl:with-param name="isError" select="$isError"/>
						<xsl:with-param name="name" select="@id"/>
						<xsl:with-param name="value" select="''"/>
						<xsl:with-param name="live" select="'off'"/>
						<xsl:with-param name="myLabel" select="$myLabel"/>
					</xsl:call-template>
					<xsl:attribute name="type">
						<xsl:text>password</xsl:text>
					</xsl:attribute>
					<xsl:if test="@maxLength">
						<xsl:attribute name="maxLength">
							<xsl:value-of select="@maxLength"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@size">
						<xsl:attribute name="size">
							<xsl:value-of select="@size"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@minLength">
						<xsl:attribute name="${wc.ui.textField.attrib.minLength}">
							<xsl:value-of select="@minLength"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@pattern">
						<xsl:attribute name="pattern">
							<xsl:value-of select="@pattern"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="not($myLabel)">
						<xsl:call-template name="ariaLabel"/>
					</xsl:if>
				</xsl:element>
				<xsl:call-template name="inlineError">
					<xsl:with-param name="errors" select="$isError"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
