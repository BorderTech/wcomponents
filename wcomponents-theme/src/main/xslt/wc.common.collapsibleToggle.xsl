<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.toggleElement.xsl"/>
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!-- Key used by collapsibleToggle to get the list of controlled collapsibles -->
	<xsl:key name="collapsibleGroupKey" match="//ui:collapsible[@groupName]" use="@groupName"/>

	<!--
		Output expand all and collapse all buttons. 
		
		Called from ui:rowexpansion and ui:collapsibletoggle.
		
		param id: the id of the collapsing control's grouping component. Default @id.
		
		param for: The identifier of the component/group which is controlled by the expand/collapse all buttons. This 
		could be an id (for a single controlled entity), a space delimited id list, or a groupName attribute. It could 
		alse be empty for a WCollapsibleToggle which controls all WCollapsibles in a view. Default @groupName.
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
			WCollapsibleToggle has a mandatory groupName attribute. 
			
			This may not point to a CollapsibleGroup (which is odd and should change) and in this case the 
			WCollapsibleToggle should toggle every WCollapsible on the page.
		-->
		<xsl:variable name="group" select="key('collapsibleGroupKey', $for)"/>
		<xsl:variable name="targetList">
			<xsl:choose>
				<xsl:when test="$mode='server'"><!--server mode does not need a target list, the expansion is done on the server -->
					<xsl:value-of select="''"/>
				</xsl:when>
				<xsl:when test="self::ui:rowexpansion"><!-- a table's rowExpansion always targets the table -->
					<xsl:value-of select="$for"/>
				</xsl:when>
				<xsl:when test="$group">
					<xsl:apply-templates select="$group" mode="getIdList"/>
				</xsl:when>
				<!-- Do not need otherwise as implicit in this is when self::ui:collapsibleToggle and not($group) the value is '' -->
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="toggleClass">
			<xsl:value-of select="local-name(.)"/>
		</xsl:variable>
		<ul id="{$id}" role="radiogroup">
			<xsl:call-template name="makeCommonClass"/>
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
				</xsl:call-template>
			</li>
			<li>
				<xsl:call-template name="toggleElement">
					<xsl:with-param name="mode" select="'client'"/><!-- collapse all is never ajaxy or lame -->
					<xsl:with-param name="id" select="concat($id,'${wc.common.toggles.id.collapse}')"/>
					<xsl:with-param name="for" select="$targetList"/>
					<xsl:with-param name="name" select="$id"/>
					<xsl:with-param name="value" select="'collapse'"/>
					<xsl:with-param name="text" select="$$${wc.common.toggles.i18n.collapseAll}"/>
					<xsl:with-param name="class" select="$toggleClass"/>
				</xsl:call-template>
			</li>
		</ul>
	</xsl:template>
</xsl:stylesheet>
