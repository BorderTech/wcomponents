
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!--
		Template match="ui:selecttoggle" mode="JS"

		This template creates JSON objects required to register named group
		controllers.
	-->
	<xsl:template match="ui:selecttoggle" mode="JS">
		<xsl:text>{"identifier":"</xsl:text>
		<xsl:value-of select="@id"/>
		<xsl:text>","groupName":"</xsl:text>
		<xsl:value-of select="@target"/>
		<xsl:text>"}</xsl:text>
		<xsl:if test="position() ne last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>



	<!--
		Builds selectToggle/rowSelection controls.
			wc.ui.selectToggle.xsl
			wc.ui.table.rowSelection.xsl
	-->
	<xsl:template name="selectToggle">
		<xsl:param name="id" select="@id"/>
		<xsl:param name="name" select="''"/>
		<xsl:param name="for" select="''"/>
		<xsl:param name="selected" select="''"/>
		<xsl:param name="type" select="'text'"/>
		<xsl:variable name="toggleId">
			<xsl:value-of select="$id"/>
			<xsl:if test="not(self::ui:selecttoggle)">
				<xsl:text>_st</xsl:text>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="baseClass">
			<xsl:value-of select="concat('wc-', local-name(), ' wc_seltog ', @class)"/>
			<xsl:if test="self::ui:selecttoggle">
				<xsl:if test="@type">
					<xsl:value-of select="concat(' wc-selecttoggle-type-', @type)"/>
				</xsl:if>
			</xsl:if>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$type eq 'text'">
				<span id="{$toggleId}" role="radiogroup" data-wc-target="{$for}" class="{normalize-space($baseClass)}">
					<xsl:if test="self::ui:selecttoggle">
						<xsl:if test="@disabled"><xsl:attribute name="aria-disabled">true</xsl:attribute></xsl:if>
					</xsl:if>
					<xsl:variable name="subClass">
						<xsl:value-of select="concat('wc_', local-name(.), ' wc_seltog')"/>
					</xsl:variable>
					<xsl:call-template name="toggleElement">
						<xsl:with-param name="id" select="concat($id,'-all')"/>
						<xsl:with-param name="name" select="$name"/>
						<xsl:with-param name="value" select="'all'"/>
						<xsl:with-param name="class" select="$subClass"/>
						<xsl:with-param name="text"><xsl:text>{{#i18n}}toggle_all{{/i18n}}</xsl:text></xsl:with-param>
						<xsl:with-param name="selected">
							<xsl:choose>
								<xsl:when test="$selected eq 'all'">
									<xsl:number value="1"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:number value="0"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="toggleElement">
						<xsl:with-param name="id" select="concat($id,'-none')"/>
						<xsl:with-param name="name" select="$name"/>
						<xsl:with-param name="value" select="'none'"/>
						<xsl:with-param name="class" select="$subClass"/>
						<xsl:with-param name="text"><xsl:text>{{#i18n}}toggle_none{{/i18n}}</xsl:text></xsl:with-param>
						<xsl:with-param name="selected">
							<xsl:choose>
								<xsl:when test="$selected eq 'none'">
									<xsl:number value="1"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:number value="0"></xsl:number>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
					</xsl:call-template>
				</span>
			</xsl:when>
			<xsl:otherwise>
				<button id="{$toggleId}" role="checkbox" type="button" data-wc-target="{$for}" class="{normalize-space(concat($baseClass, '  wc-nobutton wc-invite'))}">
					<xsl:attribute name="aria-checked">
						<xsl:choose>
							<xsl:when test="$selected eq 'all'">
								<xsl:text>true</xsl:text>
							</xsl:when>
							<xsl:when test="$selected eq 'some'">
								<xsl:text>mixed</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>false</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<!--
						The controls must have a name surrogate to report back to the server. We **do not** use a value surrogate because the value is
						determined at the time we write the state based on the aria-checked state of the control(s).
					-->
					<xsl:if test="$name ne ''">
						<xsl:attribute name="data-wc-name">
							<xsl:value-of select="$name"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="self::ui:selecttoggle">
						<xsl:if test="@disabled"><xsl:attribute name="disabled"><xsl:text>disabled</xsl:text></xsl:attribute></xsl:if>
					</xsl:if>
					<xsl:variable name="iconclass">
						<xsl:text>fa-</xsl:text>
						<xsl:choose>
							<xsl:when test="$selected eq 'all'">
								<xsl:text>check-square-o</xsl:text>
							</xsl:when>
							<xsl:when test="$selected eq 'some'">
								<xsl:text>square</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>square-o</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<i aria-hidden="true" class="fa {$iconclass}"></i>
				</button>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Helper templates and keys for common state toggling elements. -->
	<xsl:template name="toggleElement">
		<xsl:param name="id" select="@id"/>
		<xsl:param name="name" select="''"/>
		<xsl:param name="value" select="''"/>
		<xsl:param name="text" select="''"/>
		<xsl:param name="class" select="''"/>
		<xsl:param name="selected" select="0"/>
		<xsl:variable name="localClass">
			<xsl:text>wc-linkbutton</xsl:text>
			<xsl:if test="$class ne ''">
				<xsl:value-of select="concat(' ',$class)"/>
			</xsl:if>
		</xsl:variable>
		<button id="{$id}" role="radio" class="{$localClass}" data-wc-value="{$value}" type="button">
			<xsl:if test="$name ne ''">
				<xsl:attribute name="data-wc-name">
					<xsl:value-of select="$name"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="aria-checked">
				<xsl:choose>
					<xsl:when test="number($selected) eq 1">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test="self::ui:selecttoggle"><!-- WCollapsibleToggle does not have a disabled state. -->
					<xsl:if test="@disabled"><xsl:attribute name="disabled"><xsl:text>disabled</xsl:text></xsl:attribute></xsl:if>
				</xsl:when>
				<xsl:when test="not(self::ui:rowselection)">
					<xsl:variable name="iconclass">
						<xsl:choose>
							<xsl:when test="$value eq 'expand'">
								<xsl:text>fa-plus-square-o</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>fa-minus-square-o</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<i aria-hidden="true" class="fa {$iconclass}"></i>
				</xsl:when>
			</xsl:choose>
			<xsl:value-of select="$text"/>
		</button>
	</xsl:template>





	<!-- Transform for WSelectToggle. -->
	<xsl:template match="ui:selecttoggle">
		<xsl:choose>
			<xsl:when test="@renderAs eq 'control'">
				<span id="{@id}" class="{normalize-space(concat('wc-selecttoggle wc-input-wrapper ', @class))}">
					<xsl:call-template name="selectToggle">
						<xsl:with-param name="id" select="concat(@id, '_input')"/>
						<xsl:with-param name="for" select="@target"/>
						<xsl:with-param name="name" select="@id"/>
						<xsl:with-param name="selected" select="@selected"/>
						<xsl:with-param name="type">
							<xsl:text>control</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
				</span>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="selectToggle">
					<xsl:with-param name="for" select="@target"/>
					<xsl:with-param name="name" select="@id"/>
					<xsl:with-param name="selected" select="@selected"/>
					<xsl:with-param name="type">
						<xsl:text>text</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template match="ui:collapsibletoggle">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="for" select="@groupName"/>
		<xsl:variable name="mode">
			<xsl:choose>
				<xsl:when test="@mode and (@mode eq 'dynamic' or @mode eq 'lazy')">
					<xsl:value-of select="@mode"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>client</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!--
			WCollapsibleToggle has a mandatory groupName attribute.

			This may not point to a CollapsibleGroup (which is odd and should change) and in this case the WCollapsibleToggle should toggle every
			WCollapsible on the page.
		-->
		<xsl:variable name="toggleClass">wc_collapsibletoggle</xsl:variable>
		<ul id="{$id}" role="radiogroup" class="{normalize-space(concat('wc-collapsibletoggle wc_coltog ', @class))}">
			<xsl:attribute name="data-wc-group">
				<xsl:value-of select="$for"/>
			</xsl:attribute>
			<li>
				<xsl:call-template name="toggleElement">
					<xsl:with-param name="id" select="concat($id,'-ex')"/>
					<xsl:with-param name="name" select="$id"/>
					<xsl:with-param name="value" select="'expand'"/>
					<xsl:with-param name="text"><xsl:text>{{#i18n}}expandall{{/i18n}}</xsl:text></xsl:with-param>
					<xsl:with-param name="class" select="'wc_collapsibletoggle'"/>
				</xsl:call-template>
			</li>
			<li>
				<xsl:call-template name="toggleElement">
					<xsl:with-param name="id" select="concat($id,'-col')"/>
					<xsl:with-param name="name" select="$id"/>
					<xsl:with-param name="value" select="'collapse'"/>
					<xsl:with-param name="text"><xsl:text>{{#i18n}}collapseall{{/i18n}}</xsl:text></xsl:with-param>
					<xsl:with-param name="class" select="'wc_collapsibletoggle'"/>
				</xsl:call-template>
			</li>
		</ul>
	</xsl:template>


	<!--
		ui:rowexpansion controls the mode of the expandable rows and whether the expand/collapse all controls are
		visible. This template outputs those controls. It is called explicitly from the template name `topControls`.

		Structural: do not override.
	-->
	<xsl:template match="ui:rowexpansion">
		<xsl:variable name="tableId" select="../@id"/>
		<xsl:variable name="id" select="concat($tableId, '_texall')"/>
		<!--
			NOTE: the guard code testing for the existance of collapsible rows in this template is a belt-and-braces fix
			for slack front end developers. We have had genuine cases where applications have been built with
			ui:rowexpansion with @expandAll set to show the collapse/expand controls but with no collapsible rows in
			the table and then bugs raised that the expand/collapse all controls don't seem to do anything!
		 -->
		<xsl:if test="..//ui:subtr[ancestor::ui:table[1]/@id eq $tableId]">
			<xsl:variable name="mode">
				<xsl:choose>
					<xsl:when test="@mode and (@mode eq 'dynamic' or @mode eq 'lazy')">
						<xsl:value-of select="@mode"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>client</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<ul id="{$id}" role="radiogroup" class="wc-rowexpansion wc_coltog">
				<xsl:attribute name="data-wc-group">
					<xsl:value-of select="$tableId"/>
				</xsl:attribute>
				<li>
					<xsl:call-template name="toggleElement">
						<xsl:with-param name="id" select="concat($id,'-ex')"/>
						<xsl:with-param name="name" select="$id"/>
						<xsl:with-param name="value" select="'expand'"/>
						<xsl:with-param name="text"><xsl:text>{{#i18n}}expandall{{/i18n}}</xsl:text></xsl:with-param>
						<xsl:with-param name="class" select="'wc_rowexpansion'"/>
					</xsl:call-template>
				</li>
				<li>
					<xsl:call-template name="toggleElement">
						<xsl:with-param name="id" select="concat($id,'-col')"/>
						<xsl:with-param name="name" select="$id"/>
						<xsl:with-param name="value" select="'collapse'"/>
						<xsl:with-param name="text"><xsl:text>{{#i18n}}collapseall{{/i18n}}</xsl:text></xsl:with-param>
						<xsl:with-param name="class" select="'wc_rowexpansion'"/>
					</xsl:call-template>
				</li>
			</ul>
		</xsl:if>
	</xsl:template>


	<!--
		This template creates the rowSelection (select all, select none) controls if required. It is called explicitly from the template named
		`topControls`. If there are no selectable rows then nothing is output.

		NOTE: This template does not make the individual rows selectable. That is done in the transform of ui:tr.

		Structural: do not override.
	-->
	<xsl:template match="ui:rowselection">
		<xsl:variable name="tableId" select="../@id"/>
		<xsl:variable name="numberOfRows" select="count(..//ui:tr[not(@unselectable) and ancestor::ui:table[1]/@id eq $tableId])"/>
		<xsl:if test="number($numberOfRows) gt 0">
			<xsl:variable name="numberSelectedRows" select="count(..//ui:tr[@selected and ancestor::ui:table[1]/@id eq $tableId])"/>
			<xsl:variable name="selected">
				<xsl:choose>
					<xsl:when test="number($numberOfRows) eq 0">
						<xsl:text>none</xsl:text>
					</xsl:when>
					<xsl:when test="@toggle">
						<!--
							When in parent row is a select toggle mode any row which is selected but has descendant rows
							(in the same table)  which are not selected is **deemed to be unselected**.

							This is a horrible calculation and I wish I did not have to do it.
						-->
						<xsl:variable name="numberUnselectedParentRows"
							select="count(..//ui:tr[@selected and
							ancestor::ui:table[1]/@id eq $tableId and
							.//ui:subtr[ancestor::ui:table[1]/@id eq $tableId]/ui:tr[not(@unselectable or @selected)]])"/>
						<xsl:choose>
							<xsl:when test="number($numberSelectedRows) eq number($numberUnselectedParentRows)">
								<xsl:text>none</xsl:text>
							</xsl:when>
							<xsl:when test="number($numberUnselectedParentRows) eq 0 and number($numberSelectedRows) eq number($numberOfRows)">
								<xsl:text>all</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>some</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:when test="number($numberOfRows) eq number($numberSelectedRows)">
						<xsl:text>all</xsl:text>
					</xsl:when>
					<xsl:when test="count(..//ui:tr[@selected]) eq 0">
						<xsl:text>none</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>some</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="bodyId" select="concat(../@id,'_tb')"/>
			<xsl:call-template name="selectToggle">
				<xsl:with-param name="id" select="$bodyId"/>
				<xsl:with-param name="for" select="$bodyId"/>
				<xsl:with-param name="selected" select="$selected"/>
				<xsl:with-param name="type" select="@selectAll"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--
		Outputs a comma separated list of JSON objects required for registering
		the selection controls. See wc.common.registrationScripts.xsl.
	-->
	<xsl:template match="ui:rowselection" mode="JS">
		<xsl:text>{"identifier":"</xsl:text>
		<xsl:value-of select="concat(../@id,'_tb','_st')"/>
		<xsl:text>","groupName":"</xsl:text>
		<xsl:value-of select="concat(../@id,'_tb')"/>
		<xsl:text>"}</xsl:text>
		<xsl:if test="position() ne last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
