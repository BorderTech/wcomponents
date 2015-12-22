<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.toggleElement.xsl"/>
	<xsl:import href="wc.common.ajax.xsl"/>
	<!-- Key used by collapsibleToggle to get the list of controlled collapsibles -->
	<xsl:key name="collapsibleGroupKey" match="//ui:collapsible[@groupName]" use="@groupName"/>
	<!--
		Output expand all and collapse all buttons. Called from ui:rowExpansion 
		and ui:expandCollapseAll. See wc.ui.collapsibleToggle.xsl and
		wc.ui.table.rowExpansion.xsl.
		
		param id: the id of the collapsing control's grouping component. Default
			@id.
		param for: The identifier of the component/group which is controlled by 
			the expand/collapse all buttons. This could be an id (for a single 
			controlled entity), a space delimited id list, or a groupName 
			attribute. This could be empty. Default @groupName.
	-->
	<xsl:template name="collapsibleToggle">
		<xsl:param name="id" select="@id"/>
		<xsl:param name="for" select="@groupName"/>
		<xsl:variable name="mode">
			<xsl:choose>
				<xsl:when test="@roundTrip or @mode='server'">
					<xsl:text>server</xsl:text>
				</xsl:when>
				<xsl:when test="@mode='dynamic' or @mode='lazy'">
					<xsl:value-of select="@mode"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>client</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!--
			WCollapsibleToggle has a mandatory groupName attribute. This may not
			point to a CollapsibleGroup (which is odd and should change). In this
			case the CollapsibleToggle should toggle every control on the page.
		-->
		<xsl:variable name="group" select="key('collapsibleGroupKey', $for)"/>
		<xsl:variable name="targetList">
			<xsl:choose>
				<xsl:when test="$mode='server'">
					<xsl:value-of select="''"/>
					<!--server mode does not need a target list, the expansion is done on the server -->
				</xsl:when>
				<xsl:when test="self::ui:rowExpansion">
					<xsl:value-of select="$for"/>
				</xsl:when>
				<xsl:when test="$group">
					<xsl:apply-templates select="$group" mode="getIdList"/>
				</xsl:when>
				<!-- implicit in this is when self::ui:collapsibleToggle and not($group) the value is '' -->
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="toggleClass">
			<xsl:value-of select="local-name(.)"/>
		</xsl:variable>
		<ul id="{$id}" role="radiogroup">
			<xsl:attribute name="class">
				<xsl:value-of select="local-name()"/>
				<xsl:if test="@class">
					<xsl:value-of select="concat(' ', @class)"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test="self::ui:rowExpansion">
				<xsl:call-template name="disabledElement">
					<xsl:with-param name="field" select=".."/>
				</xsl:call-template>
			</xsl:if>
			<xsl:call-template name="ajaxTarget"/>
			<li>
				<xsl:call-template name="toggleElement">
					<xsl:with-param name="mode" select="$mode"/>
					<xsl:with-param name="id" select="concat($id,'${wc.common.toggles.id.expand}')"/>
					<xsl:with-param name="for" select="$targetList"/>
					<xsl:with-param name="name" select="$id"/>
					<xsl:with-param name="value" select="'expand'"/>
					<xsl:with-param name="text" select="$$${wc.common.i18n.expandAll}"/>
					<xsl:with-param name="class" select="$toggleClass"/>
					<xsl:with-param name="tabIndex" select="'0'"/>
				</xsl:call-template>
			</li>
			<li>
				<xsl:call-template name="toggleElement">
					<xsl:with-param name="mode">
						<!-- collapse all is never ajaxy or lame -->
						<xsl:text>client</xsl:text>
					</xsl:with-param>
					<xsl:with-param name="id" select="concat($id,'${wc.common.toggles.id.collapse}')"/>
					<xsl:with-param name="for" select="$targetList"/>
					<xsl:with-param name="name" select="$id"/>
					<xsl:with-param name="value" select="'collapse'"/>
					<xsl:with-param name="text" select="$$${wc.common.toggles.i18n.collapseAll}"/>
					<xsl:with-param name="class" select="$toggleClass"/>
					<xsl:with-param name="tabIndex" select="'-1'"/>
				</xsl:call-template>
			</li>
		</ul>
	</xsl:template>
</xsl:stylesheet>
