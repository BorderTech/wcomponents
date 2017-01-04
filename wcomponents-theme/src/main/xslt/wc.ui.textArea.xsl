<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	<!--
		Simple transform to textarea.

		Note on maxLength:
		We deliberately bypass the browser native implementation of maxlength on
		textareas. This is to allow users to work in the textarea before submitting the page.
		For example a user may paste in a large body of text knowing that it is too long. The
		user should be allowed to do this and then work within the textarea to reduce the length
		before submitting.

		If the length of the textarea is constrained then the user would be forced to open another
		application (for example a text editor) paste the large text there, reduce the length of the
		text (without an immediate character count) and then paste into the textarea.
		The HTML5 browsers have it wrong, we have it right.
	-->
	<xsl:template match="ui:textarea">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="tickerId" select="concat(@id,'_tick')"/>
		<xsl:choose>
			<xsl:when test="@readOnly">
				<xsl:call-template name="readOnlyControl"/>
			</xsl:when>
			<xsl:otherwise>
				<span>
					<xsl:call-template name="commonAttributes">
						<xsl:with-param name="class">
							<xsl:text>wc_input_wrapper</xsl:text>
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
						<output id="{$tickerId}" name="{$tickerId}" for="{@id}" hidden="hidden" />
						<!--<xsl:element name="output">
							<xsl:attribute name="id">
								<xsl:value-of select="$tickerId"/>
							</xsl:attribute>
							<xsl:attribute name="name">
								<xsl:value-of select="$tickerId"/>
							</xsl:attribute>
							<xsl:attribute name="for">
								<xsl:value-of select="@id"/>
							</xsl:attribute>
							<xsl:call-template name="hiddenElement"/>
						</xsl:element>-->
					</xsl:if>
				</span>
				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="ui:rtf"/>
</xsl:stylesheet>
