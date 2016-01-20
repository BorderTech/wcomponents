<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.title.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.ui.tab.n.hideTab.xsl"/>
	<xsl:import href="wc.ui.tab.n.tabClass.xsl"/>
	<xsl:import href="wc.ui.tab.n.tabElement.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Tranform for WTab. Outputs the tab opener (the tab bit of the tab). If the
		type is accordion also outputs the content.
	-->
	<xsl:template match="ui:tab">
		<xsl:param name="tabset" select="ancestor::ui:tabset[1]"/>
		<xsl:param name="firstOpenTab"/>

		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="type" select="$tabset/@type"/>
		<xsl:variable name="isDisabled">
			<xsl:if test="@disabled or $tabset/@disabled">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="isOpen">
			<!--
				It is problematic to rely on @open since this is not limited to 1 open tab per tabset
				(except accordion which can have 0...n open tabs).

				NOTE:
				If the type is not accordion then WComponents will always set an open tab. If not tabs
				are explicitly open then the first tab is marked open.
			-->
			<xsl:if test="$firstOpenTab=. or ($type='accordion' and @open)">
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
		<xsl:variable name="tabElement">
			<xsl:call-template name="tabElement"/>
		</xsl:variable>
		<xsl:element name="{$tabElement}">
			<xsl:attribute name="id">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:attribute name="role">
				<xsl:text>tab</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:call-template name="commonClassHelper"/>
				<xsl:text> wc_btn_nada</xsl:text>
			</xsl:attribute>
			<xsl:call-template name="title"/>
			<xsl:attribute name="{$expandSelectAttrib}">
				<xsl:choose>
					<xsl:when test="$isOpen=1">
						<xsl:copy-of select="$t"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="aria-controls">
				<xsl:value-of select="ui:tabContent/@id"/>
			</xsl:attribute>
			<xsl:if test="$tabElement='button'">
				<xsl:attribute name="type">
					<xsl:text>button</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$isDisabled=1">
				<!-- this is cheaper than calling template disabledElement for the
						tab, the tabGroup and the tabset in turn -->
				<xsl:choose>
					<xsl:when test="$tabElement='button'">
						<xsl:attribute name="disabled">
							<xsl:text>disabled</xsl:text>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="aria-disabled">
							<xsl:value-of select="$t"/>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:variable name="tabIndex">
				<xsl:choose>
					<xsl:when test="$isDisabled=1">
						<xsl:text>-1</xsl:text>
					</xsl:when>
					<xsl:when test="$firstOpenTab=. or ($isOpen=1 and not($tabset))">
						<xsl:text>0</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>-1</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:if test="$tabIndex!=''">
				<xsl:attribute name="tabindex">
					<xsl:value-of select="$tabIndex"/>
				</xsl:attribute>
			</xsl:if>
			<!-- do not allow the open tab to be hidden (remember firstOpenTab could be empty so use not()) -->
			<xsl:if test="not($firstOpenTab=.)">
				<xsl:call-template name="hideTab"/>
			</xsl:if>
			<xsl:call-template name="accessKey"/>
			<xsl:variable name="labelElement">
				<xsl:choose>
					<xsl:when test="$tabElement='div'">
						<xsl:text>div</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>span</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:apply-templates select="ui:decoratedLabel">
				<xsl:with-param name="output" select="$labelElement"/>
			</xsl:apply-templates>
		</xsl:element>
		<xsl:if test="$type='accordion'">
			<xsl:apply-templates select="ui:tabContent">
				<xsl:with-param name="open" select="$isOpen"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
