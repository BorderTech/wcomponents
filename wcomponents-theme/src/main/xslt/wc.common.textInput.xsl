<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.required.xsl"/>
	<!--
		Single line input controls which may be associated with a datalist.
   -->
	<xsl:template match="ui:textfield|ui:phonenumberfield|ui:emailfield|ui:passwordfield">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="myLabel" select="key('labelKey',$id)[1]"/>
		<xsl:choose>
			<xsl:when test="self::ui:passwordfield and @readOnly">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="label" select="$myLabel"/>
					<xsl:with-param name="applies" select="'none'"/><!-- NEVER allow text content to appear as it is a value. -->
				</xsl:call-template>
			</xsl:when>
			
			<xsl:when test="@readOnly">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="label" select="$myLabel"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="isError" select="key('errorKey',$id)"/>
				<xsl:if test="not($myLabel)">
					<xsl:call-template name="checkLabel">
						<xsl:with-param name="force" select="1"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:variable name="list" select="@list"/>
				<xsl:element name="input">
					<xsl:call-template name="commonControlAttributes">
						<xsl:with-param name="isError" select="$isError"/>
						<xsl:with-param name="name" select="@id"/>
						<xsl:with-param name="live" select="'off'"/>
						<xsl:with-param name="myLabel" select="$myLabel"/>
						<xsl:with-param name="value" select="text()"/>
					</xsl:call-template>
					<xsl:attribute name="type">
						<xsl:choose>
							<xsl:when test="self::ui:textfield">
								<xsl:text>text</xsl:text>
							</xsl:when>
							<xsl:when test="self::ui:emailfield">
								<xsl:text>email</xsl:text>
							</xsl:when>
							<xsl:when test="self::ui:passwordfield">
								<xsl:text>password</xsl:text>
							</xsl:when>
							<xsl:when test="self::ui:phonenumberfield">
								<xsl:text>tel</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>text</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:if test="not(self::ui:passwordfield)">
						<xsl:if test="@required">
							<xsl:attribute name="placeholder">
								<xsl:value-of select="$$${wc.common.i18n.requiredPlaceholder}"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="$list">
							<xsl:attribute name="role">
								<xsl:text>combobox</xsl:text>
							</xsl:attribute>
							<!-- every input that implements combo should have autocomplete turned off -->
							<xsl:attribute name="autocomplete">
								<xsl:text>off</xsl:text>
							</xsl:attribute>
							<xsl:attribute name="aria-owns">
								<xsl:value-of select="$list"/>
							</xsl:attribute>
							<xsl:variable name="suggestionList" select="//ui:suggestions[@id=$list]"/>
							<xsl:attribute name="aria-autocomplete">
								<xsl:choose>
									<xsl:when test="$suggestionList and $suggestionList/@autocomplete">
										<xsl:value-of select="$suggestionList/@autocomplete"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>both</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</xsl:if>
					</xsl:if>
					<xsl:if test="@size">
						<xsl:attribute name="size">
							<xsl:value-of select="@size"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@maxLength">
						<xsl:attribute name="maxLength">
							<xsl:value-of select="@maxLength"/>
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
				</xsl:element>
				
				<xsl:call-template name="inlineError">
					<xsl:with-param name="errors" select="$isError"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
