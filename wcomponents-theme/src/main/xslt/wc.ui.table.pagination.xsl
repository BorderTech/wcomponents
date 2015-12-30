<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.table.pagination.n.paginationDescription.xsl"/>
	<xsl:import href="wc.ui.table.n.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<!--
		This template creates the pagination controls. It is called specifically from 
		the tfoot template. Nothing is output if the table consists of only one page.
		
		Pagination controls consist of a labelled SELECT element and four buttons.
	-->
	<xsl:template match="ui:pagination">
		<xsl:param name="idSuffix"/>
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
		<xsl:if test="@rows &gt; 0">
			<xsl:call-template name="paginationDescription"/>
			<xsl:element name="label">
				<xsl:attribute name="for">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
				<xsl:value-of select="$$${wc.ui.table.string.pagination.page}"/>
			</xsl:element>
			<xsl:element name="select">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
				<xsl:attribute name="class">
					<xsl:text>wc_table_pag_select</xsl:text>
				</xsl:attribute>
				<!-- NOTE: do not use name or data-wc-name as we do not want to trigger an unsaved changes warning -->
				<xsl:if test="@mode='dynamic'">
					<xsl:call-template name="tableAjaxController">
						<xsl:with-param name="tableId" select="$tableId"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="$pages=1">
						<xsl:attribute name="disabled">
							<xsl:text>disabled</xsl:text>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="field" select="parent::ui:table"/>
							<xsl:with-param name="isControl" select="1"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:call-template name="pagination.option.for.loop">
					<xsl:with-param name="i" select="0"/>
					<xsl:with-param name="count" select="$pages"/>
					<xsl:with-param name="current" select="@currentPage"/>
				</xsl:call-template>
			</xsl:element>
			<xsl:if test="@mode='server'">
				<!-- TODO: remove when we kill of WDataTable -->
				<xsl:element name="input">
					<xsl:attribute name="type">
						<xsl:text>submit</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="class">
						<xsl:text>wc_table_pag_socbtn</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="value">
						<xsl:value-of select="$$${wc.ui.table.string.pagination.label.serverModeButtonText}"/>
					</xsl:attribute>
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:element>
			</xsl:if>
			
			<!-- rows per page chooser -->
			<xsl:apply-templates select="ui:rowsSelect">
				<xsl:with-param name="tableId" select="$tableId"/>
				<xsl:with-param name="idSuffix" select="$idSuffix"/>
			</xsl:apply-templates>
			
			<!-- buttons to change page -->
			<xsl:variable name="buttonType">
				<xsl:choose>
					<xsl:when test="@mode='server'">
						<xsl:text>submit</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>button</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:call-template name="paginationButton">
				<xsl:with-param name="title" select="$$${wc.ui.table.pagination.message.button.first}"/>
				<xsl:with-param name="type" select="$buttonType"/>
				<xsl:with-param name="idSuffix" select="concat($idSuffix,'1')"/>
				<xsl:with-param name="disabled">
					<xsl:if test="$pages=1 or @currentPage = 0">
						<xsl:number value="1"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="paginationButton">
				<xsl:with-param name="title" select="$$${wc.ui.table.pagination.message.button.previous}"/>
				<xsl:with-param name="type" select="$buttonType"/>
				<xsl:with-param name="idSuffix" select="concat($idSuffix,'2')"/>
				<xsl:with-param name="disabled">
					<xsl:if test="$pages=1 or @currentPage = 0">
						<xsl:number value="1"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="paginationButton">
				<xsl:with-param name="title" select="$$${wc.ui.table.pagination.message.button.next}"/>
				<xsl:with-param name="type" select="$buttonType"/>
				<xsl:with-param name="idSuffix" select="concat($idSuffix,'3')"/>
				<xsl:with-param name="disabled">
					<xsl:if test="$pages=1 or @currentPage = $pages -1">
						<xsl:number value="1"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="paginationButton">
				<xsl:with-param name="title" select="$$${wc.ui.table.pagination.message.button.last}"/>
				<xsl:with-param name="type" select="$buttonType"/>
				<xsl:with-param name="idSuffix" select="concat($idSuffix,'4')"/>
				<xsl:with-param name="disabled">
					<xsl:if test="$pages=1 or @currentPage = $pages -1">
						<xsl:number value="1"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
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
		<xsl:param name="disabled"/>
		<xsl:element name="button">
			<xsl:attribute name="id">
				<xsl:value-of select="concat(../@id,'.pagination.',$idSuffix)"/>
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:value-of select="$title"/>
			</xsl:attribute>
			<xsl:attribute name="type">
				<xsl:value-of select="$type"/>
			</xsl:attribute>
			<xsl:if test="$type='submit'">
				<xsl:attribute name="formnovalidate">
					<xsl:text>formnovalidate</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="class">
				<xsl:text>wc_ibtn</xsl:text>
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test = "$disabled = 1">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="field" select="parent::ui:table"/>
						<xsl:with-param name="isControl" select="1"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>

	<!--*
	 This is a recursive template to create the options in the pagination SELECT
	 element.

	param i an iterator
	param count the total number of pages
	param current the index of the current page, used to mark the option as selected
	-->
	<xsl:template name="pagination.option.for.loop">
		<xsl:param name="i"/>
		<xsl:param name="count"/>
		<xsl:param name="current"/>
		<xsl:if test="$i &lt; $count">
			<xsl:element name="option">
				<xsl:attribute name="value">
					<xsl:value-of select="$i"/>
				</xsl:attribute>
				<xsl:if test="$i = $current">
					<xsl:attribute name="selected">selected</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="$i + 1"/>
			</xsl:element>
			<xsl:call-template name="pagination.option.for.loop">
				<xsl:with-param name="i" select="$i + 1"/>
				<xsl:with-param name="count" select="$count"/>
				<xsl:with-param name="current" select="$current"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
