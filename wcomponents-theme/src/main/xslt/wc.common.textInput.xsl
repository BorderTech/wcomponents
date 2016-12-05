<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.required.xsl"/>
	<!--
		Single line input controls which may be associated with a datalist.
   -->
	<xsl:template match="ui:textfield|ui:phonenumberfield|ui:emailfield">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="myLabel" select="key('labelKey',$id)[1]"/>
		<xsl:choose>
			
			<xsl:when test="@readOnly">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="label" select="$myLabel"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="isError" select="key('errorKey',$id)"/>
				<xsl:if test="not($myLabel)">
					<xsl:call-template name="checkLabel">
						<xsl:with-param name="force" select="1"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:variable name="list" select="@list"/>
				<xsl:variable name="inputId">
					<xsl:value-of select="concat($id,'_input')"/>
				</xsl:variable>
				<span>
					<xsl:call-template name="commonAttributes">
						<xsl:with-param name="live" select="'off'"/>
						<xsl:with-param name="class">
							<xsl:text>wc_input_wrapper</xsl:text>
							<xsl:if test="$list">
								<xsl:text> wc-combo</xsl:text>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:if test="$list">
						<xsl:attribute name="role">
							<xsl:text>combobox</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="aria-expanded">
							<xsl:text>false</xsl:text>
						</xsl:attribute>
						<xsl:variable name="suggestionList" select="//ui:suggestions[@id eq $list]"/>
						<xsl:if test="$suggestionList and $suggestionList/@autocomplete eq 'list'">
							<xsl:attribute name="data-wc-listcomplete">
								<xsl:value-of select="$t"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:attribute name="aria-autocomplete">
							<xsl:text>list</xsl:text>
						</xsl:attribute>
						<xsl:call-template name="title"/>
					</xsl:if>
					<xsl:call-template name="requiredElement">
						<xsl:with-param name="useNative" select="0"/>
					</xsl:call-template>
					<xsl:element name="input">
						<xsl:attribute name="id">
							<xsl:value-of select="$inputId"/>
						</xsl:attribute>
						<xsl:attribute name="name">
							<xsl:value-of select="$id"/>
						</xsl:attribute>
						<xsl:attribute name="value">
							<xsl:value-of select="text()"/>
						</xsl:attribute>
						<xsl:call-template name="requiredElement">
							<xsl:with-param name="useNative" select="1"/>
						</xsl:call-template>
						<xsl:attribute name="type">
							<xsl:choose>
								<xsl:when test="self::ui:textfield">
									<xsl:text>text</xsl:text>
								</xsl:when>
								<xsl:when test="self::ui:emailfield">
									<xsl:text>email</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>tel</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:if test="@placeholder or @required">
							<xsl:attribute name="placeholder">
								<xsl:choose>
									<xsl:when test="@placeholder">
										<xsl:value-of select="@placeholder"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>{{t 'requiredPlaceholder'}}</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="not($list)">
							<xsl:call-template name="title"/>
						</xsl:if>
						<xsl:call-template name="ajaxController"/>
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="isControl" select="1"/>
						</xsl:call-template>
						<xsl:choose>
							<xsl:when test="$list">
								<xsl:attribute name="role">
									<xsl:text>textbox</xsl:text>
								</xsl:attribute>
								<!-- every input that implements combo should have autocomplete turned off -->
								<xsl:attribute name="autocomplete">
									<xsl:text>off</xsl:text>
								</xsl:attribute>
							</xsl:when>
							<xsl:when test="@autocomplete">
								<xsl:attribute name="autocomplete">
									<xsl:value-of select="@autocomplete"/>
								</xsl:attribute>
							</xsl:when>
						</xsl:choose>
						<xsl:if test="@size">
							<xsl:attribute name="size">
								<xsl:value-of select="@size"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="@maxLength">
							<xsl:attribute name="maxlength">
								<xsl:value-of select="@maxLength"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="@minLength">
							<xsl:attribute name="minlength">
								<xsl:value-of select="@minLength"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="@pattern">
							<xsl:attribute name="pattern">
								<xsl:value-of select="@pattern"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="$isError">
							<xsl:call-template name="invalid"/>
						</xsl:if>
						<xsl:if test="not($myLabel)">
							<xsl:call-template name="ariaLabel"/>
						</xsl:if>
					</xsl:element>
					<xsl:if test="$list">
						<button value="{$inputId}" tabindex="-1" id="{concat($id, '_list')}" type="button" aria-hidden="true" class="wc_suggest wc_btn_icon wc-invite">
							<xsl:call-template name="disabledElement">
								<xsl:with-param name="isControl" select="1"/>
							</xsl:call-template>
						</button>
						<xsl:variable name="suggestions" select="//ui:suggestions[@id eq $list]"/>
						<xsl:choose>
							<xsl:when test="$suggestions">
								<xsl:apply-templates select="$suggestions" mode="inline"/>
							</xsl:when>
							<xsl:otherwise>
								<span role="listbox" aria-busy="true" id="{$list}"></span>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
				</span>
				<xsl:call-template name="inlineError">
					<xsl:with-param name="errors" select="$isError"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
