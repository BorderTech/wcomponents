
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!--
		This template builds the basic tabset. The tabset is a wrapper container. It has a list of tabs and content.
	-->
	<xsl:template match="ui:tabset">
		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:apply-templates select="ui:margin" mode="asclass"/>
			<xsl:if test="@type">
				<xsl:value-of select="concat(' wc-tabset-type-', @type)"/>
			</xsl:if>
		</xsl:variable>
		<div id="{@id}" class="{normalize-space(concat('wc-tabset ', $additional))}">
			<xsl:if test="@disabled"><xsl:attribute name="aria-disabled">true</xsl:attribute></xsl:if>
			<xsl:if test="@hidden"><xsl:attribute name="hidden"><xsl:text>hidden</xsl:text></xsl:attribute></xsl:if>
			<xsl:if test="@groupName">
				<xsl:attribute name="data-wc-group">
					<xsl:value-of select="@groupName"/>
				</xsl:attribute>
			</xsl:if>
			<div role="tablist">
				<xsl:choose>
					<xsl:when test="@type eq 'accordion'">
						<xsl:attribute name="aria-multiselectable">
							<xsl:choose>
								<xsl:when test="@single">false</xsl:when>
								<xsl:otherwise>true</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="@type eq 'left' or @type eq 'right'">
						<xsl:attribute name="aria-orientation">
							<xsl:text>vertical</xsl:text>
						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
				<xsl:apply-templates select="ui:tab">
					<xsl:with-param name="numAvailTabs" select="count(ui:tab[@open and not(@disabled)])"/>
				</xsl:apply-templates>
			</div>
			<xsl:if test="not(@type eq 'accordion')">
				<xsl:apply-templates select="ui:tab" mode="content"/>
			</xsl:if>
		</div>
	</xsl:template>
	<!--
		Tranform for WTab. Outputs the tab opener (the tab bit of the tab). If the type is accordion also outputs the
		content.

		NOTE: OPEN TAB(S)
		Tabsets other than accordion only output one open tab and will always output one open tab even if no tabs are
		explicitly open (this is not in the schema but is enforced in the Java API). This open tab may be disabled.
	-->
	<xsl:template match="ui:tab">
		<xsl:param name="numAvailTabs" select="0"/>
		<xsl:variable name="type" select="../@type"/>
		<xsl:variable name="isDisabled">
			<xsl:choose>
				<xsl:when test="@disabled or ../@disabled">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="expandSelectAttrib">
			<xsl:choose>
				<xsl:when test="$type eq 'accordion'">
					<xsl:text>aria-expanded</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>aria-selected</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<div id="{@id}" role="tab" aria-controls="{ui:tabcontent/@id}" class="{normalize-space(concat('wc-tab wc-invite ', @class))}">
			<xsl:attribute name="{$expandSelectAttrib}">
				<xsl:choose>
					<xsl:when test="@open">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<!--
				We set tabindex -1 on closed tabs only if there is at least one tab open and not disabled.
			-->
			<xsl:attribute name="tabindex">
				<xsl:choose>
					<xsl:when test="@disabled or (number($numAvailTabs) gt 0 and not(@open))">
						<xsl:text>-1</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>0</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="@toolTip"><xsl:attribute name="title"><xsl:value-of select="@toolTip"/></xsl:attribute></xsl:if>
			<!--
				This is cheaper than calling template disabledElement for the tab and the tabset in turn
			-->
			<xsl:if test="number($isDisabled) eq 1">
				<xsl:attribute name="aria-disabled">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<!-- do not allow open tabs to be hidden -->
			<xsl:if test="not(@open)">
				<xsl:if test="@hidden"><xsl:attribute name="hidden"><xsl:text>hidden</xsl:text></xsl:attribute></xsl:if>
			</xsl:if>
			<xsl:if test="@accessKey">
				<xsl:attribute name="accesskey">
					<xsl:value-of select="@accessKey"/>
				</xsl:attribute>
				<xsl:attribute name="aria-describedby">
					<xsl:value-of select="concat(@id,'_wctt')"/>
				</xsl:attribute>
				<span id="{concat(@id,'_wctt')}" role="tooltip" hidden="hidden">
					<xsl:value-of select="@accessKey"/>
				</span>
			</xsl:if>
			<xsl:apply-templates select="ui:decoratedlabel">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
		</div>
		<xsl:if test="$type eq 'accordion'">
			<xsl:apply-templates select="ui:tabcontent">
				<xsl:with-param name="tabset" select="parent::ui:tabset"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>

	<!--
		Apply the tab content.
	-->
	<xsl:template match="ui:tab" mode="content">
		<xsl:apply-templates select="ui:tabcontent">
			<xsl:with-param name="tabset" select="parent::ui:tabset"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- The content of the tab. -->
	<xsl:template match="ui:tabcontent">
		<xsl:param name="tabset"/>
		<xsl:variable name="open">
			<xsl:choose>
				<xsl:when test="../@open">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="mode" select="../@mode"/>
		<div id="{@id}" role="tabpanel" aria-live="polite">
			<xsl:attribute name="class">
				<xsl:text>wc-tabcontent</xsl:text>
				<xsl:choose>
					<xsl:when test="number($open) eq 1">
						<xsl:if test="$mode eq 'dynamic'">
							<xsl:text> wc_magic wc_dynamic</xsl:text>
						</xsl:if>
					</xsl:when>
					<xsl:when test="($mode eq 'lazy') or ($mode eq 'eager') or ($mode eq 'dynamic')">
						<xsl:text> wc_magic</xsl:text>
						<xsl:if test="$mode eq 'dynamic'">
							<xsl:text> wc_dynamic</xsl:text>
						</xsl:if>
					</xsl:when>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="number($open) ne 1">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="($mode eq 'lazy') or ($mode eq 'eager') or ($mode eq 'dynamic')">
				<xsl:attribute name="data-wc-ajaxalias">
					<xsl:value-of select="../@id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="../@disabled or $tabset/@disabled">
				<xsl:attribute name="aria-disabled">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$tabset/@contentHeight">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('height:',$tabset/@contentHeight,';overflow-y:auto;')"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates />
		</div>
	</xsl:template>
</xsl:stylesheet>
