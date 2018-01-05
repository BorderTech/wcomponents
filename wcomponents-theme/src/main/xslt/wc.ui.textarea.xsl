<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>

	
	<xsl:template match="ui:textarea[@readOnly]">
		<xsl:variable name="element">
			<xsl:choose>
				<xsl:when test="ui:rtf">div</xsl:when>
				<xsl:otherwise>pre</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$element}">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="class" select="'wc-ro-input'"/>
			</xsl:call-template>
			<xsl:call-template name="roComponentName"/>
		</xsl:element>
		<xsl:choose>
			<xsl:when test="ui:rtf">
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates xml:space="preserve"/>
			</xsl:otherwise>
		</xsl:choose>
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
		<xsl:variable name="element">
			<xsl:choose>
				<xsl:when test="ui:rtf">
					<xsl:text>div</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>span</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$element}">
			<xsl:call-template name="commonInputWrapperAttributes"/>
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
				<xsl:value-of select="text()"/><!-- Note that a read-only RTF will actually contain nested HTML elements but that won't end up here -->
			</textarea>
			<xsl:if test="@maxLength">
				<output id="{$tickerId}" name="{$tickerId}" for="{@id}_input" hidden="hidden"></output>
			</xsl:if>
			<xsl:apply-templates select="ui:fieldindicator"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="ui:rtf"/>

</xsl:stylesheet>
