<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.offscreenSpan.xsl"/>
	<xsl:import href="wc.common.aria.live.xsl"/>
	<!--
		Structural: do not override.
		
		Transform for each row in the WTable.

		param myTable: The first table ancestor of the current row. This is determined at the most efficient point
		(usually ui:tbody using its parent node) and then passed through all subsequent transforms to save constant
		ancestor::ui:table[1] lookups.

		param parentIsClosed default 0, 1 indicates that the row's parent row exists and is in a collapsed state.
	-->
	<xsl:template match="ui:tr">
		<xsl:param name="myTable"/>
		<xsl:param name="parentIsClosed" select="0"/>
		<xsl:param name="topRowIsStriped" select="0"/>
		<xsl:variable name="tableId" select="$myTable/@id"/>
		<xsl:variable name="rowId" select="concat($tableId,'_',@rowIndex)"/>
		<xsl:variable name="tableRowSelection">
			<xsl:choose>
				<xsl:when test="$myTable/ui:rowselection">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="hasRowExpansion">
			<xsl:choose>
				<xsl:when test="$myTable/ui:rowexpansion">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="indent">
			<xsl:choose>
				<xsl:when test="number($hasRowExpansion) eq 1 and $myTable/@type eq 'hierarchic' and parent::ui:subtr">
					<xsl:value-of select="count(ancestor::ui:subtr[ancestor::ui:table[1] eq $myTable])"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="removeRow">
			<xsl:choose>
				<xsl:when test="number($parentIsClosed) eq 1 or @hidden or parent::ui:subtr[not(@open)] or (ancestor::ui:subtr[not(@open) and ancestor::ui:table[1]/@id eq $tableId])">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:when test="parent::ui:tbody and $myTable/ui:pagination and $myTable/ui:pagination/@mode eq 'client'">
					<xsl:call-template name="clientRowClosedHelper">
						<xsl:with-param name="myTable" select="$myTable"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="rowIsSelectable">
			<xsl:choose>
				<xsl:when test="number($tableRowSelection) eq 1 and not(@unselectable)">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- START OF ROW -->
		<tr id="{$rowId}" data-wc-rowindex="{@rowIndex}">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:choose>
						<xsl:when test="parent::ui:tbody">
							<xsl:if test="$myTable/@striping eq 'rows' and position() mod 2 eq 0">
								<xsl:text>wc_table_stripe</xsl:text>
							</xsl:if>
							<xsl:if test="$myTable/ui:pagination">
								<xsl:text> wc_table_pag_row</xsl:text>
							</xsl:if>
						</xsl:when>
						<xsl:when test="number($topRowIsStriped) eq 1">
							<xsl:text>wc_table_stripe</xsl:text>
						</xsl:when>
					</xsl:choose>
					<xsl:if test="number($rowIsSelectable) eq 1">
						<xsl:text> wc-invite</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<!-- temporary fix for problem in ariaAnalog.js -->
			<xsl:if test="number($hasRowExpansion) + $rowIsSelectable ge 1">
				<xsl:attribute name="role">row</xsl:attribute>
			</xsl:if>
			<xsl:if test="number($hasRowExpansion) eq 1">
				<xsl:if test="ui:subtr">
					<xsl:attribute name="aria-expanded">
						<xsl:choose>
							<xsl:when test="ui:subtr/@open">
								<xsl:text>true</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>false</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:attribute name="aria-controls">
						<xsl:choose>
							<xsl:when test="ui:subtr/ui:tr">
								<xsl:apply-templates select="ui:subtr/ui:tr" mode="subRowControlIdentifier">
									<xsl:with-param name="tableId" select="$tableId"/>
								</xsl:apply-templates>
							</xsl:when>
							<xsl:when test="ui:subtr/ui:content">
								<xsl:value-of select="concat($tableId,'_subc',@rowIndex)"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="concat($tableId,'_sub',@rowIndex)"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:variable name="expMode" select="$myTable/ui:rowexpansion/@mode"/>
					<xsl:if test="$expMode eq 'lazy' and ui:subtr/@open">
						<xsl:attribute name="data-wc-expmode">
							<xsl:text>client</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
				<xsl:if test="parent::ui:subtr">
					<xsl:call-template name="setARIALive"/>
				</xsl:if>
				<xsl:attribute name="aria-level">
					<xsl:value-of select="count(ancestor::ui:subtr[ancestor::ui:table[1]/@id eq $tableId]) + 1"/>
				</xsl:attribute>
			</xsl:if>
			<!--
				Row selection
				 When the table has row selection and when this row is selectable, then we need
				 to set up a series of ARIA properties and states and set the right role.

				role
				 If the rowSelection type is single then each row is a radio, otherwise it is
				 a checkbox.

				aria-checked
				 If the row is selected this is set true. We do not care about multiple
				 selection of radio buttons here it is up to the application to get that right.

				tabindex
				 To make row selection keyboard drivable the row must have a tabindex of 0

				data-wc-name and data-wc-value
				 These attributes are used in place of name and value attributes to report the
				 selected state of the row back to the server.
			-->
			<xsl:if test="number($rowIsSelectable) eq 1">
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
				<xsl:attribute name="tabindex">
					<xsl:text>0</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="data-wc-name">
					<xsl:value-of select="concat($tableId,'.selected')"/>
				</xsl:attribute>
				<xsl:attribute name="data-wc-value">
					<xsl:value-of select="@rowIndex"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="number($removeRow) eq 1">
				<xsl:call-template name="hiddenElement"/>
			</xsl:if>
<!-- END OF TR ATTRIBUTES -->
			<!-- rowSelection indicator wrapper

			This cell is an empty cell which is used as a placeholder to display the secondary indicators of the row
			selection mechanism and state. The primary indicators are the aria-selected state.
			-->
			<xsl:if test="number($tableRowSelection) eq 1">
				<td class="wc_table_sel_wrapper wc-icon">
					<xsl:choose>
						<xsl:when test="(number($hasRowExpansion) + number($tableRowSelection) eq 2) and $myTable/ui:rowselection/@toggle and ui:subtr/ui:tr[not(@unselectable)]">
							<xsl:variable name="subRowControlList">
								<xsl:if test="not(@unselectable)">
									<xsl:value-of select="concat($rowId, ' ')"/><!-- these controllers control this row too -->
								</xsl:if>
								<xsl:apply-templates select="ui:subtr//ui:tr[not(@unselectable) and ancestor::ui:table[1]/@id eq $tableId]" mode="subRowControlIdentifier">
									<xsl:with-param name="tableId" select="$tableId"/>
								</xsl:apply-templates>
							</xsl:variable>
							<xsl:if test="$subRowControlList ne ''">
								<xsl:attribute name="role">
									<xsl:text>presentation</xsl:text>
								</xsl:attribute>
								<xsl:variable name="subRowToggleControlId" select="concat($rowId, '_toggleController')"/>
								<xsl:variable name="subRowToggleControlButtonId" select="concat($subRowToggleControlId, '_showbtn')"/>
								<xsl:variable name="subRowToggleControlContentId" select="concat($subRowToggleControlId, '_content')"/>
								<!--
									THIS IS HORRID but necessary - it has to be a complete emulation of a flyout menu but I have nothing to
									apply to make the submenu and menu items so I cannot even make the menu template into a named template.
								-->
								<div class="wc-menu wc-menu-type-flyout wc_menu_bar" role="menubar" id="{$subRowToggleControlId}">
									<div class="wc-submenu" role="presentation">
										<button type="button" aria-haspopup="true" class="wc-nobutton wc-invite wc-submenu-o" id="{$subRowToggleControlButtonId}" aria-controls="{$subRowToggleControlContentId}">
											<span class="wc-decoratedlabel">
												<xsl:call-template name="offscreenSpan">
													<xsl:with-param name="class" select="'wc-labelbody'"/>
													<xsl:with-param name="text"><xsl:text>{{t 'table_rowSelection_toggleAll'}}</xsl:text></xsl:with-param>
												</xsl:call-template>
											</span>
										</button>
										<div class="wc_submenucontent wc_seltog" role="menu" aria-expanded="false" id="{$subRowToggleControlContentId}" aria-labelledby="{$subRowToggleControlButtonId}">
											<xsl:variable name="allSelectableSubRows" select="count(.//ui:subtr[ancestor::ui:table[1]/@id eq $tableId]/ui:tr[not(@unselectable)])"/>
											<xsl:variable name="allUnselectedSubRows" select="count(.//ui:subtr[ancestor::ui:table[1]/@id eq $tableId]/ui:tr[not(@unselectable or @selected)])"/>
											<button type="button" role="menuitemradio" class="wc-menuitem wc_seltog wc-nobutton wc-invite wc-icon" aria-controls="{$subRowControlList}" data-wc-value="all">
												<xsl:attribute name="aria-checked">
													<xsl:choose>
														<xsl:when test="number($allUnselectedSubRows) eq 0">
															<xsl:text>true</xsl:text>
														</xsl:when>
														<xsl:otherwise>
															<xsl:text>false</xsl:text>
														</xsl:otherwise>
													</xsl:choose>
												</xsl:attribute>
												<xsl:call-template name="offscreenSpan">
													<xsl:with-param name="text"><xsl:text>{{t 'toggle_all_label'}}</xsl:text></xsl:with-param>
												</xsl:call-template>
											</button>
											<button type="button" role="menuitemradio" class="wc-menuitem wc_seltog wc-nobutton wc-invite wc-icon" aria-controls="{$subRowControlList}" data-wc-value="none">
												<xsl:attribute name="aria-checked">
													<xsl:choose>
														<xsl:when test="number($allSelectableSubRows) eq number($allUnselectedSubRows)">
															<xsl:text>true</xsl:text>
														</xsl:when>
														<xsl:otherwise>
															<xsl:text>false</xsl:text>
														</xsl:otherwise>
													</xsl:choose>
												</xsl:attribute>
												<xsl:call-template name="offscreenSpan">
													<xsl:with-param name="text"><xsl:text>{{t 'toggle_none_label'}}</xsl:text></xsl:with-param>
												</xsl:call-template>
											</button>
										</div>
									</div>
								</div>
							</xsl:if>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="aria-hidden">
								<xsl:text>true</xsl:text>
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</xsl:if>
			<!--
			 row expansion controls

			 This cell serves two purposes, one is to hold the expansion control or an indicator for
			 compliant assistive technologues that the row is expanded or not expandable, the other is
			 to span the indentation columns which are required before outputting the collapser element.
			-->
			<xsl:if test="$myTable/ui:rowexpansion">
				<!-- The rowExpansion cell will hold the expansion control (if any) -->
				<td class="wc_table_rowexp_container wc-icon">
					<xsl:if test="ui:subtr">
						<xsl:attribute name="role">button</xsl:attribute>
						<xsl:attribute name="aria-controls">
							<xsl:value-of select="$rowId"/>
						</xsl:attribute>
						<xsl:attribute name="tabindex">0</xsl:attribute>
						<xsl:call-template name="offscreenSpan">
							<xsl:with-param name="text">
								<xsl:text>{{t 'table_rowExpansion_rowButtonDescription'}}</xsl:text>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
				</td>
			</xsl:if>
<!-- APPLY ELEMENT TEMPLATES -->
			<xsl:apply-templates select="ui:th|ui:td">
				<xsl:with-param name="myTable" select="$myTable"/>
				<xsl:with-param name="indent" select="$indent"/>
			</xsl:apply-templates>
		</tr>
<!-- SubTR -->
		<!--
		 The subTr child element is applied after closing the row's tr element as it
		 is not a child of the row.
		-->
		<xsl:apply-templates select="ui:subtr">
			<xsl:with-param name="myTable" select="$myTable"/>
			<xsl:with-param name="parentIsClosed" select="$removeRow"/>
			<xsl:with-param name="indent" select="$indent"/>
			<xsl:with-param name="topRowIsStriped">
				<xsl:choose>
					<xsl:when test="number($topRowIsStriped) eq 1 or (parent::ui:tbody and $myTable/@striping eq 'rows' and position() mod 2 eq 0)">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<!--
		A row expander control needs to know what it controls. This could be a set of rows, including other	subRows. 
		Since the rows are siblings they must be controlled individually. This template outputs ids of all rows which 
		are controlled by a rowExpansion control. This is also used by WAI-ARIA to indicate the DOM nodes controlled by 
		the expander. The list is space separated.
	-->
	<xsl:template match="ui:tr" mode="subRowControlIdentifier">
		<xsl:param name="tableId"/>
		<xsl:value-of select="concat($tableId,'_',@rowIndex)"/>
		<xsl:if test="position() ne last()">
			<xsl:value-of select="' '"/>
		</xsl:if>
	</xsl:template>

	<!-- 
		Determine if a row is closed when rowExpansion is in client mode.
	-->
	<xsl:template name="clientRowClosedHelper">
		<xsl:param name="myTable"/>
		<xsl:variable name="clientPaginationRows">
			<xsl:choose>
				<xsl:when test="$myTable/ui:pagination/@rowsPerPage">
					<xsl:value-of select="$myTable/ui:pagination/@rowsPerPage"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$myTable/ui:pagination/@rows"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="tableCurrentPage" select="$myTable/ui:pagination/@currentPage"/>
		<xsl:variable name="myPosition" select="count(preceding-sibling::ui:tr) + 1"/>
		<xsl:variable name="activeStart" select="($clientPaginationRows * $tableCurrentPage) + 1"/>
		<xsl:choose>
			<xsl:when test="((number($myPosition) lt $activeStart) or (number($myPosition) ge (number($activeStart) + number($clientPaginationRows))))">
				<xsl:number value="1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:number value="0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!--
		Transform of a ui:th element within a ui:tr. This is a row header and is a 1:1 map with a HTML th element.
	-->
	<xsl:template match="ui:th">
		<xsl:param name="myTable"/>
		<xsl:variable name="tableId" select="$myTable/@id"/>
		<xsl:variable name="myHeader" select="$myTable/ui:thead/ui:th[1]"/>
		<th id="{concat($tableId,'_trh',../@rowIndex)}" scope="row">
			<xsl:if test="$myHeader">
				<xsl:attribute name="headers">
					<xsl:value-of select="concat($tableId,'_thh','1')"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="$myHeader">
						<xsl:value-of select="concat('wc-align-', $myHeader/@align)"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates />
		</th>
	</xsl:template>
	
	<!--
		The transform for data cells within the table. These are a 1:1 map with a HTML td element.
	-->
	<xsl:template match="ui:td">
		<xsl:param name="myTable"/>
		<xsl:variable name="tableId" select="$myTable/@id"/>
		<xsl:variable name="tbleColPos" select="position()"/>
		<xsl:variable name="colHeaderElement" select="$myTable/ui:thead/ui:th[position() eq number($tbleColPos)]"/>
		<xsl:variable name="rowHeaderElement" select="../ui:th[1]"/><!-- the one is redundant -->
		<td>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="$colHeaderElement/@align">
						<xsl:value-of select="concat('wc-align-',$colHeaderElement/@align)"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:if test="$colHeaderElement or $rowHeaderElement">
				<xsl:attribute name="headers">
					<xsl:variable name="colHeader">
						<xsl:if test="$colHeaderElement">
							<xsl:value-of select="concat($tableId,'_thh',$tbleColPos)"/>
						</xsl:if>
					</xsl:variable>
					<xsl:variable name="rowHeader">
						<xsl:if test="$rowHeaderElement">
							<xsl:value-of select="concat($tableId,'_trh',../@rowIndex)"/>
						</xsl:if>
					</xsl:variable>
					<xsl:value-of select="normalize-space(concat($colHeader,' ',$rowHeader))"/>
				</xsl:attribute>
			</xsl:if>
<!-- END OF TD ATTRIBUTES -->
			<xsl:apply-templates/>
		</td>
	</xsl:template>
	
	<!--
		Transform of ui:subtr. This is a sub row element which is an optional child of a ui:tr element. It should not be
		present if the table does not have rowExpansion. In HTML these are siblings of their parent ui:tr which makes
		row manipulation interesting.
	-->
	<xsl:template match="ui:subtr">
		<xsl:param name="myTable"/>
		<xsl:param name="parentIsClosed" select="0"/>
		<xsl:param name="topRowIsStriped" select="0"/>
		<xsl:param name="indent" select="0"/>
		<!--
		 We have to output content if:
		 
			* she subTr is open;
			* the expansion mode is client;
			* there is a ui:content child element; or
			* there are ui:tr child elements.
			
		 Otherwise we have to create a null content placeholder with the appropriate wires to make the expansion AJAX 
		 enabled or able to force a submit on open.
		-->
		<xsl:choose>
			<xsl:when test="*">
				<xsl:apply-templates select="*">
					<xsl:with-param name="myTable" select="$myTable"/>
					<xsl:with-param name="parentIsClosed" select="$parentIsClosed"/>
					<xsl:with-param name="indent" select="$indent"/>
					<xsl:with-param name="topRowIsStriped" select="$topRowIsStriped"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="tableId" select="$myTable/@id"/>
				<tr id="{concat($tableId,'_sub',../@rowIndex)}" hidden="hidden"></tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!--
		Transform for ui:content child of a ui:subtr.
	-->
	<xsl:template match="ui:subtr/ui:content">
		<xsl:param name="myTable"/>
		<xsl:param name="parentIsClosed" select="0"/>
		<xsl:param name="topRowIsStriped" select="0"/>
		<xsl:param name="indent" select="0"/>
		<xsl:variable name="tableId" select="$myTable/@id"/>
		<!--NOTE: aria-level the minimum is going to be level 2 -->
		<tr id="{concat($tableId,'_subc',../../@rowIndex)}" role="row" aria-level="{count(ancestor::ui:subtr[ancestor::ui:table[1]/@id eq $tableId]) + 1}">
			<xsl:if test="number($parentIsClosed) eq 1 or ancestor::ui:subtr[not(@open)]">
				<xsl:call-template name="hiddenElement"/>
			</xsl:if>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="number($topRowIsStriped) eq 1">
						<xsl:text>wc_table_stripe</xsl:text>
					</xsl:if>
					<xsl:if test="number($indent) gt 0">
						<xsl:value-of select="concat(' wc_tbl_indent_', $indent)"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<!--
				subTr content is never individually selectable but must have the placeholder if the table has row
				selection.
			-->
			<xsl:if test="$myTable/ui:rowselection">
				<td class="wc_table_sel_wrapper">
					<xsl:text>&#x2002;</xsl:text>
				</td>
			</xsl:if>
			<!--
				subTr content is not itself expandable but must have the placeholder to position it correctly relative
				to its parent row.
			-->
			<td class="wc_table_rowexp_container">
				<xsl:text>&#x2002;</xsl:text>
			</td>
			<td>
				<xsl:if test="@spanAllCols">
					<xsl:attribute name="colspan">
						<xsl:value-of select="count(../../*) -1"/><!-- -1 because we do not count the ui:subtr -->
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
