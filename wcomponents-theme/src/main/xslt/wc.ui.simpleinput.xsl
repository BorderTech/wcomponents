<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>

	<xsl:template match="ui:textfield[@readOnly]|ui:phonenumberfield[@readOnly]|ui:emailfield[@readOnly]|ui:passwordfield[@readOnly]|ui:numberField[@readOnly]|ui:textarea[@readOnly]|ui:fileupload[@readOnly]">
		<xsl:call-template name="readOnlyControl">
			<xsl:with-param name="class" select="'wc-ro-input'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="ui:phonenumberfield[@readOnly and ./text()]|ui:emailfield[@readOnly and ./text()]">
		<xsl:variable name="href">
			<xsl:choose>
				<xsl:when test="self::ui:emailfield">
					<xsl:text>mailto:</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>tel:</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="."/>
		</xsl:variable>
		<a href="{$href}">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="class" select="'wc-ro-input'"/>
			</xsl:call-template>
			<xsl:call-template name="roComponentName"/>
			<xsl:value-of select="."/>
		</a>
	</xsl:template>

	<!-- Single line input controls which may be associated with a datalist. -->
	<xsl:template match="ui:textfield|ui:phonenumberfield|ui:emailfield|ui:passwordfield">
		<span>
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="class">
					<xsl:text>wc-input-wrapper</xsl:text>
					<xsl:if test="@list">
						<xsl:text> wc-combo</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:if test="@list">
				<xsl:attribute name="role">
					<xsl:text>combobox</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="aria-expanded">
					<xsl:text>false</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="data-wc-suggest">
					<xsl:value-of select="@list"/>
				</xsl:attribute>
				<xsl:attribute name="aria-autocomplete">
					<xsl:text>list</xsl:text>
				</xsl:attribute>
				<xsl:call-template name="title"/>
			</xsl:if>
			<xsl:element name="input">
				<xsl:call-template name="wrappedTextInputAttributes">
					<xsl:with-param name="type">
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
							<xsl:otherwise>
								<xsl:text>tel</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="useTitle">
						<xsl:choose>
							<xsl:when test="@list">
								<xsl:number value="0"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="1"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:attribute name="value">
					<xsl:value-of select="text()"/>
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="@list">
						<xsl:attribute name="role">
							<xsl:text>textbox</xsl:text>
						</xsl:attribute>
						<!-- every input that implements combo should have autocomplete turned off -->
						<xsl:attribute name="autocomplete">
							<xsl:text>off</xsl:text>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="@autocomplete">
						<xsl:attribute name="autocomplete">
							<xsl:value-of select="@autocomplete"/>
						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
				<xsl:if test="@size">
					<xsl:attribute name="size">
						<xsl:value-of select="@size"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@maxLength">
					<xsl:attribute name="maxlength">
						<xsl:value-of select="@maxLength"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@minLength">
					<xsl:attribute name="minlength">
						<xsl:value-of select="@minLength"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@pattern">
					<xsl:attribute name="pattern">
						<xsl:value-of select="@pattern"/>
					</xsl:attribute>
				</xsl:if>
			</xsl:element>
			<xsl:if test="@list">
				<button value="{concat(@id,'_input')}" tabindex="-1" id="{concat(@id, '_list')}" type="button" aria-hidden="true" class="wc_suggest wc_btn_icon wc-invite">
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="isControl" select="1"/>
					</xsl:call-template>
				</button>
			</xsl:if>
		</span>
	</xsl:template>

	<xsl:template match="ui:numberfield">
		<span>
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="class">
					<xsl:text>wc-input-wrapper</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:element name="input">
				<xsl:call-template name="wrappedTextInputAttributes">
					<xsl:with-param name="type">
						<xsl:text>number</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:attribute name="value">
					<xsl:value-of select="text()"/>
				</xsl:attribute>
				<!--
				Turning off autocomplete is CRITICAL in Internet Explorer (8, others untested, but those
				with a native HTML5 number field are probably going to be OK). It tooks me days to find this
				after tearing apart the entire framework. Here's the issue:
					In Internet Explorer the autocomplete feature on an input field causes the keydown event
					to be cancelled once there is something in the autocomplete list, i.e. once you have
					entered something into that field. So your event listeners are called with a cancelled
					event but you can find no code that cancels the event - very tricky to track down.
				
				TODO: check this in IE 11 and possibly implement autocomplete or move this attribute fix to JavaScript.
			-->
				<xsl:attribute name="autocomplete">
					<xsl:text>off</xsl:text>
				</xsl:attribute>
				<xsl:if test="@min">
					<xsl:attribute name="min">
						<xsl:value-of select="@min"/>
						<!-- NOTE: step may only be a non-integer if min is a non integer -->
						<xsl:if test="contains(@step,'.') and not(contains(@min,'.'))">
							<xsl:text>.0</xsl:text>
						</xsl:if>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@max">
					<xsl:attribute name="max">
						<xsl:value-of select="@max"/>
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
								<xsl:number value="number(@step)"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:if test="number($step) ne 0">
						<xsl:attribute name="step">
							<xsl:value-of select="$step"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
			</xsl:element>
		</span>
	</xsl:template>

	<!--
		WTextArea
		
		Note on maxLength:
		We deliberately bypass the browser native implementation of maxlength on
		textareas. This is to allow users to work in the textarea before submitting the page.
		For example a user may paste in a large body of text knowing that it is too long. The
		user should be allowed to do this and then work within the textarea to reduce the length
		before submitting.
		
		If the length of the textarea is constrained then the user would be forced to open another
		application (for example a text editor) paste the large text there, reduce the length of the
		text (without an immediate character count) and then paste into the textarea.
	-->
	<xsl:template match="ui:textarea">
		<xsl:variable name="tickerId" select="concat(@id,'_tick')"/>
		<span>
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="class">
					<xsl:text>wc-input-wrapper</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
			<textarea>
				<xsl:call-template name="wrappedTextInputAttributes"/>
				<xsl:if test="(@required or @placeholder) and not(text())">
					<xsl:attribute name="class">
						<xsl:text>wc-buggyie</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@maxLength">
					<xsl:attribute name="data-wc-maxlength">
						<xsl:value-of select="@maxLength"/>
					</xsl:attribute>
					<xsl:attribute name="aria-owns">
						<xsl:value-of select="$tickerId"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@minLength">
					<xsl:attribute name="data-wc-min">
						<xsl:value-of select="@minLength"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@cols">
					<xsl:attribute name="cols">
						<xsl:value-of select="@cols"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@rows">
					<xsl:attribute name="rows">
						<xsl:value-of select="@rows"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@autocomplete">
					<xsl:attribute name="autocomplete">
						<xsl:value-of select="@autocomplete"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="."/>
			</textarea>
			<xsl:if test="@maxLength">
				<output id="{$tickerId}" name="{$tickerId}" for="{@id}" hidden="hidden"></output>
			</xsl:if>
		</span>
	</xsl:template>

	<xsl:template match="ui:rtf"/>

	<xsl:template match="ui:fileupload">
		<span>
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="class">
					<xsl:text>wc-input-wrapper</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:element name="input">
				<xsl:call-template name="wrappedInputAttributes">
					<xsl:with-param name="type">
						<xsl:text>file</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:if test="@acceptedMimeTypes">
					<xsl:attribute name="accept">
						<xsl:value-of select="@acceptedMimeTypes"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@maxFileSize">
					<xsl:attribute name="data-wc-maxfilesize">
						<xsl:value-of select="@maxFileSize"/>
					</xsl:attribute>
				</xsl:if>
			</xsl:element>
		</span>
	</xsl:template>
</xsl:stylesheet>
