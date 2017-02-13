<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!-- Transform for WMenu. Menus may not be nested. -->
	<xsl:template match="ui:menu">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="type" select="@type"/>
		<xsl:variable name="isBarFlyout">
			<xsl:choose>
				<xsl:when test="$type eq 'bar' or $type eq 'flyout'">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<div>
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="class">
					<xsl:if test="number($isBarFlyout) eq 1">wc_menu_bar</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:attribute name="role">
				<xsl:choose>
					<xsl:when test="number($isBarFlyout) eq 1">
						<xsl:text>menubar</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>menu</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:apply-templates select="*[not(self::ui:margin)]"/>
		</div>
	</xsl:template>

	<xsl:template match="ui:separator">
		<xsl:element name="hr">
			<xsl:attribute name="role">
				<xsl:text>separator</xsl:text>
			</xsl:attribute>
			<xsl:if test="@class">
				<xsl:attribute name="class">
					<xsl:value-of select="@class"/>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<!--
		Transform for WMenuItemGroup. It is strongly recommended that you do not use this component but use WSubMenu or WSeparator instead.
		
		The rationale for this transform is from the WAI-ARIA authoing practices for a Menu Widget: http://www.w3.org/TR/wai-aria-practices/#menu:
		
		"Grouping of menuitems in a menu or menubar is performed by introducing an element with the role of separator. A separator delineates groups
		of menu items within a menu or menubar. A separator is not placed in the navigation order and it is in no way interactive. Authors provide an
		aria-orientation consistent with the separator's orientation in the menu or menubar. Since the separator is not navigable it does not support
		the aria-expanded state. It is recommended that when grouping menu items of type menuitemradio that they all be grouped together preceding or
		following a separator."
	-->
	<xsl:template match="ui:menugroup">
		<hr role="separator"/>
		<xsl:apply-templates select="*[not(self::ui:decoratedlabel)]"/><!-- The WDecoratedLabel is purposely ignored -->
		<hr role="separator"/>
	</xsl:template>
</xsl:stylesheet>
