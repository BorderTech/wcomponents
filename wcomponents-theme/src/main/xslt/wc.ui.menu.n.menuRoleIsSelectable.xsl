<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		This template is used to determine if a WMenuItem or WSubMenu (if
		supported) should be selectable based on its selectable property, or
		ancestry the params are passed in because they are expensive to 
		calculate. See the calling templates for details. 
		
		It is extremely unlikely that this template would be overridden.
	-->
	<xsl:template name="menuRoleIsSelectable">
		<xsl:param name="type"/>
		<xsl:param name="myAncestorMenu"/>
		<xsl:param name="myAncestorSubmenu"/>
		<xsl:choose>
			<xsl:when test="@selectable eq 'false'">
				<xsl:number value="0"/>
			</xsl:when>
			<xsl:when test="@selectable">
				<xsl:number value="1"/>
			</xsl:when>
			<!-- if we do not have a context menu at all then let the ajax subscriber javascript worry about selection 
				mode based on the transient attribute set from @selectable-->
			<xsl:when test="not($myAncestorMenu or $myAncestorSubmenu)">
				<xsl:number value="0"/>
			</xsl:when>
			<!-- from here down we know we have an ancestor menu -->
			<xsl:when test="$myAncestorSubmenu/@selectMode">
				<xsl:number value="1"/>
			</xsl:when>
			<xsl:when test="$myAncestorSubmenu">
				<xsl:number value="0"/>
			</xsl:when>
			<xsl:when test="$myAncestorMenu/@selectMode">
				<xsl:number value="1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:number value="0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
