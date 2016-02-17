<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.title.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Common helper template to output the readOnly state of many form control components.
		This template must never be excluded.

		Implementing components
			wc.ui.checkableInput.xsl
			wc.ui.checkableSelect.xsl
			wc.ui.dropdown.xsl
			wc.ui.fileUpload.xsl
			wc.ui.multiFormComponent.xsl
			wc.ui.textInput.xsl
			wc.ui.textArea.xsl

		The "read only" state of a WComponent does not translate to the "read only" state
		of a HTML input. Within WComponents "readOnly" is a text-equivalent rendering of
		a component which is used to output the component's value but not in a form
		which can be used to manipulate that value. It is, therefore, possible to have
		a read-only rendering of controls which do not support the HTML input element's
		readonly attribute, such as a checkbox or even a fieldset.

		This helper does not aim to produce the readOnly state of all components but is
		a helper for a group of components which have a single text value, such as
		single select components and text input equivalents.

		param class: Any className which may need to be added to the output element.
		param applies: Nodelist to apply (if any).
		param style: String to convert to an inline style attribute
		param useReadOnlyMode: If set to number 1 then any apply-templates will use
			mode="readOnly"
		param toolTip: Text used as an explicit toolTip rather than using @toolTip.
			This is unusual and is currently only implemented by WCheckBox and
			WRadioButton.
	-->
	<xsl:template name="readOnlyControl">
		<xsl:param name="class" select="''"/>
		<xsl:param name="style" select="''"/>
		<xsl:param name="applies" select="''"/>
		<xsl:param name="useReadOnlyMode"/>
		<xsl:param name="toolTip" select="''"/>
		<xsl:param name="label"/>

		<xsl:variable name="linkWithText">
			<xsl:if test="text() and (self::ui:phonenumberfield or self::ui:emailfield)">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="elementName">
			<xsl:choose>
				<xsl:when test="self::ui:textarea">
					<!--
						This is really only needed by IE due to it stripping whitepace in other elements when we use
						htmlToDocumentElement in JavaScript. See wc/xml/xslTransform.js, PRE does not cause issues in
						other UA so we don't bother with an IE test.
					-->
					<xsl:text>pre</xsl:text>
				</xsl:when>
				<xsl:when test="$linkWithText=1">
					<xsl:text>a</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>span</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$elementName}">
			<xsl:call-template name="commonAttributes"/>
			<xsl:call-template name="title">
				<xsl:with-param name="title" select="$toolTip"/>
			</xsl:call-template>
			<xsl:attribute name="class">
				<xsl:call-template name="commonClassHelper"/>
				<xsl:text> wc_ro</xsl:text>
				<xsl:if test="$class != ''">
					<xsl:value-of select="concat(' ', $class)"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test="$style!=''">
				<xsl:attribute name="style">
					<xsl:value-of select="$style"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$label">
				<xsl:attribute name="aria-labelledby">
					<xsl:value-of select="$label/@id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$linkWithText=1">
				<xsl:attribute name="href">
					<xsl:choose>
						<xsl:when test="self::ui:emailfield">
							<xsl:text>mailto:</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>tel:</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$applies!='none'">
				<xsl:choose>
					<xsl:when test="$applies!='' and $useReadOnlyMode=1">
						<xsl:apply-templates select="$applies" mode="readOnly"/>
					</xsl:when>
					<xsl:when test="$applies!=''">
						<xsl:apply-templates select="$applies"/>
					</xsl:when>
					<xsl:when test="$useReadOnlyMode=1">
						<xsl:apply-templates select="*" mode="readOnly"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
