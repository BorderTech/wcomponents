
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
<!--
		Transform for WDefinitionList. This is a pretty straightforwards implementation of a HTML definition list.

		The actual layout of the DT and DD descendants depends on the value of the type attribute.
	-->
	<xsl:template match="ui:definitionlist">
		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:if test="@type">
				<xsl:value-of select="concat(' wc-definitionlist-type-', @type)"/>
			</xsl:if>
			<xsl:apply-templates select="ui:margin" mode="asclass"/>
		</xsl:variable>
		<dl id="{@id}" class="{normalize-space(concat('wc-definitionlist ', $additional))}">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="ui:term"/>
		</dl>
	</xsl:template>
	<!--
		in WDefinition List there are a series of terms which contain their data. The dt elements are determined by an attribute and their data are
		child elements

		**NOTE:** This is a major departure in the structure of a HTML definition list and should be reviewed.
	-->
	<xsl:template match="ui:term">
		<dt>
			<xsl:value-of select="@text"/>
		</dt>
		<xsl:apply-templates select="ui:data"/>
	</xsl:template>
	<!-- The data items in a defintion list map directly to HTML dd elements -->
	<xsl:template match="ui:data">
		<dd>
			<xsl:apply-templates />
		</dd>
	</xsl:template>
</xsl:stylesheet>
