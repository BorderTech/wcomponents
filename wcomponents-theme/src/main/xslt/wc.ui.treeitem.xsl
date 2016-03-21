<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<xsl:import href="wc.common.title.xsl"/>
	<xsl:import href="wc.common.offscreenSpan.xsl"/>
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<!--
		WMenuItem forms part of a single compound widget with the WMenu at its root.

		The transform for WMenuItem. In general this is pretty straightforwards. The
		menuItem is rendered as a single control.
	-->
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
				<xsl:when test="$element='div'">
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
					<xsl:if test="$isButton=1">
						<xsl:text>wc_btn_nada wc_invite</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>

			<xsl:attribute name="role">
				<xsl:text>treeitem</xsl:text>
			</xsl:attribute>
			
			<xsl:attribute name="aria-selected">
				<xsl:choose>
					<xsl:when test="@selected">
						<xsl:copy-of select="$t"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>

			<!-- common attributes will set the correct disabled state if @disabled is set. -->
			<xsl:if test="not(@disabled = $t) and $disabled=$t">
				<xsl:choose>
					<xsl:when test="$isButton=1">
						<xsl:attribute name="disabled">disabled</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="aria-disabled">true</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>

			<xsl:choose>
				<xsl:when test="$isButton=1">
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
								<xsl:copy-of select="$t"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>false</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:variable name="nameButtonId">
							<xsl:value-of select="concat(@id, '-branch-name')"/>
					</xsl:variable>
					<button class="wc_btn_nada wc_invite wc_leaf_vopener" aria-hidden="true" type="button" tabindex="-1">
						<xsl:text>&#x0a;</xsl:text>
					</button>
					<!-- leave tabindex="0" on this button, it is used as a short-hand to find focusable controls in the core menu JavaScript. -->
					<button type="button" class="wc_btn_nada wc_invite wc_leaf" id="{$nameButtonId}" aria-controls="{@id}" tabindex="0">
						<xsl:call-template name="title"/>
						<xsl:call-template name="treeitemContent"/>
					</button>
					<!-- The content ID here is just for theme AJAX purposes. -->
					<xsl:variable name="groupId" select="concat(@id, '-content')"/>
					<div role="group" aria-labelledby="{$nameButtonId}" id="{$groupId}" data-wc-resizedirection="h">
						<xsl:if test="not(ui:treeitem)">
							<xsl:attribute name="aria-busy">
								<xsl:copy-of select="$t"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:apply-templates select="ui:treeitem">
							<xsl:with-param name="disabled">
								<xsl:choose>
									<xsl:when test="@disabled or $disabled = $t">
										<xsl:copy-of select="$t"/>
									</xsl:when>
									<xsl:otherwise>false</xsl:otherwise>
								</xsl:choose>
							</xsl:with-param>
						</xsl:apply-templates>
						<span class="wc_branch_resizer" aria-hidden="true">
							<button type="button" class="wc_btn_nada wc_btn_icon wc_invite wc_resize wc_branch_resize_handle" data-wc-resize="{$groupId}" role="presentation">
								<span class="wc_off">resize handle</span>
							</button>
						</span>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>

	<xsl:template name="treeitemContent">
		<span aria-hidden='true'>
			<xsl:attribute name="class">
				<xsl:text>wc_leaf_img</xsl:text>
				<xsl:if test="not(@imageUrl)">
					<xsl:text> wc_leaf_noimg</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test="@imageUrl">
				<img src="{@imageUrl}" alt=""/>
			</xsl:if>
		</span>
		<span class="wc_leaf_name">
			<xsl:value-of select="@label"/>
		</span>
		<span class="wc_leaf_hopener" aria-hidden="true">
			<xsl:text>&#x0a;</xsl:text>
		</span>
	</xsl:template>
</xsl:stylesheet>
