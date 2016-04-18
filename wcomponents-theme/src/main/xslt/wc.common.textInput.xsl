<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.required.xsl"/>
	<!--
		Single line input controls which may be associated with a datalist.
   -->
	<xsl:template match="ui:textfield|ui:phonenumberfield|ui:emailfield">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="myLabel" select="key('labelKey',$id)[1]"/>
		<xsl:choose>
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
				<span>
					<xsl:call-template name="commonAttributes">
						<xsl:with-param name="isWrapper" select="1"/>
						<xsl:with-param name="live" select="'off'"/>
						<xsl:with-param name="class">
							<xsl:text>wc_input_wrapper</xsl:text>
							<xsl:if test="@list">
								<xsl:text> wc_list_wrapper</xsl:text>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:element name="input">
						<xsl:attribute name="id">
							<xsl:value-of select="concat($id, '_input')"/>
						</xsl:attribute>
						<xsl:attribute name="type">
							<xsl:choose>
								<xsl:when test="self::ui:textfield">
									<xsl:text>text</xsl:text>
								</xsl:when>
								<xsl:when test="self::ui:emailfield">
									<xsl:text>email</xsl:text>
								</xsl:when>
								<xsl:when test="self::ui:phonenumberfield">
									<xsl:text>tel</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>text</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="name">
							<xsl:value-of select="@id"/>
						</xsl:attribute>
						<xsl:attribute name="value">
							<xsl:value-of select="."/>
						</xsl:attribute>
						<xsl:call-template name="title"/>
						<xsl:call-template name="requiredElement"/>
						<xsl:if test="$isError and $isError !=''">
							<xsl:call-template name="invalid"/>
						</xsl:if>
						<xsl:if test="@maxLength">
							<xsl:attribute name="maxLength">
								<xsl:value-of select="@maxLength"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="@required">
							<xsl:attribute name="placeholder">
								<xsl:value-of select="$$${wc.common.i18n.requiredPlaceholder}"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="isControl" select="1"/>
						</xsl:call-template>
						<xsl:variable name="list" select="@list"/>
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
				</span>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
