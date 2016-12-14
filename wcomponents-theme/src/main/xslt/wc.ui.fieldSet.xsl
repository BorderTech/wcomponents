<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.invalid.xsl"/>
	<xsl:import href="wc.common.required.xsl"/>
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.common.offscreenSpan.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Transform for ui:fieldset which is the XML output of WFieldSet.
	-->
	<xsl:template match="ui:fieldset">
		<xsl:variable name="frame">
			<xsl:value-of select="@frame"/>
		</xsl:variable>
		<!--
			The fieldset cannot be in an error state since it is merely a container.
			However, if a ui:validationError exists with a ui:error for the fieldset then
			it is assumed that any component of the fieldset may be in an error state and
			an error indicator is place inside the fieldset after all other content. The
			fieldset is also styled as if it was in an error state.
		-->
		<xsl:variable name="isError" select="key('errorKey',@id)"/>
		<fieldset>
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="isWrapper" select="1"/>
				<xsl:with-param name="class">
					<xsl:if test="$frame eq 'noborder' or $frame eq 'none'">
						<xsl:text>wc_noborder</xsl:text>
					</xsl:if>
					<xsl:if test="@required">
						<xsl:text> wc_req</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:if test="$isError">
				<xsl:call-template name="invalid"/>
			</xsl:if>
			<xsl:variable name="labelText" >
				<xsl:value-of select="ui:decoratedlabel"/>
				<xsl:value-of select="ui:decoratedLabel//ui:image/@alt"/>
			</xsl:variable>
			<xsl:variable name="emptyLegend">
				<xsl:choose>
					<xsl:when test="normalize-space($labelText) eq ''">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<legend>
				<xsl:if test="$frame eq 'notext' or $frame eq 'none'">
					<xsl:attribute name="class">
						<xsl:choose>
							<xsl:when test="number($emptyLegend) eq 1">
								<xsl:text>wc-error</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>wc-off</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:if>
				<xsl:call-template name="accessKey"/>
				<xsl:apply-templates select="ui:decoratedlabel"/>
				<xsl:if test="number($emptyLegend) eq 1">
					<xsl:text>{{t 'requiredLabel'}}</xsl:text>
				</xsl:if>
				<xsl:if test="@required">
					<xsl:call-template name="offscreenSpan">
						<xsl:with-param name="text">
							<xsl:text>{{t 'requiredPlaceholder'}}</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:if>
			</legend>

			<xsl:apply-templates select="ui:content" mode="passthru"/>
			<xsl:if test="$isError">
				<xsl:call-template name="inlineError">
					<xsl:with-param name="errors" select="$isError"/>
				</xsl:call-template>
			</xsl:if>
		</fieldset>
	</xsl:template>
</xsl:stylesheet>
