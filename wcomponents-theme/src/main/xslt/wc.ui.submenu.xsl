<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.icon.xsl"/>
	
	<xsl:template name='submenuIcon'>
		<xsl:call-template name="icon">
			<xsl:with-param name="class">
				<xsl:choose>
					<xsl:when test="@open = 'false'">fa-caret-right</xsl:when> <!-- only tree menus have the @open attribute -->
					<xsl:when test="@open">fa-caret-down</xsl:when>
					<xsl:when test="@nested or @type='column'">fa-caret-right</xsl:when>
					<xsl:otherwise>fa-caret-down</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Transform for WSubMenu. -->
	<xsl:template match="ui:submenu">
		<div id="{@id}" role="presentation"><!-- the presentation role is redundant but stops AXS from whining. -->
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:call-template name="disabledElement">
				<xsl:with-param name="isControl" select="0"/>
			</xsl:call-template>
			<!-- This is the submenu opener/label element. -->
			<xsl:variable name="isTree">
				<xsl:choose>
					<xsl:when test="@type = 'tree'">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<button type="button" id="{concat(@id, '_o')}" name="{@id}" class="wc-nobutton wc-invite wc-submenu-o" aria-controls="{@id}">
				<xsl:attribute name="aria-pressed">
					<xsl:choose>
						<xsl:when test="@open = 'true'">true</xsl:when>
						<xsl:otherwise>false</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:if test="number($isTree) eq 1">
					<xsl:attribute name="aria-haspopup">true</xsl:attribute>
				</xsl:if>
				<xsl:call-template name="title"/>
				<xsl:call-template name="disabledElement"/>
				<xsl:call-template name="accessKey"/>
				<xsl:if test="number($isTree) eq 1">
					<xsl:call-template name="submenuIcon"/>
				</xsl:if>
				<xsl:apply-templates select="ui:decoratedlabel"/>
				<xsl:if test="number($isTree) eq 0">
					<xsl:call-template name="submenuIcon"/>
				</xsl:if>
			</button>
			<xsl:apply-templates select="ui:content" mode="submenu"/>
		</div>
	</xsl:template>

	<!-- This is the transform of the content of a submenu. -->
	<xsl:template match="ui:content" mode="submenu">
		<xsl:variable name="mode" select="../@mode"/>
		<xsl:variable name="isAjaxMode">
			<xsl:choose>
				<xsl:when test="$mode eq 'dynamic' or $mode eq 'eager' or ($mode eq 'lazy' and not(../@open = 'true'))">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<div id="{@id}" arial-labelledby="{concat(../@id, '_o')}" role="menu">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:text>wc_submenucontent</xsl:text>
					<xsl:if test="number($isAjaxMode) eq 1">
						<xsl:text> wc_magic</xsl:text>
						<xsl:if test="$mode eq 'dynamic'">
							<xsl:text> wc_dynamic</xsl:text>
						</xsl:if>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:if test="number($isAjaxMode) eq 1">
				<xsl:attribute name="data-wc-ajaxalias">
					<xsl:value-of select="../@id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="aria-expanded">
				<xsl:choose>
					<xsl:when test="../@open = 'true'">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="not(*)">
				<!-- make the sub menu busy.
					Why?
					We have to keep the menu role on the content wrapper to make the menu function but role menu must have at least one descendant
					role menuitem[(?:radio)|(?:checkbox)]? _or_ be aria-busy.
				-->
				<xsl:attribute name="aria-busy">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="*"/>
		</div>
	</xsl:template>
</xsl:stylesheet>
