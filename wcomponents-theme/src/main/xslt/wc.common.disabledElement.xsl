<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Common helper template for marking a component as disabled. Used by all components which may be disabled. 
		
		################################################################################
		This template must NEVER be excluded.
		################################################################################
		
		param isControl default 0
		This determines if we use the disabled or aria-disabled attribute to mark the component as disabled. If true the
		disabled attribute is set; this may only be set on components which output a form control or fieldset element.

		param field default = '.'
		The component to disable, ususally though not always, the current node. See wc.ui.menuItem.xsl or 
		wc.ui.submenu.xsl for extreme cases where the component being disabled may not be the component which has the 
		@disabled attribute.
	-->
	<xsl:template name="disabledElement">
		<xsl:param name="isControl" select="0"/>
		<xsl:param name="field" select="."/>
		
		<xsl:if test="$field/@disabled">
			<xsl:choose>
				<xsl:when test="$isControl=1">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="aria-disabled">
						<xsl:copy-of select="$t"/>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
