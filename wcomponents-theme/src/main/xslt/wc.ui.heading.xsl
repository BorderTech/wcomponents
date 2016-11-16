<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
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
		<xsl:variable name="labelText" >
			<xsl:value-of select="."/>
			<xsl:value-of select="ui:decoratedLabel//ui:image/@alt"/>
		</xsl:variable>
		<xsl:variable name="emptyHeading">
			<xsl:choose>
				<xsl:when test="normalize-space($labelText) eq ''">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{concat('h',@level)}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="number($emptyHeading) eq 1">
						<xsl:text>wc-error</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="ajaxTarget"/>
			<xsl:apply-templates />
			<xsl:if test="number($emptyHeading) eq 1">
				<xsl:text>{{t 'requiredLabel'}}</xsl:text>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
