
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">

	<xsl:template name="gapClass">
		<xsl:param name="gap" select="''"/>
		<xsl:param name="isVGap" select="0"/>
		<xsl:if test="$gap != ''">
			<xsl:text> wc-</xsl:text><!-- leading space is important -->
			<xsl:choose>
				<xsl:when test="number($isVGap) eq 1">
					<xsl:text>v</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>h</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>gap-</xsl:text>
			<xsl:value-of select="$gap"/>
		</xsl:if>
	</xsl:template>

	<!--
		WPanel is the basic layout component in the framework. Generally output as a "block" container (usually div).
	-->
	<xsl:template match="ui:panel">
		<xsl:param name="type" select="@type"/>
		<xsl:variable name="containerElement">
			<xsl:choose>
				<xsl:when test="$type eq 'chrome' or $type eq 'action'">
					<xsl:text>section</xsl:text>
				</xsl:when>
				<xsl:when test="$type eq 'header' or $type eq 'footer'">
					<xsl:value-of select="$type"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>div</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="additionalClass">
			<xsl:value-of select="@class"/>
			<xsl:choose>
				<xsl:when test="(@mode eq 'lazy' and @hidden)">
					<xsl:text> wc_magic</xsl:text>
				</xsl:when>
				<xsl:when test="@mode eq 'dynamic'">
					<xsl:text> wc_magic wc_dynamic</xsl:text>
				</xsl:when>
			</xsl:choose>
			<xsl:if test="@type">
				<xsl:value-of select="concat(' wc-panel-type-', @type)"/>
			</xsl:if>
			<xsl:apply-templates select="ui:margin" mode="asclass"/>
		</xsl:variable>
		<xsl:element name="{$containerElement}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space(concat('wc-panel ', $additionalClass))"/>
			</xsl:attribute>
			<xsl:if test="@buttonId">
				<xsl:attribute name="data-wc-submit">
					<xsl:value-of select="@buttonId"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$type eq 'header'">
				<xsl:attribute name="role">
					<xsl:text>banner</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@mode">
				<xsl:attribute name="aria-live">
					<xsl:text>polite</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@title and @accessKey">
				<xsl:if test="@title">
					<xsl:attribute name="data-wc-title">
						<xsl:value-of select="@title"/>
					</xsl:attribute>
					<xsl:if test="@accessKey">
						<!-- NOTE: accesskey is a common attribute in HTML 5
							see https://html.spec.whatwg.org/multipage/interaction.html#the-accesskey-attribute
						-->
						<xsl:attribute name="accesskey">
							<xsl:value-of select="@accessKey"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
			</xsl:if>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="*[not(self::ui:margin)]/node() or not(@mode eq 'eager')">
				<xsl:if test="(@type eq 'chrome' or @type eq 'action')">
					<h1>
						<xsl:value-of select="normalize-space(@title)"/>
					</h1>
				</xsl:if>
				<!--
					We have split out preping the child elements into a helper template
					so that implementations can easily override the way templates are
					applied. Call this last.
				-->
				<xsl:apply-templates />
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<!--
		Make the skipLink links to panels which have accessKey and title attributes set.
	-->
	<xsl:template match="ui:panel" mode="skiplinks">
		<a class="wc-skiplink" href="#{@id}">
			<xsl:value-of select="@title"/>
		</a>
	</xsl:template>

	<!-- BorderLayout is a rough CSS emulation of AWT BorderLayout. This Layout is deprecated and will be removed. -->
	<xsl:template match="ui:borderlayout">
		<xsl:variable name="vgap">
			<xsl:if test="@vgap">
				<xsl:call-template name="gapClass">
					<xsl:with-param name="gap" select="@vgap"/>
					<xsl:with-param name="isVGap" select="1"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:variable>
		<div>
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space(concat('wc-borderlayout ', $vgap))"/>
			</xsl:attribute>
			<xsl:apply-templates select="ui:north"/>
			<xsl:if test="count(ui:west|ui:center|ui:east) gt 0">
				<div>
					<xsl:attribute name="class">
						<xsl:text>wc_bl_mid</xsl:text>
						<xsl:if test="@hgap">
							<xsl:call-template name="gapClass">
								<xsl:with-param name="gap" select="@hgap"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:attribute>
					<xsl:apply-templates select="ui:west"/>
					<xsl:apply-templates select="ui:center"/>
					<xsl:apply-templates select="ui:east"/>

				</div>
			</xsl:if>
			<xsl:apply-templates select="ui:south"/>
		</div>
	</xsl:template>

	<!--
		The transform for north and south elements within a ui:borderlayout.
	-->
	<xsl:template match="ui:north|ui:south">
		<div class="wc-{local-name()}">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!--
		The transform for west, center and east elements within a ui:borderlayout.
	-->
	<xsl:template match="ui:east|ui:west|ui:center">
		<div>
			<xsl:attribute name="class">
				<xsl:value-of select="concat('wc-',local-name(.))"/>
				<!--
					IE8 needs more help because it does not know about last child or flex layouts.
					We should be able to remove all this stuff (eventually) when flex-grow: 3 differs from flex-grow: 1 on all target browsers
					(wishful thinking?).
				-->
				<xsl:variable name="colCount" select="count(../ui:west|../ui:east|../ui:center)"/>
				<xsl:variable name="classPrefix">
					<xsl:text> wc_bl_mid</xsl:text>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="number($colCount) eq 1">
						<xsl:value-of select="concat($classPrefix, '100')"/>
					</xsl:when>
					<xsl:when test="(self::ui:west or self::ui:east) and ../ui:center">
						<xsl:value-of select="concat($classPrefix, '25')"/>
					</xsl:when>
					<xsl:when test="(self::ui:east and (../ui:west)) or (self::ui:west and (../ui:east)) or (number($colCount) eq 3 and self::ui:center)">
						<xsl:value-of select="concat($classPrefix, '50')"/>
					</xsl:when>
					<xsl:when test="self::ui:center">
						<xsl:value-of select="concat($classPrefix, '75')"/>
					</xsl:when>
				</xsl:choose>
			</xsl:attribute>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!--
		ui:columnlayout is one of the possible layout child elements of WPanel.
	-->
	<xsl:template match="ui:columnlayout">
		<xsl:variable name="additional">
			<xsl:if test="@vgap">
				<xsl:call-template name="gapClass">
					<xsl:with-param name="gap" select="@vgap"/>
					<xsl:with-param name="isVGap" select="1"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="@align">
				<xsl:value-of select="concat(' wc-align-', @align)"/>
			</xsl:if>
		</xsl:variable>
		<div>
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space(concat('wc-columnlayout ', $additional))"/>
			</xsl:attribute>
			<xsl:variable name="width">
				<xsl:choose>
					<xsl:when test="ui:column[1]/@width">
						<xsl:value-of select="ui:column[1]/@width"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="cols" select="count(ui:column)"/>
			<xsl:choose>
				<xsl:when test="number($cols) eq 1"><!-- I don't know why people do this, but they do -->
					<xsl:apply-templates select="ui:cell" mode="clRow">
						<xsl:with-param name="align" select="ui:column[1]/@align"/>
						<xsl:with-param name="width" select="$width"/>
						<xsl:with-param name="cols" select="$cols"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="ui:cell[position() mod number($cols) eq 1]" mode="clRow">
						<xsl:with-param name="align" select="ui:column[1]/@align"/>
						<xsl:with-param name="width" select="$width"/>
						<xsl:with-param name="cols" select="$cols"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<!--
		param align: the column align property
		param width: the width of the first column
		param cols: the number of columns in each row in the layout.
	-->
	<xsl:template match="ui:cell" mode="clRow">
		<xsl:param name="align"/>
		<xsl:param name="width"/>
		<xsl:param name="cols" select="0"/>
		<div>
			<xsl:attribute name="class">
				<xsl:text>wc-row</xsl:text>
				<xsl:if test = "../@hgap">
					<xsl:call-template name="gapClass">
						<xsl:with-param name="gap" select="../@hgap"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="contains(../../@class, 'wc-respond')">
					<xsl:text> wc-respond</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:variable name="additional">
				<xsl:choose>
					<xsl:when test="not($align) or $align eq ''">
						<xsl:text> wc-align-left</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat(' wc-align-', $align)"/>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="$width and number($width) ne 0">
					<xsl:value-of select="concat(' wc_col_',$width)"/>
				</xsl:if>
			</xsl:variable>
			<div class="{normalize-space(concat('wc-cell wc-column', $additional))}">
				<xsl:apply-templates />
			</div>
			<xsl:if test="number($cols) gt 1">
				<xsl:apply-templates select="following-sibling::ui:cell[position() lt number($cols)]" mode="clInRow"/>
			</xsl:if>
		</div>
	</xsl:template>

	<!--
		Creates columns within a row (except the first). Each column has to look up the alignment and width of its position equivalent ui:column.
	-->
	<xsl:template match="ui:cell" mode="clInRow">
		<!--
			variable colPos
			This variable is used to find the ui:column which holds the meta-data pertinent
			to the column being constructed.

			The columns built in this template are columns 2...n but are called from a
			sibling using following-siblings and therefore their position() is 1...n-1.
			Therefore to match the equivalent ui:column we have to use position() + 1.
		-->
		<xsl:variable name="colPos" select="position() + 1"/>
		<!--
			variable myColumn
			This is a handle to the ui:column sibling of the cell which has position relative
 			to the parent element equal to the value of $colPos calculated above.
		-->
		<xsl:variable name="myColumn" select="../ui:column[position() eq number($colPos)]"/>
		<xsl:variable name="align" select="$myColumn/@align"/>
		<xsl:variable name="width" select="$myColumn/@width"/>
		<xsl:variable name="additional">
			<xsl:choose>
				<xsl:when test="not($align) or $align eq ''">
					<xsl:text> wc-align-left</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(' wc-align-', $align)"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="$width and number($width) ne 0">
				<xsl:value-of select="concat(' wc_col_',$width)"/>
			</xsl:if>
		</xsl:variable>
		<div class="{normalize-space(concat('wc-cell wc-column', $additional))}">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!--
		ui:flowlayout is one of the possible child elements of WPanel

		A flowLayout is used to place elements in a particular linear relationship to
		each other using the align property.
	-->
	<xsl:template match="ui:flowlayout">
		<xsl:variable name="class">
			<xsl:if test="@gap">
				<xsl:call-template name="gapClass">
					<xsl:with-param name="gap" select="@gap"/>
					<xsl:with-param name="isVGap">
						<xsl:choose>
							<xsl:when test="@align and @align eq 'vertical'">
								<xsl:number value="1"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="0"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="@align">
				<xsl:value-of select="concat(' wc-align-', @align)"/>
			</xsl:if>
			<xsl:if test="@valign">
				<xsl:value-of select="concat(' wc_fl_', @valign)"/>
			</xsl:if>
		</xsl:variable>
		<div class="{normalize-space(concat('wc-flowlayout ', $class))}">
			<xsl:apply-templates mode="fl" select="ui:cell[node()]"/>
		</div>
	</xsl:template>

	<!--
		In order to apply flow styles to each cell in a consistent manner we wrap the
		cell content in a div element. This is then able to be styled independently
		of the actual content.
	-->
	<xsl:template match="ui:cell" mode="fl">
		<div class="wc-cell">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--
		Creates a pseudo-grid where each column is the same width and each row is the height of the tallest cell in the row. This is a very rough
		emulation of an AWT GridLayout. ui:gridlayout is one of the possible child elements of ui:panel.

		NOTE: this Layout is being phased out and should be replaced with a more web-focused grid system.

		Child elements

		* ui:cell (minOccurs 0, maxOccurs unbounded) Each component placed into a gridLayout is output in a ui:cell.
		Empty cells are ouput into the UI to maintain grid positioning of content.

		This template determines the order in which cell child elements templates are applied based on calculations of
		rows and columns in the grid. The recursion rules of XSLT 1, and its lack of incrementers etc, mean that when
		applying templates which have to have wrappers around certain elements we have to split the call and make the
		siblings into temporary pseudo-parents. Not as hard as it sounds.
	-->
	<xsl:template match="ui:gridlayout">
		<xsl:if test="ui:cell">
			<xsl:variable name="cols" select="@cols"/>
			<xsl:variable name="rows" select="@rows"/>
			<!--
				The raw number of columns may not give an accurate reflection of the intended state of the grid as @cols
				has an inclusiveMin of 0. For this reason we use @cols if it is greater than 0, if not we look at @rows
				and if it is greater than 0 we calculate the number of columns by ceiling(count(ui:cell) div @rows).
				Otherwise we assume 1 column (which I grant is a bad assumption; maybe ceiling the square-root of the
				number of cells would be better?). Practically the Java API requires at least one of @cols or @rows to
				be non-zero.
			-->
			<xsl:variable name="useCols">
				<xsl:choose>
					<xsl:when test="number($cols) gt 0">
						<xsl:number value="number($cols)"/>
					</xsl:when>
					<xsl:when test="number($rows) gt 0">
						<xsl:value-of select="ceiling(count(ui:cell) div number($rows))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="1"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="vgap">
				<xsl:if test="@vgap">
					<xsl:call-template name="gapClass">
						<xsl:with-param name="gap" select="@vgap"/>
						<xsl:with-param name="isVGap" select="1"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:variable>
			<xsl:variable name="additional">
				<xsl:if test="number($useCols) le 12">
					<xsl:value-of select="concat('wc-gridlayout-col-', $useCols)"/>
				</xsl:if>
				<xsl:value-of select="$vgap"/>
			</xsl:variable>
			<div class="{normalize-space(concat('wc-gridlayout ', $additional))}">
				<xsl:choose>
					<xsl:when test="number($useCols) eq 1">
						<xsl:apply-templates mode="gl" select="ui:cell"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates mode="gl" select="ui:cell[(position() mod number($useCols)) eq 1]">
							<xsl:with-param name="cols" select="number($useCols)"/>
							<xsl:with-param name="colWidth">
								<xsl:if test="number($useCols) gt 12">
									<xsl:value-of select="format-number(1 div number($useCols), '##0.###%')"/>
								</xsl:if>
							</xsl:with-param>
						</xsl:apply-templates>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:if>
	</xsl:template>

	<!--
		This template creates a rows of cells and then applies templates to the cell's following-siblings up to the number of columns in the row.

		param cols: the number of columns in the row. This is used to apply templates on following-siblings up to 1 less than cols (this cell is the
		  first col)
		param colWidth: the width of each cell in the grid.
		param hgap the gridlayouts hgap value.
	-->
	<xsl:template match="ui:cell" mode="gl">
		<xsl:param name="cols" select="1"/>
		<xsl:param name="colWidth"/>
		<xsl:choose>
			<xsl:when test="number($cols) eq 1">
				<xsl:call-template name="gridCell"/>
			</xsl:when>
			<xsl:otherwise>
				<div>
					<xsl:attribute name="class">
						<xsl:text>wc_gl_row</xsl:text>
						<xsl:if test="../@hgap">
							<xsl:call-template name="gapClass">
								<xsl:with-param name="gap" select="../@hgap"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:attribute>
					<xsl:call-template name="gridCell">
						<xsl:with-param name="width" select="$colWidth"/>
					</xsl:call-template>
					<xsl:if test="number($cols) gt 1">
						<xsl:apply-templates mode="inRow" select="following-sibling::ui:cell[position() lt number($cols)]">
							<xsl:with-param name="width" select="$colWidth"/>
						</xsl:apply-templates>
					</xsl:if>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		This template outputs each cell in a row other than the first.

		param width: the width of each cell in the grid (only set if more than 12 cols).
	-->
	<xsl:template match="ui:cell" mode="inRow">
		<xsl:param name="width"/>
		<xsl:call-template name="gridCell">
			<xsl:with-param name="width" select="$width"/>
		</xsl:call-template>
	</xsl:template>

	<!--
		Helper template to create each cell in a gridLayout.

		param width: The width of the cells in percent or '' if cols no more than 12.
	-->
	<xsl:template name="gridCell">
		<xsl:param name="width" select="''"/>
		<div class="wc-cell">
			<xsl:if test="$width ne ''">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('width:', $width, ';')"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Transform for ui:listlayout which is one of the possible child elements of ui:panel. -->
	<xsl:template match="ui:listlayout">
		<xsl:variable name="listElement">
			<xsl:choose>
				<xsl:when test="@ordered">
					<xsl:text>ol</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>ul</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="additionalClasses">
			<xsl:if test="@gap">
				<xsl:call-template name="gapClass">
					<xsl:with-param name="gap" select="@gap"/>
					<xsl:with-param name="isVGap">
						<xsl:choose>
							<xsl:when test="@type eq 'flat'">
								<xsl:number value="0"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="1"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="@align">
					<xsl:value-of select="concat(' wc-align-', @align)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text> wc-align-left</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="@type">
				<xsl:value-of select="concat(' wc-listlayout-type-', @type)"/>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="not(@separator) or @separator eq 'none'">
					<xsl:text> wc_list_nb</xsl:text>
				</xsl:when>
				<xsl:when test="not(@ordered)">
					<xsl:value-of select="concat(' wc-listlayout-separator-', @separator)"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$listElement}">
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space(concat('wc-listlayout ', $additionalClasses))"/>
			</xsl:attribute>
			<xsl:apply-templates mode="ll"/>
		</xsl:element>
	</xsl:template>

	<!--
		This template creates the HTML LI elements and applies the content. If there is no content the cell is omitted.
	-->
	<xsl:template match="ui:cell" mode="ll">
		<xsl:if test="node()">
			<li>
				<xsl:apply-templates />
			</li>
		</xsl:if>
	</xsl:template>

	<!--
		WRow transform.
	-->
	<xsl:template match="ui:row">
		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:if test="@gap">
				<xsl:call-template name="gapClass">
					<xsl:with-param name="gap" select="@gap"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="@align">
				<xsl:value-of select="concat(' wc-align-', @align)"/>
			</xsl:if>
			<xsl:apply-templates select="ui:margin" mode="asclass"/>
		</xsl:variable>
		<div id="{@id}" class="{normalize-space(concat('wc-row ', $additional))}">
			<xsl:apply-templates select="ui:column"/>
		</div>
	</xsl:template>

	<!-- Transform for WColumn. -->
	<xsl:template match="ui:column">
		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:text> wc-align-</xsl:text>
			<xsl:choose>
				<xsl:when test="@align">
					<xsl:value-of select="@align"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>left</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="@width and number(@width) ne 0">
				<xsl:value-of select="concat(' wc_col_',@width)"/>
			</xsl:if>
			<xsl:apply-templates select="ui:margin" mode="asclass"/>
		</xsl:variable>
		<div id="{@id}" class="{normalize-space(concat('wc-column ', $additional))}">
			<xsl:apply-templates select="node()[not(self::ui:margin)]" />
		</div>
	</xsl:template>

	<!--
		ui:content is a child node of a number of components in its most basic form it merely passes through. Some components have their own content
		implementation:

		Generic template for unmoded content elements. Pass content through without any form of wrapper.
	-->
	<xsl:template match="ui:content">
		<xsl:param name="class" select="''"/>
		<div class="{normalize-space(concat('wc-content ', $class))}">
			<xsl:if test="@id">
				<xsl:attribute name="id">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates />
		</div>
	</xsl:template>
</xsl:stylesheet>
