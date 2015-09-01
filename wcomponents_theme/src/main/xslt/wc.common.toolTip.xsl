<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<!--
		Common helper template to create "toolTip" style accesskey markers. This
		template must never be excluded.

		This template is most often called from wc.common.accessKey.xsl but may
		be called directly from a component if there is a need to separate the
		determination of the accesskey attribute from the output of the tooltip.

		param accessKey: default @accessKey
		The character used as the content of the balloon, this must be a single
		uppercase letter or digit

		param id: default @id
		The id of the component which has the accesskey attribute, used to
		reference that component when generating an ID for the tooltip balloon
	-->
	<xsl:template name="tooltip">
		<xsl:param name="ttAccessKey" select="@accessKey"/>
		<xsl:param name="id" select="@id"/>
		<xsl:if test="$ttAccessKey!=''">
			<xsl:element name="span">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
					<xsl:text>${wc.ui.accesskey.id.suffix}</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="role">
					<xsl:text>tooltip</xsl:text>
				</xsl:attribute>
				<xsl:call-template name="hiddenElement"/>
				<xsl:value-of select="$ttAccessKey"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
