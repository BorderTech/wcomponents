<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.required.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.missingLabel.xsl"/>
	<!--
		Single line input controls
		
		Transform of:
		* WTextField
		* WPhoneNumberField
		* WEmailField
		* WNumberField
		* WPasswordField

		Outputs the HTML for the components based on read only status and the component
		being transformed. The majority of the transform for each of these components
		is identical, hence their agglomeration here.
   -->
	<xsl:template match="ui:textfield|ui:phonenumberfield|ui:emailfield|ui:numberfield|ui:passwordfield">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="type">
			<xsl:choose>
				<xsl:when test="self::ui:textfield">
					<xsl:text>text</xsl:text>
				</xsl:when>
				<xsl:when test="self::ui:numberfield">
					<xsl:text>number</xsl:text>
				</xsl:when>
				<xsl:when test="self::ui:passwordfield">
					<xsl:text>password</xsl:text>
				</xsl:when>
				<xsl:when test="self::ui:emailfield">
					<xsl:text>email</xsl:text>
				</xsl:when>
				<xsl:when test="self::ui:phonenumberfield">
					<xsl:text>tel</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="readOnly">
			<xsl:if test="@readOnly">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="myLabel" select="key('labelKey',$id)[1]"/>
		<xsl:choose>
			<xsl:when test="$readOnly=1">
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
				<xsl:element name="input">
					<xsl:call-template name="commonControlAttributes">
						<xsl:with-param name="isError" select="$isError"/>
						<xsl:with-param name="name" select="$id"/>
						<xsl:with-param name="live" select="'off'"/>
						<xsl:with-param name="myLabel" select="$myLabel[1]"/>
					</xsl:call-template>
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
					<xsl:attribute name="type">
						<xsl:value-of select="$type"/>
					</xsl:attribute>
					<xsl:attribute name="value">
						<xsl:value-of select="."/>
					</xsl:attribute>
					<xsl:if test="@size and not(self::ui:numberfield)">
						<xsl:attribute name="size">
							<xsl:value-of select="@size"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="self::ui:numberfield">
						<!--
							Turning off autocomplete is CRITICAL in Internet Explorer (8, others untested, but those
							with a native HTML5 number field are probably going to be OK). It tooks me days to find this
							after tearing apart the entire framework. Here's the issue:
								In Internet Explorer the autocomplete feature on an input field causes the keydown event
								to be cancelled once there is something in the autocomplete list, i.e. once you have
								entered something into that field. So your event listeners are called with a cancelled
								event but you can find no code that cancels the event - very tricky to track down.
						-->
						<xsl:attribute name="autocomplete">
							<xsl:text>off</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="@minLength">
							<xsl:attribute name="${wc.ui.textField.attrib.minLength}">
								<xsl:value-of select="@minLength"/>
							</xsl:attribute>
						</xsl:when>
						<xsl:when test="@min">
							<xsl:attribute name="min">
								<xsl:value-of select="@min"/>
								<!-- NOTE: step may only be a non-integer if min is a non integer -->
								<xsl:if test="contains(@step,'.') and not(contains(@min,'.'))">
									<xsl:text>.0</xsl:text>
								</xsl:if>
							</xsl:attribute>
						</xsl:when>
					</xsl:choose>
					<xsl:if test="@max">
						<xsl:attribute name="max">
							<xsl:value-of select="@max"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@pattern">
						<xsl:attribute name="pattern">
							<xsl:value-of select="@pattern"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@step">
						<!-- NOTE: if min is not defined step must be an integer and step may not be 0-->
						<xsl:variable name="step">
							<xsl:choose>
								<xsl:when test="not(@min) and contains(@step,'.')">
									<xsl:number value="round(number(@step))"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="number(@step)"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:if test="$step != '' and $step != 0">
							<xsl:attribute name="step">
								<xsl:value-of select="$step"/>
							</xsl:attribute>
						</xsl:if>
					</xsl:if>
				</xsl:element>
				<xsl:call-template name="inlineError">
					<xsl:with-param name="errors" select="$isError"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
