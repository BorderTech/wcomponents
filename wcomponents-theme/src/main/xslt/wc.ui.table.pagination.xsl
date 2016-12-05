<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.ui.table.n.tableAjaxController.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<!--
		This template creates the pagination controls. It is called specifically from 
		the tfoot template. Nothing is output if the table consists of only one page.
		
		Pagination controls consist of a labelled SELECT element and four buttons.

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
				<xsl:text>{{t 'table_pagination_page'}}</xsl:text>
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
								<xsl:copy-of select="$t"/>
							</xsl:attribute>
							<xsl:call-template name="disabledElement"><!-- WDataTable compatibility only -->
								<xsl:with-param name="field" select="parent::ui:table"/>
								<xsl:with-param name="isControl" select="1"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
					<option value="{@currentPage}" selected="selected">
						<xsl:value-of select="@currentPage + 1"/>
					</option>
				</select>
			</label>
			
			<!-- rows per page chooser -->
			<xsl:apply-templates select="ui:rowsselect">
				<xsl:with-param name="tableId" select="$tableId"/>
				<xsl:with-param name="idSuffix" select="$idSuffix"/>
			</xsl:apply-templates>
			
			<!-- buttons to change page -->
			<xsl:variable name="buttonType">
				<xsl:choose>
					<xsl:when test="@mode eq 'server'"><!-- WDataTable compatibility only -->
						<xsl:text>submit</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>button</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<span class="wc_table_pag_btns">
				<xsl:call-template name="paginationButton">
					<xsl:with-param name="title"><xsl:text>{{t 'table_pagination_button_first'}}</xsl:text></xsl:with-param>
					<xsl:with-param name="type" select="$buttonType"/>
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
				</xsl:call-template>
				<xsl:call-template name="paginationButton">
					<xsl:with-param name="title"><xsl:text>{{t 'table_pagination_button_previous'}}</xsl:text></xsl:with-param>
					<xsl:with-param name="type" select="$buttonType"/>
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
				</xsl:call-template>
				<xsl:call-template name="paginationButton">
					<xsl:with-param name="title"><xsl:text>{{t 'table_pagination_button_next'}}</xsl:text></xsl:with-param>
					<xsl:with-param name="type" select="$buttonType"/>
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
				</xsl:call-template>
				<xsl:call-template name="paginationButton">
					<xsl:with-param name="title"><xsl:text>{{t 'table_pagination_button_last'}}</xsl:text></xsl:with-param>
					<xsl:with-param name="type" select="$buttonType"/>
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
				</xsl:call-template>
			</span>
		</xsl:if>
	</xsl:template>

	<!--
		The template which outputs the four buttons used in table pagination.

		param name: The button name, used for server pagination
		param title: The button title text
		param class: The class to apply to the button element
		param type: The button type: "button" or "submit". Type is "submit" for server pagination.
		param idSuffix: A string to append to the ID of the button element
		param disabled: 1 if the button should be disabled based on the current page displayed.
	-->
	<xsl:template name="paginationButton">
		<xsl:param name="title"/>
		<xsl:param name="type"/>
		<xsl:param name="idSuffix"/>
		<xsl:param name="disabled" select="0"/>
		<button id="{concat(../@id,'.pagination.',$idSuffix)}" title="{$title}" type="{$type}" class="wc_btn_icon wc-invite">
			<xsl:if test="$type eq 'submit'">
				<xsl:attribute name="formnovalidate">
					<xsl:text>formnovalidate</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="number($disabled) eq 1">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise><!-- WDataTable compatibility only -->
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="field" select="parent::ui:table"/>
						<xsl:with-param name="isControl" select="1"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
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
			<xsl:text>{{t 'table_pagination_label_rppChooser'}}</xsl:text>
			<select id="{$rppChooserName}" class="wc_table_pag_rpp">
				<!-- NOTE: do not use name or data-wc-name as we do not want to trigger an unsaved changes warning -->
				<xsl:call-template name="disabledElement">
					<xsl:with-param name="field" select="ancestor::ui:table[1]"/>
					<xsl:with-param name="isControl" select="1"/>
				</xsl:call-template>
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
					<xsl:text>{{t 'table_pagination_option_allRows'}}</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$value"/>
				</xsl:otherwise>
			</xsl:choose>
		</option>
	</xsl:template>
</xsl:stylesheet>
