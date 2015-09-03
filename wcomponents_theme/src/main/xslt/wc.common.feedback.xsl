<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<!--
		Feedback comprises ui:messageBox and ui:validationErrors.

		Generates the feedback container/message box, its title and the message list
		container then applies templates to generate the message list item(s).
	-->
	<xsl:template match="ui:messageBox|ui:validationErrors">
		<xsl:variable name="type">
			<xsl:choose>
				<xsl:when test="self::ui:validationErrors">
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
			<xsl:if test="$isDebug=1">
				<xsl:call-template name="debugAttributes"/>
				<xsl:call-template name="thisIsNotAllowedHere-debug">
					<xsl:with-param name="testForPhraseOnly" select="1"/>
					<xsl:with-param name="testForNoInteractive">
						<xsl:choose>
							<xsl:when test="self::ui:validationErrors">
								<xsl:number value="1"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="0"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<xsl:attribute name="class">
				<xsl:value-of select="concat(local-name(.), ' wc_msgbox ', $type)"/>
			</xsl:attribute>
			<xsl:element name="h1">
				<xsl:choose>
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
			</xsl:element>
			<xsl:element name="ul">
				<xsl:apply-templates/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
