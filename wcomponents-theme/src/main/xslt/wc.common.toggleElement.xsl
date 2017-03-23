<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.icon.xsl"/>
	
	<!-- Output expand all and collapse all buttons. -->
	<xsl:template name="collapsibleToggle">
		<xsl:param name="id" select="@id"/>
		<xsl:param name="for" select="@groupName"/>
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
		<xsl:variable name="toggleClass">
			<xsl:value-of select="concat('wc_', local-name(.))"/>
		</xsl:variable>
		<ul id="{$id}" role="radiogroup">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:text>wc_coltog</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:attribute name="data-wc-group">
				<xsl:value-of select="$for"/>
			</xsl:attribute>
			<li>
				<xsl:call-template name="toggleElement">
					<xsl:with-param name="name" select="$id"/>
					<xsl:with-param name="value" select="'expand'"/>
					<xsl:with-param name="text"><xsl:text>{{t 'expandall'}}</xsl:text></xsl:with-param>
					<xsl:with-param name="class" select="$toggleClass"/>
				</xsl:call-template>
			</li>
			<li>
				<xsl:call-template name="toggleElement">
					<xsl:with-param name="name" select="$id"/>
					<xsl:with-param name="value" select="'collapse'"/>
					<xsl:with-param name="text"><xsl:text>{{t 'collapseall'}}</xsl:text></xsl:with-param>
					<xsl:with-param name="class" select="$toggleClass"/>
				</xsl:call-template>
			</li>
		</ul>
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
		<xsl:choose>
			<xsl:when test="$type eq 'text'">
				<span id="{$toggleId}" role="radiogroup" data-wc-target="{$for}">
					<xsl:call-template name="makeCommonClass">
						<xsl:with-param name="additional">
							<xsl:text>wc_seltog</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:if test="self::ui:selecttoggle">
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="isControl" select="0"/>
						</xsl:call-template>
					</xsl:if>
					<xsl:variable name="subClass">
						<xsl:value-of select="concat('wc_', local-name(.), ' wc_seltog')"/>
					</xsl:variable>
					<xsl:call-template name="toggleElement">
						<xsl:with-param name="name" select="$name"/>
						<xsl:with-param name="value" select="'all'"/>
						<xsl:with-param name="class" select="$subClass"/>
						<xsl:with-param name="text"><xsl:text>{{t 'toggle_all'}}</xsl:text></xsl:with-param>
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
						<xsl:with-param name="name" select="$name"/>
						<xsl:with-param name="value" select="'none'"/>
						<xsl:with-param name="class" select="$subClass"/>
						<xsl:with-param name="text"><xsl:text>{{t 'toggle_none'}}</xsl:text></xsl:with-param>
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
				<button id="{$toggleId}" role="checkbox" type="button" data-wc-target="{$for}">
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
					<xsl:call-template name="makeCommonClass">
						<xsl:with-param name="additional">
							<xsl:text>wc_seltog wc-nobutton</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:if test="self::ui:selecttoggle">
						<xsl:call-template name="disabledElement"/>
					</xsl:if>
					<xsl:call-template name="icon">
						<xsl:with-param name="class">
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
						</xsl:with-param>
					</xsl:call-template>
				</button>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Helper templates and keys for common state toggling elements. -->
	<xsl:template name="toggleElement">
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
		<button role="radio" class="{$localClass}" data-wc-value="{$value}" type="button">
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
					<xsl:call-template name="disabledElement"/>
				</xsl:when>
				<xsl:when test="not(self::ui:rowselection)">
					<xsl:call-template name="icon">
						<xsl:with-param name="class">
							<xsl:choose>
								<xsl:when test="$value eq 'expand'">
									<xsl:text>fa-plus-square-o</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>fa-minus-square-o</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
			<xsl:value-of select="$text"/>
		</button>
	</xsl:template>
</xsl:stylesheet>
