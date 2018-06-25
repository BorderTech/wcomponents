
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<xsl:template match="ui:multitextfield[@readOnly]">
		<ul id="{@id}" class="{normalize-space(concat('wc-multitextfield wc-vgap-sm ',  @class))}" data-wc-component="multitextfield">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates mode="readOnly" select="ui:value"/>
		</ul>
	</xsl:template>

	<xsl:template match="ui:multitextfield">
		<fieldset aria-atomic="false" aria-relevant="additions removals" id="{@id}">
			<xsl:variable name="additional">
				<xsl:value-of select="@class"/>
				<xsl:if test="@required">
					<xsl:text> wc_req</xsl:text>
				</xsl:if>
			</xsl:variable>
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space(concat('wc-multitextfield wc_mfc wc_noborder ', $additional))" />
			</xsl:attribute>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@toolTip">
				<xsl:attribute name="title">
					<xsl:value-of select="@toolTip"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@accessibleText">
				<xsl:attribute name="aria-label">
					<xsl:value-of select="@accessibleText"/>
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
			<xsl:if test="@min">
				<xsl:attribute name="data-wc-min">
					<xsl:value-of select="@min"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@max">
				<xsl:attribute name="data-wc-max">
					<xsl:value-of select="@max"/>
				</xsl:attribute>
			</xsl:if>
			<ul class="wc_list_nb wc-vgap-sm">
				<xsl:choose>
					<xsl:when test="ui:value">
						<xsl:apply-templates select="*"/>
					</xsl:when>
					<xsl:otherwise>
						<li>
							<xsl:variable name="inputId" select="concat(@id, generate-id())"/>
							<label for="{$inputId}" class="wc-off">
								<xsl:value-of select="@title"/>
							</label>
							<xsl:element name="input">
								<xsl:attribute name="type">
									<xsl:text>text</xsl:text>
								</xsl:attribute>
								<xsl:attribute name="name">
									<xsl:value-of select="@id"/>
								</xsl:attribute>
								<xsl:attribute name="id">
									<xsl:value-of select="$inputId"/>
								</xsl:attribute>
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
								<xsl:if test="@pattern">
									<xsl:attribute name="pattern">
										<xsl:value-of select="@pattern"/>
									</xsl:attribute>
								</xsl:if>
								<xsl:if test="@minLength">
									<xsl:attribute name="minlength">
										<xsl:value-of select="@minLength"/>
									</xsl:attribute>
								</xsl:if>
								<xsl:if test="@placeholder">
									<xsl:attribute name="placeholder">
										<xsl:value-of select="@placeholder"/>
									</xsl:attribute>
								</xsl:if>
								<xsl:if test="@disabled">
									<xsl:attribute name="disabled">
										<xsl:text>disabled</xsl:text>
									</xsl:attribute>
								</xsl:if>
							</xsl:element>
							<button aria-controls="{@id}" class="wc_btn_icon wc-invite" type="button">
								<xsl:attribute name="title">
									<xsl:text>{{#i18n}}mfc_add{{/i18n}}</xsl:text>
								</xsl:attribute>
								<xsl:if test="@disabled">
									<xsl:attribute name="disabled">
										<xsl:text>disabled</xsl:text>
									</xsl:attribute>
								</xsl:if>
								<i aria-hidden="true" class="fa fa-plus-square"/>
							</button>
						</li>
					</xsl:otherwise>
				</xsl:choose>
			</ul>
			<xsl:apply-templates select="ui:fieldindicator"/>
		</fieldset>
	</xsl:template>

	<!-- Transforms for each value in a multiTextField. -->
	<xsl:template match="ui:value">
		<li class="wc-value">
			<xsl:variable name="fieldId">
				<xsl:value-of select="../@id"/>
			</xsl:variable>
			<xsl:variable name="inputId" select="concat($fieldId, generate-id(), '-', position())"/>
			<label for="{$inputId}" class="wc-off">
				<xsl:text>{{#i18n}}mfc_value{{/i18n}}</xsl:text>
			</label>
			<xsl:element name="input">
				<xsl:attribute name="type">
					<xsl:text>text</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:value-of select="$fieldId"/>
				</xsl:attribute>
				<xsl:attribute name="id">
					<xsl:value-of select="$inputId"/>
				</xsl:attribute>
				<xsl:if test="../@size">
					<xsl:attribute name="size">
						<xsl:value-of select="../@size"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="../@maxLength">
					<xsl:attribute name="maxlength">
						<xsl:value-of select="../@maxLength"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="../@pattern">
					<xsl:attribute name="pattern">
						<xsl:value-of select="../@pattern"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="../@minLength">
					<xsl:attribute name="minlength">
						<xsl:value-of select="../@minLength"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="../@placeholder">
					<xsl:attribute name="placeholder">
						<xsl:value-of select="../@placeholder"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="../@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:attribute name="value">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:element>
			<xsl:variable name="toolTip">
				<xsl:choose>
					<xsl:when test="position() eq 1">
						<xsl:text>{{#i18n}}mfc_add{{/i18n}}</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>{{#i18n}}mfc_remove{{/i18n}}</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<button class="wc_btn_icon wc-invite" title="{$toolTip}" type="button">
				<xsl:attribute name="aria-controls">
					<xsl:choose>
						<xsl:when test="position() eq 1">
							<xsl:value-of select="$fieldId"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$inputId"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:if test="../@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:variable name="iconclass">
					<xsl:text>fa-</xsl:text>
					<xsl:choose>
						<xsl:when test="position() = 1">plus</xsl:when>
						<xsl:otherwise>minus</xsl:otherwise>
					</xsl:choose>
					<xsl:text>-square</xsl:text>
				</xsl:variable>
				<i aria-hidden="true" class="fa {$iconclass}"/>
			</button>
		</li>
	</xsl:template>

	<xsl:template match="ui:value" mode="readOnly">
		<li>
			<xsl:value-of select="."/>
		</li>
	</xsl:template>
</xsl:stylesheet>
