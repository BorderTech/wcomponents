<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.imageEdit.xsl"/>
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Transform for WImage. Simple 1:1 map with HTML IMG element
	-->
	<xsl:template match="ui:image">
		<xsl:element name="img">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:value-of select="local-name(.)"/>
				<xsl:if test="@class">
					<xsl:value-of select="concat(' ', @class)"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:attribute name="src">
				<xsl:value-of select="@src"/>
			</xsl:attribute>
			<xsl:attribute name="alt">
				<xsl:value-of select="@alt"/>
			</xsl:attribute>
			<xsl:if test="@width">
				<xsl:attribute name="width">
					<xsl:value-of select="@width"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@height">
				<xsl:attribute name="height">
					<xsl:value-of select="@height"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:call-template name="ajaxTarget"/>
		</xsl:element>
		<xsl:call-template name="imageEditButton">
			<xsl:with-param name="text">
				<xsl:text>Edit</xsl:text><!-- TODO i18n -->
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
