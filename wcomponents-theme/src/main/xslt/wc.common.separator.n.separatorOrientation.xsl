<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		If a separator has a direction/orientation other than horizontal it must have
		the aria-orientation attribute set to "vertical".
	-->
	
	<xsl:template name="separatorOrientation">
		<xsl:variable name="sepV" select="'vertical'"/>
		<xsl:variable name="orientation">
			<!-- note: 
				ui:menuGroup cannot be an immediate child of ui:menuGroup so 
				the separator is at the top level of a menu if
				1. it is a WSeparator and its parent is a WMenu; or
				2. if it is called from ui:menuGroup and that WMenuGroup's parent is a WMenu or
				3. it is a WSeparator with a WMenuGroup parent and that WMenuGroup's parent is a WMenu.
			-->
			<xsl:if test="parent::ui:menu or parent::ui:menuGroup[parent::ui:menu]">
				<xsl:variable name="menuType" select="ancestor::ui:menu[1]/@type"/>
				<xsl:if test="$menuType='bar' or $menuType='flyout'">
					<xsl:value-of select="$sepV"/>
				</xsl:if>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="$orientation=$sepV">
			<xsl:attribute name="aria-orientation">
				<xsl:value-of select="$sepV"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	
	<!--
		If you really want to return support for ui:separator in tabsets then you might want to use the following 
		transform instead:
	-\->
		
	<xsl:template name="separatorOrientation">
		<xsl:variable name="sepV" select="'vertical'"/>
		<xsl:variable name="orientation">
			<xsl:choose>
				<!-\-
					note: ui:tabGroup can contain only ui:tab or ui:separator so 
					the separator is at the top level of a tabset if
					1. it is a WSeparator and its parent is a WTabSet or WTabGroup;
				-\->
				<xsl:when test="parent::ui:tabset/@type='top' or parent::ui:tabGroup[../@type='top']">
					<xsl:value-of select="$sepV"/>
				</xsl:when>
				<!-\- note: 
					ui:menuGroup cannot be an immediate child of ui:menuGroup so 
					the separator is at the top level of a menu if
					1. it is a WSeparator and its parent is a WMenu; or
					2. if it is called from ui:menuGroup and that WMenuGroup's parent is a WMenu or
					3. it is a WSeparator with a WMenuGroup parent and that WMenuGroup's parent is a WMenu.
				-\->
				<xsl:when test="parent::ui:menu or parent::ui:menuGroup[parent::ui:menu]">
					<xsl:variable name="menuType" select="ancestor::ui:menu[1]/@type"/>
					<xsl:if test="$menuType='bar' or $menuType='flyout'">
						<xsl:value-of select="$sepV"/>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="$orientation=$sepV">
			<xsl:attribute name="aria-orientation">
				<xsl:value-of select="$sepV"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	-->
</xsl:stylesheet>
