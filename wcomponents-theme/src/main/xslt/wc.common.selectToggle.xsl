<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.toggleElement.xsl"/>
	<xsl:import href="wc.common.key.label.xsl"/>

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
		<xsl:param name="label" select="''"/><!--not set for ui:selecttoggle -->
		<xsl:param name="type" select="'text'"/>
		<xsl:variable name="toggleId">
			<xsl:value-of select="$id"/>
			<xsl:if test="not(self::ui:selecttoggle)">
				<xsl:text>_st</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="myLabel" select="key('labelKey',$id)[1]"/>
		<xsl:choose>
			<xsl:when test="$type eq 'text'">
				<span id="{$toggleId}" role="radiogroup" data-wc-target="{$for}">
					<xsl:call-template name="makeCommonClass">
						<xsl:with-param name="additional">
							<xsl:text>wc_seltog</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:choose>
						<xsl:when test="self::ui:rowselection">
							<xsl:call-template name="disabledElement"><!-- WDataTable compatibility only. -->
								<xsl:with-param name="field" select=".."/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="disabledElement"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:variable name="defaultLabelId">
						<xsl:value-of select="concat($toggleId, '_l')"/>
					</xsl:variable>
					<xsl:variable name="labelContent">
						<xsl:choose>
							<xsl:when test="$label ne ''">
								<xsl:value-of select="$label"/>
							</xsl:when>
							<xsl:when test="self::ui:rowselection">
								<xsl:text>{{t 'toggle_label'}}</xsl:text>
							</xsl:when>
							<xsl:when test="not($myLabel)">
								<xsl:text>{{t 'toggle_label'}}</xsl:text>
							</xsl:when>
						</xsl:choose>
					</xsl:variable>
					<xsl:if test="$labelContent ne ''">
						<span id="{$defaultLabelId}">
							<xsl:value-of select="$labelContent"/>
						</span>
					</xsl:if>
					<xsl:variable name="realLabelId">
						<xsl:choose>
							<xsl:when test="self::ui:rowselection">
								<xsl:value-of select="$defaultLabelId"/>
							</xsl:when>
							<xsl:when test="$myLabel">
								<xsl:value-of select="$myLabel/@id"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$defaultLabelId"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="subClass">
						<xsl:value-of select="concat('wc_', local-name(.), ' wc_seltog')"/>
					</xsl:variable>
					<xsl:call-template name="toggleElement">
						<xsl:with-param name="mode" select="'client'"/>
						<xsl:with-param name="id" select="concat($id,'_all')"/>
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
						<xsl:with-param name="labelId" select="$realLabelId"/>
					</xsl:call-template>
					<xsl:call-template name="toggleElement">
						<xsl:with-param name="mode" select="'client'"/>
						<xsl:with-param name="id" select="concat($id,'_none')"/>
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
						<xsl:with-param name="labelId" select="$realLabelId"/>
					</xsl:call-template>
				</span>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="textEquivalent">
					<xsl:choose>
						<xsl:when test="$label ne ''">
							<xsl:value-of select="$label"/>
						</xsl:when>
						<xsl:when test="self::ui:rowselection">
							<xsl:text>{{t 'toggle_all_label'}}</xsl:text>
						</xsl:when>
						<xsl:when test="$myLabel">
							<xsl:apply-templates select="$myLabel" mode="selectToggle"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>{{t 'toggle_all_label'}}</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
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
							<xsl:text>wc_seltog wc-nobutton wc-icon</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:if test="self::ui:selecttoggle">
						<xsl:attribute name="title">
							<xsl:value-of select="$textEquivalent"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="self::ui:selecttoggle">
							<xsl:call-template name="disabledElement">
								<xsl:with-param name="isControl" select="1"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise><!-- TODO: this applies only to WDataTable and is to be removed -->
							<xsl:call-template name="disabledElement">
								<xsl:with-param name="isControl" select="1"/>
								<xsl:with-param name="field" select="parent::ui:table"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
					<!-- ADDING TEXT CONTENT - NO MORE ATTRIBUTES AFTER THIS COMMENT -->
					<xsl:if test="self::ui:rowselection">
						<span>
							<xsl:value-of select="$textEquivalent"/>
						</span>
					</xsl:if>
				</button>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
