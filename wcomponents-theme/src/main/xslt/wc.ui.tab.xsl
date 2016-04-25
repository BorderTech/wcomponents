<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.title.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Tranform for WTab. Outputs the tab opener (the tab bit of the tab). If the type is accordion also outputs the 
		content.
		
		NOTE: OPEN TAB(S)
		Tabsets other than accordion only output one open tab and will always output one open tab even if no tabs are
		explicitly open (this is not in the schema but is enforced in the Java API). This open tab may be disabled.
		
		NOTE: ANCESTOR TABSET
		WTab does not implement AjaxTarget therefore the ui:tab can never be an ajax target. Therefore we can
		always rely on finding a tabset ancestor.
		
		
	-->
	<xsl:template match="ui:tab">
		<xsl:param name="tabset"/>
		<xsl:param name="numAvailTabs"/>

		<xsl:variable name="type" select="$tabset/@type"/>

		<xsl:variable name="isDisabled">
			<xsl:if test="@disabled or $tabset/@disabled">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="expandSelectAttrib">
			<xsl:choose>
				<xsl:when test="$type='accordion'">
					<xsl:text>aria-expanded</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>aria-selected</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<button id="{@id}" role="tab" aria-controls="{ui:tabcontent/@id}" type="button">
			<xsl:attribute name="{$expandSelectAttrib}">
				<xsl:choose>
					<xsl:when test="@open">
						<xsl:copy-of select="$t"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			
			<!--
				We set tabindex -1 on closed tabs only if there is at least one tab open and not disabled.
			-->
			<xsl:if test="$numAvailTabs &gt; 0 and not(@open)">
				<xsl:attribute name="tabindex">
					<xsl:text>-1</xsl:text>
				</xsl:attribute>
			</xsl:if>

			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:text>wc_btn_nada wc_invite</xsl:text>
				</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="title"/>
			
			<!--
				This is cheaper than calling template disabledElement for the tab, the tabGroup and the tabset in turn
			-->
			<xsl:if test="$isDisabled=1">
				<xsl:attribute name="disabled">
					<xsl:text>disabled</xsl:text>
				</xsl:attribute>
			</xsl:if>

			<!-- do not allow open tabs to be hidden -->
			<xsl:if test="not(@open)">
				<xsl:call-template name="hideElementIfHiddenSet"/>
			</xsl:if>

			<xsl:call-template name="accessKey"/>
			<xsl:apply-templates select="ui:decoratedlabel"/>
		</button>
		<xsl:if test="$type='accordion'">
			<xsl:apply-templates select="ui:tabcontent">
				<xsl:with-param name="tabset" select="$tabset"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
