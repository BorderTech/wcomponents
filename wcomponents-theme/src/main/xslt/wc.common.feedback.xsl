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
					<xsl:text>wc_msgbox</xsl:text>
					<xsl:if test="self::ui:validationerrors">
						<xsl:text> wc-messagebox-type-error</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>

			<h1 class="wc-icon">
				<span>
					<xsl:choose>
						<xsl:when test="@title">
							<xsl:value-of select="@title"/>
						</xsl:when>
						<xsl:when test="$type='error'">
							<xsl:text>{{t '${wc.ui.messageBox.title.error}'}}</xsl:text>
						</xsl:when>
						<xsl:when test="$type='warn'">
							<xsl:text>{{t '${wc.ui.messageBox.title.warn}'}}</xsl:text>
						</xsl:when>
						<xsl:when test="$type='info'">
							<xsl:text>{{t '${wc.ui.messageBox.title.info}'}}</xsl:text>
						</xsl:when>
						<xsl:when test="$type='success'">
							<xsl:text>{{t '${wc.ui.messageBox.title.success}'}}</xsl:text>
						</xsl:when>
					</xsl:choose>
				</span>
			</h1>
			<div class="wc_messages">
				<xsl:apply-templates />
			</div>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
