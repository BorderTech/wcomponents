<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Transform for WHeading. This is a fairly straightforwards 1:1 match with a HTML
		H# element where the WHeading @level sets the #
	
		Child elements
		* ui:decoratedlabel (minOccurs 0)
		If the heading does not have a WDecoratedLabel then its text content is used
		as the text in the HTML heading element. If the content is mixed only the
		WDecoratedLabel	is output (this is actually not possible in the Java API so
		it not as draconian as it appears).
	-->
	<xsl:template match="ui:heading">
		<xsl:element name="{concat('h',@level)}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:call-template name="ajaxTarget"/>
			<xsl:apply-templates select="ui:margin"/>
			<xsl:choose>
				<xsl:when test="ui:decoratedlabel">
					<xsl:apply-templates select="ui:decoratedlabel"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
