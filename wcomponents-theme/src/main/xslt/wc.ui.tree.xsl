<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.invalid.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>

	<xsl:template match="ui:tree">
		<xsl:variable name="isError" select="key('errorKey', @id)"/>

		<div role="tree">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="class">
					<xsl:if test="@htree">
						<xsl:text>wc_htree</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>

			<xsl:attribute name="aria-multiselectable">
				<xsl:choose>
					<xsl:when test="@multiple">
						<xsl:value-of select="@multiple"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>

			<xsl:if test="@mode and @mode != 'client'">
				<xsl:attribute name="data-wc-ajaxmode">
					<xsl:value-of select="@mode"/>
				</xsl:attribute>
			</xsl:if>
	
			<xsl:call-template name="requiredElement"/>
			<xsl:call-template name="ajaxController"/>

			<xsl:apply-templates select="ui:margin"/>

			<xsl:if test="$isError">
				<xsl:call-template name="invalid"/>
			</xsl:if>
			
			<xsl:variable name="groupId" select="concat(@id, '-content')"/>
			<div role="group" class="wc_tree_root" id="{$groupId}" data-wc-resizedirection="h">
				<xsl:apply-templates select="ui:treeitem">
					<xsl:with-param name="disabled" select="@disabled"/>
				</xsl:apply-templates>
				<span class="wc_branch_resizer" aria-hidden="true">
					<button type="button" class="wc_btn_nada wc_btn_icon wc_invite wc_resize wc_branch_resize_handle" data-wc-resize="{$groupId}" role="presentation">
						<span class="wc_off">resize handle</span>
					</button>
				</span>
			</div>

			<xsl:call-template name="inlineError">
				<xsl:with-param name="errors" select="$isError"/>
			</xsl:call-template>
		</div>
	</xsl:template>
</xsl:stylesheet>
