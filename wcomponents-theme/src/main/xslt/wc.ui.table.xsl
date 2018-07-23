
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!--
		WTable

		This is long but reasonably straight-forward generation of HTML tables.

		The HTML TABLE element itself is wrapped in a DIV. This is to provide somewhere to attach messages as a WTable
		can be in an error state (yes, really). As a side-effect it makes it really easy to create more-or-les
		accessible horizontal scrolling.
	-->
	<xsl:template match="ui:table">
		<xsl:variable name="rowExpansion">
			<xsl:choose>
				<xsl:when test="ui:rowexpansion">
					<xsl:value-of select="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="rowSelection">
			<xsl:choose>
				<xsl:when test="ui:rowselection">
					<xsl:value-of select="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- the table wrapper starts here -->
		<xsl:variable name="additionalTableClass">
			<xsl:value-of select="@class"/>
			<xsl:apply-templates select="ui:margin" mode="asclass"/>
			<xsl:if test="@type">
				<xsl:value-of select="concat(' wc-table-', @type)"/>
			</xsl:if>
		</xsl:variable>
		<div id="{@id}" class="{normalize-space(concat('wc-table ', $additionalTableClass))}">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<!-- AJAX table actions make the table an ARIA live region -->
			<xsl:if
				test="ui:pagination[@mode eq 'dynamic' or @mode eq 'client'] or ui:rowexpansion[@mode eq 'lazy' or @mode eq 'dynamic'] or ui:sort[@mode eq 'dynamic']">
				<xsl:attribute name="aria-live">
					<xsl:text>polite</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="number($rowExpansion) eq 1">
				<xsl:variable name="expMode" select="ui:rowexpansion/@mode"/>
				<xsl:attribute name="data-wc-expmode">
					<xsl:choose>
						<xsl:when test="($expMode eq 'lazy') and ui:subtr/@open">
							<xsl:text>client</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$expMode"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="ui:pagination">
				<xsl:attribute name="data-wc-pagemode">
					<xsl:choose>
						<xsl:when test="ui:pagination/@mode">
							<xsl:value-of select="ui:pagination/@mode"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>client</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<!-- THIS IS WHERE THE DIV's CONTENT STARTS NO MORE ATTRIBUTES AFTER THIS POINT THANK YOU! -->
			<!--
				Add table controls which do not form part of the table structure but which control and reference the
				table. The default are the select/deselect all, expand/collapse all and pagination controls (if position
				is TOP or BOTH).
			-->
			<xsl:call-template name="topControls"/>
			<xsl:variable name="tableClass">
				<xsl:if test="number($rowExpansion) eq 1">
					<xsl:text>wc_tbl_expansion</xsl:text>
				</xsl:if>
				<xsl:if test="ui:thead/ui:th[@width]">
					<xsl:text> wc_table_fix</xsl:text>
				</xsl:if>
			</xsl:variable>
			<!-- start the actual table -->
			<table>
				<xsl:if test="$tableClass ne ''">
					<xsl:attribute name="class">
						<xsl:value-of select="normalize-space($tableClass)"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="number($rowExpansion) + number($rowSelection) gt 0">
					<xsl:if test="number($rowSelection) eq 1">
						<xsl:attribute name="aria-multiselectable">
							<xsl:choose>
								<xsl:when test="ui:rowselection/@multiple">
									<xsl:text>true</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>false</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
				<xsl:if test="ui:pagination">
					<xsl:attribute name="data-wc-rpp">
						<xsl:choose>
							<xsl:when test="ui:pagination/@rowsPerPage">
								<xsl:value-of select="ui:pagination/@rowsPerPage"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="ui:pagination/@rows"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="ui:sort">
					<xsl:attribute name="sortable">sortable</xsl:attribute>
				</xsl:if>
				<!-- END OF TABLE ATTRIBUTES -->
				<xsl:if test="@caption or ui:tbody/ui:nodata">
					<caption>
						<div class="wc-caption">
							<xsl:value-of select="@caption"/>
						</div>
						<xsl:apply-templates select="ui:tbody/ui:nodata"/>
					</caption>
				</xsl:if>
				<colgroup>
					<xsl:if test="@separators eq 'both' or @separators eq 'vertical'">
						<xsl:attribute name="class">
							<xsl:text>wc_table_colsep</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="number($rowSelection) eq 1">
						<col class="wc_table_colauto"/>
					</xsl:if>
					<xsl:if test="number($rowExpansion) eq 1">
						<col class="wc_table_colauto"/>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="ui:thead/ui:th">
							<xsl:apply-templates mode="col" select="ui:thead/ui:th">
								<xsl:with-param name="stripe">
									<xsl:choose>
										<xsl:when test="@striping eq 'cols'">
											<xsl:value-of select="1"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:number value="0"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
								<xsl:with-param name="sortCol" select="ui:sort/@col"/>
							</xsl:apply-templates>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates mode="col" select="ui:tbody/ui:tr[1]/ui:th | ui:tbody/ui:tr[1]/ui:td">
								<xsl:with-param name="stripe">
									<xsl:choose>
										<xsl:when test="@striping eq 'cols'">
											<xsl:value-of select="1"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:number value="0"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
								<xsl:with-param name="sortCol" select="ui:sort/@col"/>
							</xsl:apply-templates>
						</xsl:otherwise>
					</xsl:choose>
				</colgroup>
				<xsl:apply-templates select="ui:thead"/>
				<xsl:apply-templates select="ui:tbody"/>
			</table>
			<!--
				Add table controls which do not form part of the table structure but which control and reference the
				table. The default are actions and the pagination controls (if position is unsset, BOTTOM or BOTH).
			-->
			<xsl:call-template name="tableBottomControls"/>
			<xsl:element name="input">
				<xsl:attribute name="type">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:value-of select="concat(@id, '-h')"/>
				</xsl:attribute>
				<xsl:attribute name="value">
					<xsl:text>x</xsl:text>
				</xsl:attribute>
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
			</xsl:element>
		</div>
	</xsl:template>

	<!--
		Creates each col element in the colgroup created in the transform of the table.

		param stripe: 1 if the table has column striping.
		param sortCol: The 0 indexed column which is currently sorted (if any).
	-->
	<xsl:template match="ui:th|ui:td" mode="col">
		<xsl:param name="stripe" select="0"/>
		<xsl:param name="sortCol" select="-1"/>
		<xsl:variable name="class">
			<xsl:if test="number($stripe) eq 1 and position() mod 2 eq 0">
				<xsl:text>wc_table_stripe</xsl:text>
			</xsl:if>
			<xsl:if test="$sortCol and number($sortCol) ge 0 and position() eq number($sortCol) + 1">
				<xsl:text> wc_table_sort_sorted</xsl:text>
			</xsl:if>
		</xsl:variable>
		<col>
			<xsl:if test="$class ne ''">
				<xsl:attribute name="class">
					<xsl:value-of select="normalize-space($class)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@width">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('width:',@width,'%')"/>
				</xsl:attribute>
			</xsl:if>
		</col>
	</xsl:template>

<!-- THEAD -->

	<!-- Template for ui:thead to html thead element -->
	<xsl:template match="ui:thead">
		<thead>
			<tr>
				<xsl:if test="../ui:rowselection">
					<th class="wc_table_sel_wrapper" scope="col" aria-hidden="true">
						<xsl:text>&#xa0;</xsl:text>
					</th>
				</xsl:if>
				<xsl:if test="../ui:rowexpansion">
					<th class="wc_table_rowexp_container" scope="col">
						<span class="wc-off">{{#i18n}}table_rowExpansion_toggleAll{{/i18n}}</span>
					</th>
				</xsl:if>
				<xsl:apply-templates select="ui:th" mode="thead"/>
			</tr>
		</thead>
	</xsl:template>

	<!-- ui:th inside the ui:thead element. -->
	<xsl:template match="ui:th" mode="thead">
		<xsl:variable name="tableId" select="../../@id"/>
		<xsl:variable name="sortable">
			<xsl:choose>
				<xsl:when test="@sortable">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="sortControl" select="../../ui:sort"/>
		<xsl:variable name="isSorted">
			<xsl:choose>
				<xsl:when test="$sortable != 1 or not($sortControl)">
					<xsl:number value="0"/>
				</xsl:when>
				<xsl:when test="position() - 1 eq number($sortControl/@col)">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="sortDesc">
			<xsl:choose>
				<xsl:when test="number($sortable) ne 1 or not($sortControl)">
					<xsl:text>false</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$sortControl/@descending"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="additional">
			<xsl:if test="@align">
				<xsl:value-of select="concat(' wc-align-', @align)"/>
			</xsl:if>
		</xsl:variable>
		<th id="{concat($tableId,'_thh', position())}" scope="col" data-wc-columnidx="{position() - 1}" class="{normalize-space(concat('wc-th ', $additional))}">
			<xsl:if test="number($sortable) eq 1 and $sortControl">
				<xsl:attribute name="tabindex">0</xsl:attribute>
				<xsl:if test="number($isSorted) eq 1">
					<xsl:attribute name="sorted">
						<xsl:if test="$sortDesc eq 'true'">
							<xsl:text>reversed </xsl:text>
						</xsl:if>
						<xsl:text>1</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:attribute name="aria-sort">
					<xsl:choose>
						<xsl:when test="number($isSorted) eq 0">
							<xsl:text>none</xsl:text>
						</xsl:when>
						<xsl:when test="$sortDesc eq 'true'">
							<xsl:text>descending</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>ascending</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="ui:decoratedlabel">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
			<xsl:if test="number($sortable) eq 1 and $sortControl">
				<i aria-hidden="true">
					<xsl:attribute name="class">
						<xsl:text>fa fa-caret-</xsl:text>
						<xsl:choose>
							<xsl:when test="number($isSorted) eq 0">
								<xsl:text>down</xsl:text>
							</xsl:when>
							<xsl:when test="$sortDesc eq 'true'">
								<xsl:text>square-o-down</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>square-o-up</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</i>
			</xsl:if>
		</th>
	</xsl:template>

<!-- TBODY -->
	<!--
		Transform of ui:tbody to tbody.
	-->
	<xsl:template match="ui:tbody">
		<xsl:variable name="additional">
			<xsl:if test="../@type">
				<xsl:value-of select="concat('wc_tbl_', ../@type)"/>
			</xsl:if>
			<xsl:if test="../@separators eq 'both' or ../@separators eq 'horizontal'">
				<xsl:text> wc_table_rowsep</xsl:text>
			</xsl:if>
		</xsl:variable>
		<tbody id="{concat(../@id,'_tb')}" class="{normalize-space(concat('wc-tbody ', $additional))}">
			<xsl:apply-templates select="ui:tr">
				<xsl:with-param name="myTable" select=".."/>
			</xsl:apply-templates>
		</tbody>
	</xsl:template>
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
		<xsl:variable name="rowId" select="concat($tableId, '_', @rowIndex)"/>
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
				<xsl:when
					test="number($parentIsClosed) eq 1 or @hidden or parent::ui:subtr[not(@open)] or (ancestor::ui:subtr[not(@open) and ancestor::ui:table[1]/@id eq $tableId])">
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
		<xsl:variable name="additional">
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
		</xsl:variable>
		<tr class="{normalize-space(concat('wc-tr ', $additional))}" data-wc-rowindex="{@rowIndex}" id="{$rowId}">
			<xsl:if test="@disabled">
				<xsl:attribute name="disabled">
					<xsl:text>disabled</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<!-- WDataTable only -->
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
								<xsl:apply-templates mode="subRowControlIdentifier" select="ui:subtr/ui:tr">
									<xsl:with-param name="tableId" select="$tableId"/>
								</xsl:apply-templates>
							</xsl:when>
							<xsl:when test="ui:subtr/ui:content">
								<xsl:value-of select="concat($tableId, '_subc', @rowIndex)"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="concat($tableId, '_sub', @rowIndex)"/>
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
					<xsl:attribute name="aria-live">
						<xsl:text>polite</xsl:text>
					</xsl:attribute>
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
					<xsl:value-of select="concat($tableId, '.selected')"/>
				</xsl:attribute>
				<xsl:attribute name="data-wc-value">
					<xsl:value-of select="@rowIndex"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="number($removeRow) eq 1">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<!-- END OF TR ATTRIBUTES -->
			<!-- rowSelection indicator wrapper

			This cell is an empty cell which is used as a placeholder to display the secondary indicators of the row
			selection mechanism and state. The primary indicators are the aria-selected state.
			-->
			<xsl:if test="number($tableRowSelection) eq 1">
				<td class="wc_table_sel_wrapper">
					<xsl:choose>
						<xsl:when
							test="(number($hasRowExpansion) + number($tableRowSelection) eq 2) and $myTable/ui:rowselection/@toggle and ui:subtr/ui:tr[not(@unselectable)]">
							<xsl:variable name="subRowControlList">
								<xsl:if test="not(@unselectable)">
									<xsl:value-of select="concat($rowId, ' ')"/>
									<!-- these controllers control this row too -->
								</xsl:if>
								<xsl:apply-templates mode="subRowControlIdentifier"
									select="ui:subtr//ui:tr[not(@unselectable) and ancestor::ui:table[1]/@id eq $tableId]">
									<xsl:with-param name="tableId" select="$tableId"/>
								</xsl:apply-templates>
							</xsl:variable>
							<xsl:if test="$subRowControlList ne ''">
								<xsl:attribute name="role">
									<xsl:text>presentation</xsl:text>
								</xsl:attribute>
							</xsl:if>
							<xsl:if test="number($rowIsSelectable) eq 1">
								<xsl:call-template name="rowSelectionIcon">
									<xsl:with-param name="myTable" select="$myTable"/>
								</xsl:call-template>
							</xsl:if>
							<xsl:if test="$subRowControlList ne ''">
								<xsl:variable name="subRowToggleControlId" select="concat($rowId, '_toggleController')"/>
								<xsl:variable name="subRowToggleControlButtonId" select="concat($subRowToggleControlId, '_showbtn')"/>
								<xsl:variable name="subRowToggleControlContentId" select="concat($subRowToggleControlId, '_content')"/>
								<!--
									THIS IS HORRID but necessary - it has to be a complete emulation of a flyout menu but I have nothing to
									apply to make the submenu and menu items so I cannot even make the menu template into a named template.
								-->
								<div class="wc-menu wc-menu-type-flyout wc_menu_bar" id="{$subRowToggleControlId}" role="menubar">
									<div class="wc-submenu" role="presentation">
										<button aria-controls="{$subRowToggleControlContentId}" aria-haspopup="true"
											class="wc-nobutton wc-invite wc-submenu-o" id="{$subRowToggleControlButtonId}" type="button">
											<span class="wc-decoratedlabel">
												<span class="wc-off wc-labelbody">{{#i18n}}table_rowSelection_toggleAll{{/i18n}}</span>
											</span>
											<i aria-hidden="true" class="fa fa-caret-down"/>
										</button>
										<div aria-expanded="false" aria-labelledby="{$subRowToggleControlButtonId}"
											class="wc_submenucontent wc_seltog" id="{$subRowToggleControlContentId}" role="menu">
											<xsl:variable name="allSelectableSubRows"
												select="count(.//ui:subtr[ancestor::ui:table[1]/@id eq $tableId]/ui:tr[not(@unselectable)])"/>
											<xsl:variable name="allUnselectedSubRows"
												select="count(.//ui:subtr[ancestor::ui:table[1]/@id eq $tableId]/ui:tr[not(@unselectable or @selected)])"/>
											<button aria-controls="{$subRowControlList}" class="wc-menuitem wc_seltog wc-nobutton wc-invite"
												data-wc-value="all" role="menuitemradio" type="button">
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
												<i aria-hidden="true" class="fa fa-check-square-o"/>
												<span class="wc-off">{{#i18n}}toggle_all_label{{/i18n}}</span>
											</button>
											<button aria-controls="{$subRowControlList}" class="wc-menuitem wc_seltog wc-nobutton wc-invite"
												data-wc-value="none" role="menuitemradio" type="button">
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
												<i aria-hidden="true" class="fa fa-square-o"/>
												<span class="wc-off">{{#i18n}}toggle_none_label{{/i18n}}</span>
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
							<xsl:if test="number($rowIsSelectable) eq 1">
								<xsl:call-template name="rowSelectionIcon">
									<xsl:with-param name="myTable" select="$myTable"/>
								</xsl:call-template>
							</xsl:if>
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
				<td class="wc_table_rowexp_container">
					<xsl:if test="ui:subtr">
						<xsl:attribute name="role">button</xsl:attribute>
						<xsl:attribute name="aria-controls">
							<xsl:value-of select="$rowId"/>
						</xsl:attribute>
						<xsl:attribute name="tabindex">0</xsl:attribute>
						<span class="wc-off">{{#i18n}}table_rowExpansion_rowButtonDescription{{/i18n}}</span>
						<xsl:variable name="iconclass">
							<xsl:text>fa-fw fa-caret-</xsl:text>
							<xsl:choose>
								<xsl:when test="ui:subtr/@open">
									<xsl:text>down</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>right</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<i aria-hidden="true" class="fa {$iconclass}"/>
					</xsl:if>
				</td>
			</xsl:if>
			<!-- APPLY ELEMENT TEMPLATES -->
			<xsl:apply-templates select="ui:th | ui:td">
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
		<xsl:variable name="additional">
			<xsl:if test="$myHeader">
				<xsl:value-of select="concat('wc-align-', $myHeader/@align)"/>
			</xsl:if>
		</xsl:variable>
		<th id="{concat($tableId,'_trh',../@rowIndex)}" scope="row" class="{normalize-space(concat('wc-th ', $additional))}">
			<xsl:if test="$myHeader">
				<xsl:attribute name="headers">
					<xsl:value-of select="concat($tableId,'_thh','1')"/>
				</xsl:attribute>
			</xsl:if>

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
		<xsl:variable name="additional">
			<xsl:if test="$colHeaderElement/@align">
				<xsl:value-of select="concat('wc-align-',$colHeaderElement/@align)"/>
			</xsl:if>
		</xsl:variable>
		<td class="{normalize-space(concat('wc-td ', $additional))}">
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
			<xsl:apply-templates />
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
		<xsl:variable name="additional">
			<xsl:if test="number($topRowIsStriped) eq 1">
				<xsl:text>wc_table_stripe</xsl:text>
			</xsl:if>
			<xsl:if test="number($indent) gt 0">
				<xsl:value-of select="concat(' wc_tbl_indent_', $indent)"/>
			</xsl:if>
		</xsl:variable>
		<tr id="{concat($tableId,'_subc',../../@rowIndex)}" role="row" aria-level="{count(ancestor::ui:subtr[ancestor::ui:table[1]/@id eq $tableId]) + 1}" class="{normalize-space(concat('wc-tr ', $additional))}">
			<xsl:if test="number($parentIsClosed) eq 1 or ancestor::ui:subtr[not(@open)]">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
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
				<xsl:apply-templates />
			</td>
		</tr>
	</xsl:template>

	<!--
		Table Actions
	-->
	<xsl:template match="ui:actions">
		<div class="wc-actions">
			<xsl:apply-templates select="ui:action/html:button"/>
		</div>
	</xsl:template>

	<xsl:template match="ui:action" mode="JS">
		<xsl:text>{"trigger":"</xsl:text>
		<xsl:value-of select="html:button/@id"/>
		<xsl:text>"</xsl:text>
		<xsl:if test="ui:condition">
			<xsl:text>,"conditions":[</xsl:text>
			<xsl:apply-templates select="ui:condition" mode="action"/>
			<xsl:text>]</xsl:text>
		</xsl:if>
		<xsl:text>}</xsl:text>
		<xsl:if test="position() ne last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
	<!--
		Outputs a comma separated list of JSON objects stored in a button attribute which is used to determine whether
		the action's conditions are met before undertaking the action.
	-->
	<xsl:template match="ui:condition" mode="action">
		<xsl:text>{</xsl:text>
		<xsl:if test="@minSelectedRows">
			<xsl:text>"min":</xsl:text>
			<xsl:value-of select="@minSelectedRows"/>
			<xsl:text>,</xsl:text>
		</xsl:if>
		<xsl:if test="@maxSelectedRows">
			<xsl:text>"max":</xsl:text>
			<xsl:value-of select="@maxSelectedRows"/>
			<xsl:text>,</xsl:text>
		</xsl:if>
		<xsl:if test="ancestor::ui:table[1]/ui:pagination">
			<xsl:text>"otherSelected":</xsl:text>
			<xsl:choose>
				<xsl:when test="@selectedOnOther">
					<xsl:value-of select="@selectedOnOther"/>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="@selectedOnOther"/>
			<xsl:text>,</xsl:text>
		</xsl:if>
		<xsl:text>"type":"</xsl:text>
		<xsl:value-of select="@type"/>
		<xsl:text>","message":"</xsl:text>
		<xsl:value-of select="@message"/>
		<xsl:text>"}</xsl:text>
		<xsl:if test="position() ne last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>

	<!--
		1. Guard wrapper for action conditions. These are not output in place but are part of the sibling button's
		attribute data. The ui:action part of the match is to differentiate from ui:subordinate's ui:condition.

		2. Null template for ui:sort. The sort indicators are generated in the ui:table column generation and sort
		controls in ui:thead/ui:th. The ui:sort element itself contains metaData only and does not generate a usable
		HTML artefact.
	-->
	<xsl:template match="ui:action|ui:sort"/>


	<!--
		Transform for the noData child of a tbody. This is (usually) a String so just needs to be wrapped up properly.
	-->
	<xsl:template match="ui:nodata">
		<div class="wc-nodata">
			<xsl:value-of select="."/>
		</div>
	</xsl:template>




	<!--
		Pagination controls
		Structural: do not override.
	-->
	<xsl:template match="ui:pagination">
		<xsl:param name="idSuffix" select="''"/>
		<xsl:variable name="tableId" select="../@id"/>
		<xsl:variable name="name">
			<xsl:value-of select="concat($tableId, '.page')"/>
		</xsl:variable>
		<xsl:variable name="id">
			<xsl:value-of select="concat($name, $idSuffix)"/>
		</xsl:variable>
		<xsl:variable name="pages">
			<xsl:choose>
				<xsl:when test="not(@rowsPerPage)">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="ceiling(@rows div @rowsPerPage)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="number(@rows) gt 0">
			<xsl:if test="number(@rows) gt number(@rowsPerPage)">
				<span class="wc_table_pag_rows" data-wc-tablerpp="{@rowsPerPage}" data-wc-tablerows="{@rows}" data-wc-tablepage="{@currentPage}"></span>
			</xsl:if>
			<label for="{$id}">
				<xsl:text>{{#i18n}}table_pagination_page{{/i18n}}</xsl:text>
				<select id="{$id}" class="wc_table_pag_select" data-wc-pages="{$pages}">
					<!-- NOTE: do not use name or data-wc-name as we do not want to trigger an unsaved changes warning -->
					<xsl:choose>
						<xsl:when test="number($pages) eq 1">
							<xsl:attribute name="disabled">
								<xsl:text>disabled</xsl:text>
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="aria-busy">
								<xsl:text>true</xsl:text>
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<option value="{@currentPage}" selected="selected">
						<xsl:value-of select="@currentPage + 1"/>
					</option>
				</select>
			</label>
			<!-- rows per page chooser -->
			<xsl:if test="not(@mode = 'client')">
				<xsl:apply-templates select="ui:rowsselect">
					<xsl:with-param name="tableId" select="$tableId"/>
					<xsl:with-param name="idSuffix" select="$idSuffix"/>
				</xsl:apply-templates>
			</xsl:if>
			<span class="wc_table_pag_btns">
				<xsl:call-template name="paginationButton">
					<xsl:with-param name="title"><xsl:text>{{#i18n}}table_pagination_button_first{{/i18n}}</xsl:text></xsl:with-param>
					<xsl:with-param name="idSuffix" select="concat($idSuffix,'1')"/>
					<xsl:with-param name="disabled">
						<xsl:choose>
							<xsl:when test="number($pages) eq 1 or number(@currentPage) eq 0">
								<xsl:number value="1"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="0"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="name" select="'f'"/>
				</xsl:call-template>
				<xsl:call-template name="paginationButton">
					<xsl:with-param name="title"><xsl:text>{{#i18n}}table_pagination_button_previous{{/i18n}}</xsl:text></xsl:with-param>
					<xsl:with-param name="idSuffix" select="concat($idSuffix,'2')"/>
					<xsl:with-param name="disabled">
						<xsl:choose>
							<xsl:when test="number($pages) eq 1 or number(@currentPage) eq 0">
								<xsl:number value="1"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="0"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="name" select="'p'"/>
				</xsl:call-template>
				<xsl:call-template name="paginationButton">
					<xsl:with-param name="title"><xsl:text>{{#i18n}}table_pagination_button_next{{/i18n}}</xsl:text></xsl:with-param>
					<xsl:with-param name="idSuffix" select="concat($idSuffix,'3')"/>
					<xsl:with-param name="disabled">
						<xsl:choose>
							<xsl:when test="number($pages) eq 1 or number(@currentPage) eq number($pages) - 1">
								<xsl:number value="1"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="0"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="name" select="'n'"/>
				</xsl:call-template>
				<xsl:call-template name="paginationButton">
					<xsl:with-param name="title"><xsl:text>{{#i18n}}table_pagination_button_last{{/i18n}}</xsl:text></xsl:with-param>
					<xsl:with-param name="idSuffix" select="concat($idSuffix,'4')"/>
					<xsl:with-param name="disabled">
						<xsl:choose>
							<xsl:when test="number($pages) eq 1 or number(@currentPage) eq number($pages) - 1">
								<xsl:number value="1"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="0"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="name" select="'l'"/>
				</xsl:call-template>
			</span>
		</xsl:if>
	</xsl:template>

	<!--
		The template which outputs the four buttons used in table pagination.
		param title: The button title text
		param idSuffix: A string to append to the ID of the button element
		param disabled: 1 if the button should be disabled based on the current page displayed.
	-->
	<xsl:template name="paginationButton">
		<xsl:param name="title"/>
		<xsl:param name="idSuffix"/>
		<xsl:param name="disabled" select="0"/>
		<xsl:param name='name' select="''"/>
		<button id="{concat(../@id,'.pagination.',$idSuffix)}" title="{$title}" type="button" class="wc_btn_icon wc-invite">
			<xsl:if test="number($disabled) eq 1">
				<xsl:attribute name="disabled">
					<xsl:text>disabled</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:variable name="iconclass">
				<xsl:text>fa-fw </xsl:text>
				<xsl:choose>
					<xsl:when test="$name eq 'f'">fa-angle-double-left</xsl:when>
					<xsl:when test="$name eq 'p'">fa-angle-left</xsl:when>
					<xsl:when test="$name eq 'n'">fa-angle-right</xsl:when>
					<xsl:otherwise>fa-angle-double-right</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<i aria-hidden="true" class="fa {$iconclass}"></i>
		</button>
	</xsl:template>

	<!--
		The rows per page selector.
	-->
	<xsl:template match="ui:rowsselect">
		<xsl:param name="tableId"/>
		<xsl:param name="idSuffix"/>
		<xsl:variable name="rppChooserName">
			<xsl:value-of select="concat($tableId,'.rows', $idSuffix)"/>
		</xsl:variable>
		<label for="{$rppChooserName}">
			<xsl:text>{{#i18n}}table_pagination_label_rppChooser{{/i18n}}</xsl:text>
			<!-- NOTE: do not use name or data-wc-name as we do not want to trigger an unsaved changes warning -->
			<select id="{$rppChooserName}" class="wc_table_pag_rpp">
				<xsl:apply-templates select="ui:option" mode="rowsPerPage">
					<xsl:with-param name="rowsPerPage" select="../@rowsPerPage"/>
				</xsl:apply-templates>
			</select>
		</label>
	</xsl:template>

	<!--
		The rows per page options.
	-->
	<xsl:template match="ui:option" mode="rowsPerPage">
		<xsl:param name="rowsPerPage"/>
		<xsl:variable name="value" select="@value"/>
		<option value="{$value}">
			<xsl:if test="number($rowsPerPage) eq number($value)">
				<xsl:attribute name="selected">
					<xsl:text>selected</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="number($value) eq 0">
					<xsl:text>{{#i18n}}table_pagination_option_allRows{{/i18n}}</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$value"/>
				</xsl:otherwise>
			</xsl:choose>
		</option>
	</xsl:template>

	<!--
		Helper as we have a complex arrangement of attributes and content based on silly pathways so we need to make this icon in one of two places.
	-->
	<xsl:template name="rowSelectionIcon">
		<xsl:param name="myTable"/>
		<xsl:variable name="iconclass">
			<xsl:text>fa-fw fa-</xsl:text>
			<xsl:choose>
				<xsl:when test="$myTable/ui:rowselection/@multiple">
					<xsl:if test="@selected">check-</xsl:if>
					<xsl:text>square-o</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="@selected">dot-</xsl:if>
					<xsl:text>circle-o</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<i aria-hidden="true" class="fa {$iconclass}"></i>
	</xsl:template>


	<!--
		Creates the text-mode row select all/none, pagination controls and the expand all/none controls if required.
		Called from the transform for ui:table.

		Reasonably safe to override this template so long as the class attribute values are retained. I would suggest,
		however, leaving it be other than tweaking the order in which the components appear.
	-->
	<xsl:template name="topControls">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="hasExpandAll">
			<xsl:choose>
				<xsl:when test="ui:rowexpansion/@expandAll and .//ui:subtr[ancestor::ui:table[1]/@id eq $id]">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="hasRowSelection">
			<xsl:choose>
				<xsl:when test="ui:rowselection[@selectAll] and ..//ui:tr[not(@unselectable) and ancestor::ui:table[1]/@id eq $id]">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="hasPagination">
			<xsl:choose>
				<xsl:when test="not(ui:pagination) or not(ui:pagination/@controls) or ui:pagination/@controls eq 'bottom'">
					<xsl:number value="0"/>
				</xsl:when>
				<xsl:when test="ui:pagination">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="$hasExpandAll + $hasRowSelection + $hasPagination gt 0">
			<div class="wc_table_top_controls">
				<xsl:if test="number($hasRowSelection) eq 1">
					<div class="wc_table_sel_cont">
						<xsl:apply-templates select="ui:rowselection"/>
					</div>
				</xsl:if>
				<xsl:if test="number($hasExpandAll) eq 1">
					<div class="wc_table_exp_cont">
						<xsl:apply-templates select="ui:rowexpansion"/>
					</div>
				</xsl:if>
				<xsl:if test="number($hasPagination) eq 1">
					<div class="wc_table_pag_cont">
						<xsl:apply-templates select="ui:pagination">
							<xsl:with-param name="idSuffix" select="'top'"/>
						</xsl:apply-templates>
					</div>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>

	<!--
		Creates a container for controls at the bottom of a table.

		You probably do not need to override this but it should be safe to do so so long as any hard-coded class attribute values are left in place.
	-->
	<xsl:template name="tableBottomControls">
		<xsl:variable name="showPagination">
			<xsl:choose>
				<xsl:when test="ui:pagination and not(ui:pagination/@controls eq 'top')">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="ui:actions or number($showPagination) eq 1">
			<div class="wc_table_bottom_controls">
				<xsl:if test="number($showPagination) eq 1">
					<div class="wc_table_pag_cont">
						<xsl:apply-templates select="ui:pagination"/>
					</div>
				</xsl:if>
				<xsl:if test="ui:actions">
					<xsl:apply-templates select="ui:actions"/>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
