<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.menu.n.hasStickyOpen.xsl"/>
	<xsl:import href="wc.ui.menu.n.menuRoleIsSelectable.xsl"/>
	<xsl:import href="wc.ui.menu.n.menuTabIndexHelper.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<xsl:import href="wc.common.title.xsl"/>
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<!--
		WMenuItem forms part of a single compound widget with the WMenu at its root.

		The transform for WMenuItem. In general this is pretty straightforwards. The
		menuItem is rendered as a single control.
	-->
	<xsl:template match="ui:treeitem">
		<xsl:param name="disabled" select="'false'"/>

		<xsl:variable name="id" select="@id"/>

		<xsl:variable name="element">
			<xsl:choose>
				<xsl:when test="@expandable or ui:treeitem">
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
					<xsl:text>wc-submenu</xsl:text><!-- TODO - check if this is needed in WMenu Type TREE-->
					<xsl:if test="$isButton=1">
						<xsl:text> wc_btn_nada</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			
			<xsl:attribute name="role">
				<xsl:text>treeitem</xsl:text>
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

			<xsl:if test="@expandable">
				<xsl:attribute name="aria-expanded">
					<xsl:choose>
						<xsl:when test="@open">
							<xsl:copy-of select="$t"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>false</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>

			<xsl:variable name="myAncestorTree" select="ancestor::ui:tree[1]"/>
			<xsl:variable name="isHtree">
				<xsl:choose>
					<xsl:when test="not(myAncestorTree)">
						<xsl:number value="0"/>
					</xsl:when>
					<xsl:when test="myAncestorTree/@htree = $t">
						<xsl:number value="2"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="1"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:choose>
				<xsl:when test="$isButton=1">
					<xsl:call-template name="title"/>
					<xsl:if test="$isHtree &lt; 2">
						<span class="wc_leaf_vopener" role="presentation">
							<xsl:text>&#x0a;</xsl:text>
						</span>
					</xsl:if>
					<span class="wc_leaf_img" role="presentation">
						<xsl:choose>
							<xsl:when test="@imageUrl">
								<img src="{@imageUrl}" alt=""/>
							</xsl:when>
							<xsl:otherwise>&#x0a;</xsl:otherwise>
						</xsl:choose>
					</span>
					<span class="wc_leaf_name">
						<xsl:value-of select="@label"/>
					</span>
					<xsl:if test="$isHtree !=1">
						<span class="wc_leaf_hopener" role="presentation">
							<xsl:text>&#x0a;</xsl:text>
						</span>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="contentWrapperId">
						<xsl:value-of select="concat(@id, '-branch-content')"/>
					</xsl:variable>
					<xsl:variable name="nameButtonId">
							<xsl:value-of select="concat(@id, '-branch-name')"/>
					</xsl:variable>
					<xsl:if test="$isHtree &lt; 2">
						<button type="button" class="wc_btn_nada wc_leaf_vopener" aria-controls="{$contentWrapperId}">
							<xsl:text>&#x0a;</xsl:text>
						</button>
					</xsl:if>
					<button type="button" class="wc_btn_nada wc_leaf" id="{$nameButtonId}">
						<xsl:call-template name="title"/>
						<span class="wc_leaf_img">
							<xsl:choose>
								<xsl:when test="@imageUrl">
									<img src="{@imageUrl}" alt=""/>
								</xsl:when>
								<xsl:otherwise>&#x0a;</xsl:otherwise>
							</xsl:choose>
						</span>
						<span class="wc_leaf_name">
							<xsl:value-of select="@label"/>
						</span>
					</button>
					<xsl:if test="$isHtree != 1">
						<button type="button" class="wc_btn_nada wc_leaf_hopener" aria-controls="{$contentWrapperId}">
							<xsl:text>&#x0a;</xsl:text>
						</button>
					</xsl:if>
					<div class="wc_submenucontent" role="group" id="{$contentWrapperId}" aria-labelledby="{$nameButtonId}">
						<xsl:if test="not(@open = $t)">
							<xsl:call-template name="hiddenElement"/>
						</xsl:if>
						<xsl:if test="not(ui:treeitem)">
							<xsl:attribute name="aria-busy">
								<xsl:copy-of select="$t"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:apply-templates select="ui:treeitem">
							<xsl:with-param name="disabled">
								<xsl:choose>
									<xsl:when test="@disabled = $t or $disabled = $t">
										<xsl:copy-of select="$t"/>
									</xsl:when>
									<xsl:otherwise>false</xsl:otherwise>
								</xsl:choose>
							</xsl:with-param>
						</xsl:apply-templates>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
