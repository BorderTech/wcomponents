<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		WDecoratedLabel allows a labelling element to contain up to three independently
		stylable areas. The output element of the label and its children is dependent
		upon the content model of the containing element and defaults to span.
	
		Child elements
		* ui:labelhead (0..1)
		* ui:labelbody (required, exactly 1)
		* ui:labeltail (0..1)

		Output the WDecoratedLabel
		param output: A HTML element name. Default 'span'
	-->
	<xsl:template match="ui:decoratedlabel">
		<xsl:param name="output" select="'span'"/>
		<xsl:param name="useId" select="1"/>
		<xsl:element name="{$output}">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="id">
					<xsl:choose>
						<xsl:when test="number($useId) eq 1">
							<xsl:value-of select="@id"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="''"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="live" select="'off'"/>
				<xsl:with-param name="isWrapper" select="1"/>
			</xsl:call-template>
			<xsl:apply-templates select="*">
				<xsl:with-param name="output" select="$output"/>
				<xsl:with-param name="useId" select="number($useId)"/>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
