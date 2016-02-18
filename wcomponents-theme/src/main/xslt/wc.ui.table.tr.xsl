<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.aria.live.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<xsl:import href="wc.common.offscreenSpan.xsl"/>
	<xsl:import href="wc.ui.table.n.xsl"/>
	<xsl:import href="wc.ui.table.tr.n.clientRowClosedHelper.xsl"/>
	<!--
		Transform for each row in the WTable. The row itself transforms to a HTML tr element. It may also output another
		row if it has a ui:subtr child.

		param myTable: The first table ancestor of the current row. This is determined at the most efficient point
		(usually ui:tbody using its parent node) and then passed through all subsequent transforms to save constant
		ancestor::ui:table[1] lookups.

		param parentIsClosed default 0, 1 indicates that the row's parent row exists and is in a collapsed state.
	-->
	<xsl:template match="ui:tr">
		<xsl:param name="myTable"/>
		<xsl:param name="hasRole" select="0"/>
		<xsl:param name="parentIsClosed" select="0"/>
		<xsl:param name="topRowIsStriped" select="0"/>

		<xsl:variable name="tableId" select="$myTable/@id"/>
		<xsl:variable name="rowId" select="concat($tableId,'-',@rowIndex)"/>

		<xsl:variable name="selectableRow">
			<xsl:if test="$myTable/ui:rowselection">
				<xsl:value-of select="1"/>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="hasRowExpansion">
			<xsl:if test="$myTable/ui:rowexpansion">
				<xsl:value-of select="1"/>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="indent">
			<xsl:choose>
				<xsl:when test="$hasRowExpansion=1 and $myTable/@type='hierarchic' and parent::ui:subtr">
					<xsl:value-of select="count(ancestor::ui:subtr[ancestor::ui:table[1] = $myTable])"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>


		<xsl:variable name="removeRow">
			<xsl:choose>
				<xsl:when test="$parentIsClosed=1 or @hidden=$t or parent::ui:subtr[not(@open=$t)] or (ancestor::ui:subtr[not(@open=$t) and ancestor::ui:table[1]/@id=$tableId])">
					<xsl:value-of select="1"/>
				</xsl:when>
				<xsl:when test="parent::ui:tbody and $myTable/ui:pagination/@mode = 'client'">
					<xsl:call-template name="clientRowClosedHelper">
						<xsl:with-param name="myTable" select="$myTable"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="isSelectToggle">
			<xsl:if test="$hasRowExpansion + $selectableRow = 2 and $myTable/ui:rowselection/@toggle and ui:subtr/ui:tr[not(@unselectable)]">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>

		<tr id="{$rowId}" data-wc-rowindex="{@rowIndex}">
			<xsl:if test="$hasRole &gt; 0">
				<xsl:attribute name="role">row</xsl:attribute>
			</xsl:if>

			<xsl:attribute name="class">
				<xsl:call-template name="commonClassHelper"/>
				<xsl:choose>
					<xsl:when test="parent::ui:tbody">
						<xsl:if test="$myTable/@striping='rows' and position() mod 2 = 0">
							<xsl:text> wc_table_stripe</xsl:text>
						</xsl:if>
						<xsl:if test="$myTable/ui:pagination">
							<xsl:text> wc_table_pag_row</xsl:text>
						</xsl:if>
					</xsl:when>
					<xsl:when test="$topRowIsStriped=1">
						<xsl:text> wc_table_stripe</xsl:text>
					</xsl:when>
				</xsl:choose>
				<!--<xsl:if test="$isSelectToggle = 1">
					<xsl:text> wc_seltog</xsl:text>
				</xsl:if>-->
			</xsl:attribute>

			<xsl:if test="$hasRowExpansion=1">
				<xsl:if test="ui:subtr">
					<xsl:attribute name="aria-expanded">
						<xsl:choose>
							<xsl:when test="ui:subtr/@open=$t">
								<xsl:copy-of select="$t"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>false</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>

					<xsl:variable name="expMode" select="$myTable/ui:rowexpansion/@mode"/>

					<xsl:variable name="isOpen">
						<xsl:if test="ui:subtr/@open=$t">
							<xsl:value-of select="1"/>
						</xsl:if>
					</xsl:variable>

					<xsl:attribute name="aria-controls">
						<xsl:choose>
							<xsl:when test="ui:subtr/ui:tr">
								<xsl:apply-templates select="ui:subtr/ui:tr" mode="subRowControlIdentifier">
									<xsl:with-param name="tableId" select="$tableId"/>
								</xsl:apply-templates>
							</xsl:when>
							<xsl:when test="ui:subtr/ui:content">
								<xsl:value-of select="concat($tableId,'${wc.ui.table.id.subTr.content.suffix}',@rowIndex)"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="concat($tableId,'${wc.ui.table.id.subTr.suffix}',@rowIndex)"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>

					<xsl:variable name="expansionMode">
						<xsl:choose>
							<xsl:when test="($expMode='lazy' or $expMode='eager') and $isOpen=1">
								<xsl:text>client</xsl:text>
							</xsl:when>
							<xsl:when test="$expMode='eager'">
								<xsl:text>lazy</xsl:text>
							</xsl:when>
							<xsl:when test="$expMode">
								<xsl:value-of select="$expMode"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>client</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:if test="$expansionMode='lazy' or $expansionMode='dynamic'">
						<xsl:attribute name="data-wc-ajaxalias">
							<xsl:value-of select="$tableId"/>
						</xsl:attribute>
						<xsl:if test="$expansionMode='lazy'">
							<xsl:attribute name="data-wc-expmode">
								<xsl:value-of select="$expansionMode"/>
							</xsl:attribute>
						</xsl:if>
					</xsl:if>
				</xsl:if>

				<xsl:if test="parent::ui:subtr">
					<xsl:call-template name="setARIALive"/>
				</xsl:if>

				<xsl:attribute name="aria-level">
					<xsl:value-of select="count(ancestor::ui:subtr[ancestor::ui:table[1]/@id=$tableId]) + 1"/>
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
			<xsl:if test="$selectableRow=1 and not(@unselectable=$t)">
				<xsl:attribute name="aria-selected">
					<xsl:choose>
						<xsl:when test="$isSelectToggle=1 and .//ui:subtr[ancestor::ui:table[1]/@id = $tableId]/ui:tr[not(@unselectable or @selected)]">
							<!-- If I am a select toggle row for my 'descendant' rows and one or more of these are not selected then I am not selected. -->
							<xsl:text>false</xsl:text>
						</xsl:when>
						<xsl:when test="$isSelectToggle = 1 or @selected = $t">
							<xsl:copy-of select="$t"/>
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
					<xsl:value-of select="concat($tableId,'${wc.ui.table.rowSelect.state.suffix}')"/>
				</xsl:attribute>

				<xsl:attribute name="data-wc-value">
					<xsl:value-of select="@rowIndex"/>
				</xsl:attribute>

				<!--<xsl:if test="ui:subtr/ui:tr and $myTable/ui:rowselection/@toggle">
					<xsl:attribute name="wc-data-toggle">
						<xsl:text>1</xsl:text>
					</xsl:attribute>
				</xsl:if>-->

				<!-- WDataTable still needs disabled support -->
				<xsl:choose>
					<xsl:when test="@disabled">
						<xsl:call-template name="disabledElement"/>
					</xsl:when>
					<xsl:when test="$myTable/@disabled">
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="field" select="$myTable"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:if>

			<xsl:if test="$removeRow=1">
				<xsl:call-template name="hiddenElement"/>
			</xsl:if>

			<xsl:choose>
				<xsl:when test="@disabled">
					<xsl:call-template name="disabledElement"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="field" select="$myTable"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
			<!-- END OF TR ATTRIBUTES -->


			<!--
			rowSelection indicator wrapper

			 This cell is actually an empty cell which is used as a placeholder to display
			 the row selection mechanism and state indicators.
			-->
			<xsl:if test="$selectableRow=1">
				<td class="wc_table_sel_wrapper" aria-hidden="true">
					<xsl:if test="$isSelectToggle=1">
						<xsl:variable name="subRowToggleControlId" select="concat($rowId, '_toggleController')"/>
						<xsl:variable name="subRowToggleControlButtonId" select="concat($subRowToggleControlId, '_showbtn')"/>
						<xsl:variable name="subRowToggleControlContentId" select="concat($subRowToggleControlId, '_content')"/>

						<div class="wc-menu flyout" role="menubar" id="{$subRowToggleControlId}">
							<div class="wc-submenu" role="menuitem">
								<button type="button" aria-haspopup="true" class="wc_btn_nada" id="{$subRowToggleControlButtonId}" aria-controls="{$subRowToggleControlContentId}">
									<span class="wc_off"><xsl:value-of select="$$${wc.ui.table.string.rowSelection.label}"/></span>
								</button>
								<div class="wc_submenucontent wc_seltog" role="menu" aria-expanded="false" id="{$subRowToggleControlContentId}" aria-labelledby="{$subRowToggleControlButtonId}">
									<xsl:variable name="allSelectableSubRows" select="count(.//ui:subtr[ancestor::ui:table[1]/@id = $tableId]/ui:tr[not(@unselectable)])"/>
									<xsl:variable name="allUnselectedSubRows" select="count(.//ui:subtr[ancestor::ui:table[1]/@id = $tableId]/ui:tr[not(@unselectable or @selected)])"/>
									<xsl:variable name="subRowControlList">
										<xsl:value-of select="concat($rowId, ' ')"/><!-- these controllers control this row too -->
										<xsl:apply-templates select="ui:subtr//ui:tr[ancestor::ui:table[1]/@id = $tableId]" mode="subRowControlIdentifier">
											<xsl:with-param name="tableId" select="$tableId"/>
										</xsl:apply-templates>
									</xsl:variable>
									<button type="button" role="menuitemradio" class="wc-menuitem wc_seltog wc_btn_nada" aria-controls="{$subRowControlList}" data-wc-value="all">
										<xsl:attribute name="aria-checked">
											<xsl:choose>
												<xsl:when test="$allUnselectedSubRows = 0">
													<xsl:text>true</xsl:text>
												</xsl:when>
												<xsl:otherwise>
													<xsl:text>false</xsl:text>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:attribute>
										<span class="wc_off"><xsl:value-of select="$$${wc.common.toggles.i18n.select.label}"/> </span>
										<xsl:value-of select="$$${wc.common.toggles.i18n.selectAll}"/>
									</button>
									<button type="button" role="menuitemradio" class="wc-menuitem wc_seltog wc_btn_nada" aria-controls="{$subRowControlList}"  data-wc-value="none">
										<xsl:attribute name="aria-checked">
											<xsl:choose>
												<xsl:when test="$allSelectableSubRows = $allUnselectedSubRows">
													<xsl:text>true</xsl:text>
												</xsl:when>
												<xsl:otherwise>
													<xsl:text>false</xsl:text>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:attribute>
										<span class="wc_off"><xsl:value-of select="$$${wc.common.toggles.i18n.select.label}"/> </span>
										<xsl:value-of select="$$${wc.common.toggles.i18n.selectNone}"/>
									</button>
								</div>
							</div>
						</div>
					</xsl:if>
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
						<xsl:call-template name="offscreenSpan">
							<xsl:with-param name="text">
								<xsl:value-of select="$$${wc.ui.table.rowExpansion.message.collapser}"/>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
				</td>
			</xsl:if>

			<xsl:apply-templates select="ui:th|ui:td">
				<xsl:with-param name="myTable" select="$myTable"/>
				<xsl:with-param name="indent" select="$indent"/>
				<xsl:with-param name="hasRole" select="$hasRole"/>
			</xsl:apply-templates>
		</tr>
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
					<xsl:when test="$topRowIsStriped=1 or (parent::ui:tbody and $myTable/@striping='rows' and position() mod 2 = 0)">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="hasRole" select="$hasRole"/>
		</xsl:apply-templates>
	</xsl:template>
</xsl:stylesheet>
