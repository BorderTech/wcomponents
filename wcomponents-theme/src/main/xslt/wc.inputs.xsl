
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<xsl:template match="ui:textfield[@readOnly] | ui:phonenumberfield[@readOnly] | ui:emailfield[@readOnly] | ui:passwordfield[@readOnly]">
		<span id="{@id}" class="{normalize-space(concat('wc-', local-name(), ' wc-ro-input ', @class))}" data-wc-component="{local-name()}">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</span>
	</xsl:template>

	<xsl:template match="ui:phonenumberfield[@readOnly and ./text()] | ui:emailfield[@readOnly and ./text()]">
		<xsl:variable name="href">
			<xsl:choose>
				<xsl:when test="self::ui:emailfield">
					<xsl:text>mailto:</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>tel:</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="text()"/>
		</xsl:variable>
		<a href="{$href}" id="{@id}"  class="{normalize-space(concat('wc-', local-name(), ' wc-ro-input ', @class))}" data-wc-component="{local-name()}">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="text()"/>
		</a>
	</xsl:template>

	<!-- Single line input controls which may be associated with a datalist. -->
	<xsl:template match="ui:textfield | ui:phonenumberfield | ui:emailfield | ui:passwordfield">
		<span id="{@id}">
			<xsl:variable name="additional">
				<xsl:value-of select="@class"/>
				<xsl:if test="@list">
					<xsl:text> wc-combo</xsl:text>
				</xsl:if>
			</xsl:variable>
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space(concat('wc-', local-name(),' wc-input-wrapper ', $additional))"/>
			</xsl:attribute>
			<xsl:if test="@disabled">
				<xsl:attribute name="aria-disabled">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@list">
				<xsl:attribute name="role">
					<xsl:text>combobox</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="aria-expanded">
					<xsl:text>false</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="data-wc-suggest">
					<xsl:value-of select="@list"/>
				</xsl:attribute>
				<xsl:attribute name="aria-autocomplete">
					<xsl:text>list</xsl:text>
				</xsl:attribute>
				<xsl:if test="@toolTip">
					<xsl:attribute name="title">
						<xsl:value-of select="@toolTip"/>
					</xsl:attribute>
				</xsl:if>
			</xsl:if>
			<xsl:element name="input">
				<xsl:attribute name="id">
					<xsl:value-of select="concat(@id, '_input')"/>
				</xsl:attribute>
				<xsl:attribute name="type">
					<xsl:choose>
						<xsl:when test="self::ui:textfield">
							<xsl:text>text</xsl:text>
						</xsl:when>
						<xsl:when test="self::ui:emailfield">
							<xsl:text>email</xsl:text>
						</xsl:when>
						<xsl:when test="self::ui:passwordfield">
							<xsl:text>password</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>tel</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
				<xsl:attribute name="value">
					<xsl:value-of select="text()"/>
				</xsl:attribute>
				<xsl:if test="@required">
					<xsl:attribute name="required">
						<xsl:text>required</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@accessibleText">
					<xsl:attribute name="aria-label">
						<xsl:value-of select="@accessibleText"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@buttonId">
					<xsl:attribute name="data-wc-submit">
						<xsl:value-of select="@buttonId"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="ui:fieldindicator">
					<xsl:if test="ui:fieldindicator[@id]">
						<xsl:attribute name="aria-describedby">
							<xsl:value-of select="ui:fieldindicator/@id" />
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="ui:fieldindicator[@type='error']">
						<xsl:attribute name="aria-invalid">
							<xsl:text>true</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
				<xsl:if test="(@submitOnChange and not(@list))">
					<xsl:attribute name="class">
						<xsl:text>wc_soc</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@toolTip and not(@list)">
					<xsl:attribute name="title">
						<xsl:value-of select="@toolTip"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="@list">
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
					<xsl:when test="self::ui:emailfield">
						<xsl:attribute name="autocomplete">
							<xsl:text>email</xsl:text>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="self::ui:phonenumberfield">
						<xsl:attribute name="autocomplete">
							<xsl:text>tel</xsl:text>
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
				<xsl:if test="@placeholder">
					<xsl:attribute name="placeholder">
						<xsl:value-of select="@placeholder"/>
					</xsl:attribute>
				</xsl:if>
			</xsl:element>
			<xsl:if test="@list">
				<button aria-hidden="true" class="wc_suggest wc-invite" id="{concat(@id, '_list')}" tabindex="-1" type="button"
					value="{concat(@id,'_input')}">
					<xsl:if test="@disabled">
						<xsl:attribute name="disabled">
							<xsl:text>disabled</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<i aria-hidden="true" class="fa fa-caret-down"/>
				</button>
			</xsl:if>
			<xsl:apply-templates select="ui:fieldindicator"/>
		</span>
	</xsl:template>

	<xsl:template match="ui:checkbox | ui:radiobutton">
		<span id="{@id}" class="{normalize-space(concat('wc-', local-name(), ' wc-input-wrapper ', @class))}">
			<xsl:if test="@disabled">
				<xsl:attribute name="aria-disabled">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:element name="input">
				<xsl:attribute name="id">
					<xsl:value-of select="concat(@id, '_input')"/>
				</xsl:attribute>
				<xsl:attribute name="type">
					<xsl:choose>
						<xsl:when test="self::ui:checkbox">
							<xsl:text>checkbox</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>radio</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:choose>
						<xsl:when test="self::ui:checkbox">
							<xsl:value-of select="@id"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@groupName"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:if test="@toolTip">
					<xsl:attribute name="title">
						<xsl:value-of select="@toolTip"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@required">
					<xsl:attribute name="required">
						<xsl:text>required</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@accessibleText">
					<xsl:attribute name="aria-label">
						<xsl:value-of select="@accessibleText"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@buttonId">
					<xsl:attribute name="data-wc-submit">
						<xsl:value-of select="@buttonId"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="ui:fieldindicator">
					<xsl:if test="ui:fieldindicator[@id]">
						<xsl:attribute name="aria-describedby">
							<xsl:value-of select="ui:fieldindicator/@id" />
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="ui:fieldindicator[@type='error']">
						<xsl:attribute name="aria-invalid">
							<xsl:text>true</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
				<xsl:if test="@submitOnChange">
					<xsl:attribute name="class">
						<xsl:text>wc_soc</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@selected">
					<xsl:attribute name="checked">
						<xsl:text>checked</xsl:text>
					</xsl:attribute>
				</xsl:if>

				<xsl:choose>
					<xsl:when test="self::ui:checkbox">
						<xsl:attribute name="value">
							<xsl:text>true</xsl:text>
						</xsl:attribute>
						<xsl:if test="@groupName">
							<xsl:attribute name="data-wc-group">
								<xsl:value-of select="@groupName"/>
							</xsl:attribute>
						</xsl:if>
					</xsl:when>
					<xsl:when test="@value">
						<xsl:attribute name="value">
							<xsl:value-of select="@value"/>
						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
			</xsl:element>

			<xsl:choose>
				<xsl:when test="self::ui:checkbox">
					<xsl:apply-templates select="ui:fieldindicator"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="input">
						<xsl:attribute name="type">
							<xsl:text>hidden</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="name">
							<xsl:value-of select="concat(@groupName, '-h')"/>
						</xsl:attribute>
						<xsl:attribute name="value">
							<xsl:text>x</xsl:text>
						</xsl:attribute>
						<xsl:if test="@disabled">
							<xsl:attribute name="disabled">
								<xsl:text>disabled</xsl:text>
							</xsl:attribute>
						</xsl:if>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
		</span>
	</xsl:template>

	<xsl:template match="ui:checkbox[@readOnly] | ui:radiobutton[@readOnly]">
		<span id="{@id}" data-wc-component="{local-name()}">
			<xsl:variable name="additional">
				<xsl:value-of select="@class"/>
				<xsl:if test="@selected">
					<xsl:text> wc_ro_sel</xsl:text>
				</xsl:if>
			</xsl:variable>
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space(concat('wc-', local-name(),' wc-ro-input ', $additional))"/>
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:choose>
					<xsl:when test="@selected">
						<xsl:text>{{#i18n}}input_selected{{/i18n}}</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>{{#i18n}}input_unselected{{/i18n}}</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="data-wc-value">
				<xsl:choose>
					<xsl:when test="@selected">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:variable name="iconclass">
				<xsl:choose>
					<xsl:when test="self::ui:checkbox and @selected">fa-check-square-o</xsl:when>
					<xsl:when test="self::ui:checkbox">fa-square-o</xsl:when>
					<xsl:when test="@selected">fa-dot-circle-o</xsl:when>
					<xsl:otherwise>fa-circle-o</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<i aria-hidden="true" class="fa {$iconclass}"/>
		</span>
	</xsl:template>

	<!-- This is a transform for the output of WDataListServlet. -->
	<xsl:template match="ui:datalist">
		<select>
			<xsl:apply-templates mode="selectableList" select="ui:option"/>
		</select>
	</xsl:template>


</xsl:stylesheet>
