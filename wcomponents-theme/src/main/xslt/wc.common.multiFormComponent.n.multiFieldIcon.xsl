<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<!--
		Generate the + and - buttons

		param isSingular: 1 if the template is called from a ui:multiTextField
			with no values. This allows us to determine the target element for
			some attribute and variable computation.
	-->
	<xsl:template name="multiFieldIcon">
		<xsl:param name="isSingular"/>
		<xsl:param name="myLabel"/>
		
		<xsl:variable name="id">
			<xsl:choose>
				<xsl:when test="self::ui:multiTextField">
					<xsl:value-of select="@id"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="../@id"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="toolTip">
			<xsl:choose>
				<xsl:when test="$isSingular=1 or position() = 1">
					<xsl:value-of select="$$${wc.ui.multiFormComponent.i18n.addControl.message}"/>
					<xsl:if test="$myLabel">
						<xsl:value-of select="$myLabel"/>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$$${wc.ui.multiFormComponent.i18n.removeControl.message}"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<button type="button" title="{$toolTip}">
			<xsl:attribute name="aria-controls">
				<xsl:choose>
					<xsl:when test="self::ui:multiTextField or position() = 1">
						<xsl:value-of select="$id"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($id, generate-id(), '-', position())"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test="self::ui:multiTextField">
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="isControl" select="1"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="isControl" select="1"/>
						<xsl:with-param name="field" select="parent::*"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</button>
	</xsl:template>
</xsl:stylesheet>
