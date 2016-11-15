<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<!--
		Generate the + and - buttons

		param isSingular: 1 if the template is called from a ui:multitextfield
			with no values. This allows us to determine the target element for
			some attribute and variable computation.
	-->
	<xsl:template name="multiFieldIcon">
		<xsl:param name="isSingular" select="0"/>
		<xsl:param name="myLabel"/>
		
		<xsl:variable name="id">
			<xsl:choose>
				<xsl:when test="self::ui:multitextfield">
					<xsl:value-of select="@id"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="../@id"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="toolTip">
			<xsl:choose>
				<xsl:when test="number($isSingular) eq 1 or position() eq 1">
					<xsl:text>{{t 'mfc_add'}}</xsl:text>
					<xsl:if test="$myLabel">
						<xsl:value-of select="$myLabel"/>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>{{t 'mfc_remove'}}</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<button type="button" title="{$toolTip}" class="wc_btn_icon wc-invite">
			<xsl:attribute name="aria-controls">
				<xsl:choose>
					<xsl:when test="self::ui:multitextfield or position() eq 1">
						<xsl:value-of select="$id"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($id, generate-id(), '-', position())"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test="self::ui:multitextfield">
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
