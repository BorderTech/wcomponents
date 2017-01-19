<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>

	<!-- Transform for WSubMenu. -->
	<xsl:template match="ui:submenu">
		<div id="{@id}" role="presentation"><!-- the presentation role is redundant but stops AXS from whining. -->
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:call-template name="disabledElement">
				<xsl:with-param name="isControl" select="0"/>
			</xsl:call-template>
			<!-- This is the submenu opener/label element. -->
			<button type="button" id="{concat(@id, '_o')}" name="{@id}" class="wc-nobutton wc-invite wc-submenu-o" aria-controls="{@id}" aria-haspopup="true">
				<xsl:attribute name="aria-pressed">
					<xsl:choose>
						<xsl:when test="@open">true</xsl:when>
						<xsl:otherwise>false</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:call-template name="title"/>
				<xsl:call-template name="disabledElement"/>
				<xsl:call-template name="accessKey"/>
				<xsl:apply-templates select="ui:decoratedlabel"/>
			</button>
			<xsl:apply-templates select="ui:content" mode="submenu"/>
		</div>
	</xsl:template>

	<!-- This is the transform of the content of a submenu. -->
	<xsl:template match="ui:content" mode="submenu">
		<xsl:variable name="mode" select="../@mode"/>
		<xsl:variable name="isAjaxMode">
			<xsl:choose>
				<xsl:when test="$mode eq 'dynamic' or $mode eq 'eager' or ($mode eq 'lazy' and not(../@open))">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<div id="{@id}" arial-labelledby="{concat(../@id, '_o')}" role="menu">
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
					<xsl:value-of select="../@id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="aria-expanded">
				<xsl:choose>
					<xsl:when test="../@open">
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
