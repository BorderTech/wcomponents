<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		ui:content is a child node of a number of components in its most basic form it
		merely passes through. Some components have their own content implementation:

		Generic template for unmoded content elements. Pass content through without any
		form of wrapper.
	-->
	<xsl:template match="ui:content">
		<xsl:param name="class"/>
		<xsl:param name="ajaxId"/>
		<xsl:param name="labelId" />
		<div>
			<xsl:if test="@id and @id != ''">
				<xsl:attribute name="id">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$ajaxId != ''">
				<xsl:attribute name="data-wc-ajaxalias">
					<xsl:value-of select="$ajaxId"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$labelId != ''">
				<xsl:attribute name="aria-describedby">
					<xsl:value-of select="$labelId"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional" select="$class"/>
			</xsl:call-template>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	
	<xsl:template match="ui:content" mode="passthru">
		<xsl:apply-templates/>
	</xsl:template>
</xsl:stylesheet>
