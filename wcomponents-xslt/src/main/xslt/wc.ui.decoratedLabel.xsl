
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!--
		WDecoratedLabel allows a labelling element to contain up to three independently stylable areas. The output element of the label and its
		children is dependent upon the content model of the containing element and defaults to span.

		param output: A HTML element name. Default 'span'
		param useId: int if not 1 then the id of the WDecoratedLabel is not output. This is because we sometimes have to reproduce the WDecoratedLabel
		more than once in a UI (to make a submenu openbutton and the content of a submenu close button for example).
	-->
	<xsl:template match="ui:decoratedlabel">
		<xsl:param name="output" select="'span'"/>
		<xsl:element name="{$output}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id" />
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>wc-decoratedlabel</xsl:text>
				<xsl:if test="@type">
					<xsl:value-of select="concat(' wc-decoratedlabel-type-', @type)"/>
				</xsl:if>
				<xsl:if test="@class">
					<xsl:value-of select="concat(' ', @class)"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="*">
				<xsl:with-param name="output" select="$output"/>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	<!--
		param output: an HTML element of this name is created and the content placed inside
		param useId: int if not 1 then the id of the segment is not output (see above).
	-->
	<xsl:template match="ui:labelbody|ui:labelhead|ui:labeltail">
		<xsl:param name="output" select="'span'"/>
		<xsl:element name="{$output}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:value-of select="concat('wc-', local-name(), ' wc_dlbl_seg')"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
