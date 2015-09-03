<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.debug.submenu.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

<!--
	This is the transform of the content of a submenu. The template creates a 
	wrapper element and sets up several attributes which control its behaviour, 
	style and exposure to assitive technologies.
-->
	<xsl:template match="ui:content" mode="submenu">
		<xsl:param name="open" select="0"/>
		<xsl:variable name="mode" select="../@mode"/>
		
		<xsl:variable name="isAjaxMode">
			<xsl:if test="$mode='dynamic' or $mode='eager' or ($mode='lazy' and $open!=1)">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		
		<xsl:variable name="submenuId">
			<xsl:value-of select="../@id"/>
		</xsl:variable>
		
		<xsl:element name="div">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="aria-labelledby">
				<xsl:value-of select="$submenuId"/>
				<xsl:text>${wc.ui.menu.submenu.openerIdSuffix}</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>submenucontent</xsl:text>
				<xsl:choose>
					<xsl:when test="$isAjaxMode=1">
						<xsl:text> wc_magic</xsl:text>
						<xsl:if test="$mode='dynamic'">
							<xsl:text> wc_dynamic</xsl:text>
						</xsl:if>
					</xsl:when>
					<xsl:when test="$mode='server'">
						<xsl:text> wc_lame</xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="$isAjaxMode=1">
				<xsl:attribute name="data-wc-ajaxalias">
					<xsl:value-of select="$submenuId"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="role">
				<xsl:choose>
					<xsl:when test="ancestor::ui:menu[1]/@type='tree'">
						<xsl:text>group</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>menu</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:apply-templates select="*"/>
		</xsl:element>
	</xsl:template>


</xsl:stylesheet>
