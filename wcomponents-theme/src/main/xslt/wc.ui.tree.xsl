<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.invalid.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>

	<xsl:template match="ui:tree">
		<xsl:variable name="isError" select="key('errorKey', @id)"/>


		<div role="tree">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="class">
					<xsl:if test="@htree">
						<xsl:text>wc_htree</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>

			<xsl:attribute name="aria-multiselectable">
				<xsl:choose>
					<xsl:when test="@multiple">
						<xsl:value-of select="@multiple"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>

			<xsl:call-template name="requiredElement"/>
			<xsl:call-template name="ajaxController"/>

			<xsl:apply-templates select="ui:margin"/>

			<xsl:if test="$isError">
				<xsl:call-template name="invalid"/>
			</xsl:if>

			<xsl:apply-templates select="ui:treeitem">
				<xsl:with-param name="disabled" select="@disabled"/>
			</xsl:apply-templates>

			<xsl:call-template name="inlineError">
				<xsl:with-param name="errors" select="$isError"/>
			</xsl:call-template>
			<xsl:call-template name="hField"/>
		</div>
	</xsl:template>
</xsl:stylesheet>
