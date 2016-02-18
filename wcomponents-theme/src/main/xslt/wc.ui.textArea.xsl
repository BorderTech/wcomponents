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
		<xsl:variable name="readOnly">
			<xsl:if test="@readOnly=$t">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="tickerId" select="concat(@id,'${wc.ui.maxLength.ticker.id.suffix}')"/>
		<xsl:variable name="myLabel" select="key('labelKey',$id)"/>
		<xsl:choose>
			<xsl:when test="$readOnly=1">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="label" select="$myLabel[1]"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="isError" select="key('errorKey',$id)"/>
				<xsl:if test="not($myLabel)">
					<xsl:call-template name="checkLabel">
						<xsl:with-param name="force" select="1"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:element name="textarea">
					<xsl:call-template name="commonControlAttributes">
						<xsl:with-param name="isError" select="$isError"/>
						<xsl:with-param name="name" select="$id"/>
						<xsl:with-param name="live" select="'off'"/>
						<xsl:with-param name="myLabel" select="$myLabel[1]"/>
					</xsl:call-template>
					<xsl:if test="@maxLength">
						<xsl:attribute name="${wc.ui.maxLength.attribute.maxlength}">
							<xsl:value-of select="@maxLength"/>
						</xsl:attribute>
						<xsl:attribute name="aria-owns">
							<xsl:value-of select="$tickerId"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@minLength">
						<xsl:attribute name="${wc.common.attrib.min}">
							<xsl:value-of select="@minLength"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@required=$t">
						<xsl:attribute name="placeholder">
							<xsl:value-of select="$$${wc.common.i18n.requiredPlaceholder}"/>
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
					<xsl:value-of select="."/>
				</xsl:element>
				<xsl:if test="@maxLength">
					<xsl:variable name="chrs" select="@maxLength - string-length(text())"/>
					<xsl:element name="output">
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
						<!-- This is surely implicit on an output element.
						<xsl:call-template name="setARIALive"/>
						-->
						<xsl:if test="string-length(text()) &gt; @maxLength">
						<xsl:attribute name="class">
							<xsl:text>wc_error</xsl:text>
						</xsl:attribute>
						</xsl:if>
					</xsl:element>
				</xsl:if>
				<xsl:call-template name="inlineError">
					<xsl:with-param name="errors" select="$isError"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
