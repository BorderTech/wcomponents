<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		If a separator has a direction/orientation other than horizontal it must have
		the aria-orientation attribute set to "vertical".
	-->
	
	<xsl:template name="separatorOrientation">
		<xsl:variable name="sepV" select="'vertical'"/>
		<xsl:variable name="orientation">
			<!-- note: 
				ui:menugroup cannot be an immediate child of ui:menugroup so 
				the separator is at the top level of a menu if
				1. it is a WSeparator and its parent is a WMenu; or
				2. if it is called from ui:menugroup and that WMenuGroup's parent is a WMenu or
				3. it is a WSeparator with a WMenuGroup parent and that WMenuGroup's parent is a WMenu.
			-->
			<xsl:if test="parent::ui:menu or parent::ui:menugroup[parent::ui:menu]">
				<xsl:variable name="menuType" select="ancestor::ui:menu[1]/@type"/>
				<xsl:if test="$menuType eq 'bar' or $menuType eq 'flyout'">
					<xsl:value-of select="$sepV"/>
				</xsl:if>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="$orientation eq $sepV">
			<xsl:attribute name="aria-orientation">
				<xsl:value-of select="$sepV"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
