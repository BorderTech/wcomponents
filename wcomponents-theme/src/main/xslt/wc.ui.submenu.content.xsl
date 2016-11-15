<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		This is the transform of the content of a submenu. The template creates a
		wrapper element and sets up several attributes which control its behaviour,
		style and exposure to assitive technologies.
	-->
	<xsl:template match="ui:content" mode="submenu">
		<xsl:param name="open" select="0"/>
		<xsl:param name="type"/>
		<xsl:variable name="mode" select="../@mode"/>

		<xsl:variable name="isAjaxMode">
			<xsl:choose>
				<xsl:when test="$mode eq 'dynamic' or $mode eq 'eager' or ($mode eq 'lazy' and number($open) ne 1)">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="submenuId">
			<xsl:value-of select="../@id"/>
		</xsl:variable>

		<div id="{@id}" arial-labelledby="{concat($submenuId, '_o')}" role="menu">
			<xsl:attribute name="class">
				<xsl:text>wc_submenucontent</xsl:text>
				<xsl:if test="number($isAjaxMode) eq 1">
					<xsl:text> wc_magic</xsl:text>
					<xsl:if test="$mode eq 'dynamic'">
						<xsl:text> wc_dynamic</xsl:text>
					</xsl:if>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test="number($isAjaxMode) eq 1">
				<xsl:attribute name="data-wc-ajaxalias">
					<xsl:value-of select="$submenuId"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="aria-expanded">
				<xsl:choose>
					<xsl:when test="number($open) eq 1">
						<xsl:copy-of select="$t"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="not(*)">
				<!-- make the sub menu busy.
					Why?
					We have to keep the menu role on the content wrapper to make the menu function but role menu
					must have at least one descendant role menuitem[(?:radio)|(?:checkbox)]? _or_ be aria-busy.
				-->
				<xsl:attribute name="aria-busy">
					<xsl:copy-of select="$t"/>
				</xsl:attribute>
			</xsl:if>
			<button id="{generate-id()}" class="wc-menuitem wc_closesubmenu wc-nobutton wc-icon wc-invite" role="menuitem" type="button">
				<xsl:apply-templates select="../ui:decoratedlabel">
					<xsl:with-param name="useId" select="0"/>
				</xsl:apply-templates>
			</button>
			<xsl:apply-templates select="*"/>
		</div>
	</xsl:template>
</xsl:stylesheet>
