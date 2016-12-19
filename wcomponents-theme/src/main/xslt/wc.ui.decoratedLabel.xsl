<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!--
		WDecoratedLabel allows a labelling element to contain up to three independently stylable areas. The output element of the label and its
		children is dependent upon the content model of the containing element and defaults to span.
	
		param output: A HTML element name. Default 'span'
		param useId: int if not 1 then the id of the WDecoratedLabel is not output. This is because we sometimes have to reproduce the WDecoratedLabel
		more than once in a UI (to make a submenu openbutton and the content of a submenu close button for example).
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
				<xsl:with-param name="isWrapper" select="1"/>
			</xsl:call-template>
			<xsl:apply-templates select="*">
				<xsl:with-param name="output" select="$output"/>
				<xsl:with-param name="useId" select="number($useId)"/>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>	
	<!--
		param output: an HTML element of this name is created and the content placed inside
		param useId: int if not 1 then the id of the segment is not output (see above).
	-->
	<xsl:template match="ui:labelbody|ui:labelhead|ui:labeltail">
		<xsl:param name="output" select="'span'"/>
		<xsl:param name="useId" select="1"/>
		<xsl:element name="{$output}">
			<xsl:if test="number($useId) eq 1">
				<xsl:attribute name="id">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:text> wc_dlbl_seg</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
