<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.offscreenSpan.xsl"/>
	<xsl:import href="wc.common.icon.xsl"/>

	<xsl:template match="ui:tree">
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
			<xsl:if test="@mode">
				<xsl:attribute name="data-wc-mode">
					<xsl:value-of select="@mode"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="requiredElement">
				<xsl:with-param name="useNative" select="0"/>
			</xsl:call-template>
			<xsl:variable name="groupId" select="concat(@id, '-content')"/>
			<div role="group" class="wc_tree_root" id="{$groupId}" data-wc-resizedirection="h">
				<xsl:apply-templates select="ui:treeitem">
					<xsl:with-param name="disabled" select="@disabled"/>
				</xsl:apply-templates>
				<xsl:call-template name="resizerbar">
					<xsl:with-param name="groupId" select="$groupId"/>
				</xsl:call-template>
			</div>
		</div>
	</xsl:template>
	
	<xsl:template match="ui:treeitem">
		<xsl:param name="disabled" select="'false'"/>
		<xsl:variable name="element">
			<xsl:choose>
				<xsl:when test="@expandable">
					<xsl:text>div</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>button</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="isButton">
			<xsl:choose>
				<xsl:when test="$element eq 'div'">
					<xsl:number value="0"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$element}">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="isControl" select="$isButton"/>
				<xsl:with-param name="class">
					<xsl:if test="number($isButton) eq 1">
						<xsl:text>wc-nobutton wc-invite</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:attribute name="role">
				<xsl:text>treeitem</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="aria-selected">
				<xsl:choose>
					<xsl:when test="@selected">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<!-- common attributes will set the correct disabled state if @disabled is set. -->
			<xsl:if test="not(@disabled) and $disabled eq 'true'">
				<xsl:choose>
					<xsl:when test="number($isButton) eq 1">
						<xsl:attribute name="disabled">disabled</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="aria-disabled">true</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="number($isButton) eq 1">
					<xsl:attribute name="type">
						<xsl:text>button</xsl:text>
					</xsl:attribute>
					<!-- leave tabindex on this butten, it is used as a short-hand to find fousable controls in the core menu JavaScript. -->
					<xsl:attribute name="tabindex">
						<xsl:text>0</xsl:text>
					</xsl:attribute>
					<xsl:call-template name="title"/>
					<span class="wc_leaf_vopener" aria-hidden="true">
						<xsl:text>&#x0a;</xsl:text>
					</span>
					<xsl:call-template name="treeitemContent"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="aria-expanded">
						<xsl:choose>
							<xsl:when test="@open and not(@disabled)">
								<xsl:text>true</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>false</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:variable name="nameButtonId">
						<xsl:value-of select="concat(@id, '-branch-name')"/>
					</xsl:variable>
					<button class="wc-nobutton wc-invite wc_leaf_vopener" aria-hidden="true" type="button" tabindex="-1">
						<xsl:call-template name="icon">
							<xsl:with-param name="class">
								<xsl:choose>
									<xsl:when test="@open and not(@disabled)">fa-caret-down</xsl:when>
									<xsl:otherwise>fa-caret-right</xsl:otherwise>
								</xsl:choose>
							</xsl:with-param>
						</xsl:call-template>
						<xsl:call-template name="offscreenSpan">
							<xsl:with-param name="text">
								<xsl:text>{{#i18n}}tree_toggle_branch{{/i18n}}</xsl:text>
							</xsl:with-param>
						</xsl:call-template>
					</button>
					<!-- leave tabindex="0" on this button, it is used as a short-hand to find focusable controls in the core menu JavaScript. -->
					<button type="button" class="wc-nobutton wc-invite wc_leaf" id="{$nameButtonId}" aria-controls="{@id}" tabindex="0">
						<xsl:call-template name="title"/>
						<xsl:call-template name="treeitemContent">
							<xsl:with-param name="isButton" select="1"/>
						</xsl:call-template>
					</button>
					<!-- The content ID here is just for theme AJAX purposes. -->
					<xsl:variable name="groupId" select="concat(@id, '-content')"/>
					<div role="group" aria-labelledby="{$nameButtonId}" id="{$groupId}" data-wc-resizedirection="h">
						<xsl:if test="not(ui:treeitem)">
							<xsl:attribute name="aria-busy">
								<xsl:text>true</xsl:text>
							</xsl:attribute>
						</xsl:if>
						<xsl:apply-templates select="ui:treeitem">
							<xsl:with-param name="disabled">
								<xsl:choose>
									<xsl:when test="@disabled or $disabled eq 'true'">
										<xsl:text>true</xsl:text>
									</xsl:when>
									<xsl:otherwise>false</xsl:otherwise>
								</xsl:choose>
							</xsl:with-param>
						</xsl:apply-templates>
						<xsl:call-template name="resizerbar">
							<xsl:with-param name="groupId" select="$groupId"/>
						</xsl:call-template>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="treeitemContent">
		<xsl:param name="isButton" select="0"/>
		<span aria-hidden="true" class="wc_leaf_img">
			<xsl:choose>
				<xsl:when test="@imageUrl">
					<img src="{@imageUrl}" alt=""/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="icon">
						<xsl:with-param name="class">
							<xsl:choose>
								<xsl:when test="$isButton = 0">fa-file-o</xsl:when>
								<xsl:otherwise>fa-folder-o</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</span>
		<span class="wc_leaf_name">
			<xsl:value-of select="@label"/>
		</span>
		<span class="wc_leaf_hopener" aria-hidden="true">
			<xsl:call-template name="icon">
				<xsl:with-param name="class">fa-caret-right</xsl:with-param>
			</xsl:call-template>
		</span>
	</xsl:template>
	
	<xsl:template name="resizerbar">
		<xsl:param name="groupId"/>
		<span class="wc_branch_resizer" aria-hidden="true">
			<button type="button" class="wc-nobutton wc-invite wc_resize wc_branch_resize_handle" data-wc-resize="{$groupId}">
				<xsl:call-template name="offscreenSpan">
					<xsl:with-param name="text">
						<xsl:text>{{#i18n}}tree_resize_handle{{/i18n}}</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</button>
		</span>
	</xsl:template>
</xsl:stylesheet>
