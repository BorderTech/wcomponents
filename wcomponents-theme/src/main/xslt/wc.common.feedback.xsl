<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Feedback comprises ui:messagebox and ui:validationerrors.

		Generates the feedback container/message box, its title and the message list container then applies templates to
		generate the message list item(s).
	-->
	<xsl:template match="ui:messagebox|ui:validationerrors">
		<xsl:variable name="type">
			<xsl:choose>
				<xsl:when test="self::ui:validationerrors">
					<xsl:text>error</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@type"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="${wc.dom.html5.element.section}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:value-of select="concat(' wc_msgbox ', $type)"/>
				</xsl:with-param>
			</xsl:call-template>

			<h1>
				<span>
					<xsl:choose>
						<xsl:when test="@title">
							<xsl:value-of select="@title"/>
						</xsl:when>
						<xsl:when test="$type='error'">
							<xsl:value-of select="$$${wc.ui.messageBox.title.error}"/>
						</xsl:when>
						<xsl:when test="$type='warn'">
							<xsl:value-of select="$$${wc.ui.messageBox.title.warn}"/>
						</xsl:when>
						<xsl:when test="$type='info'">
							<xsl:value-of select="$$${wc.ui.messageBox.title.info}"/>
						</xsl:when>
						<xsl:when test="$type='success'">
							<xsl:value-of select="$$${wc.ui.messageBox.title.success}"/>
						</xsl:when>
					</xsl:choose>
				</span>
			</h1>
			<ul>
				<xsl:apply-templates />
			</ul>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
