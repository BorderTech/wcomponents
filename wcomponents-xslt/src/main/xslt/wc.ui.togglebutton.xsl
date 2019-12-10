
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<xsl:template match="ui:togglebutton[@readOnly]">
		<span id="{@id}" data-wc-component="togglebutton">
			<xsl:variable name="additional">
				<xsl:value-of select="@class"/>
				<xsl:if test="@selected">
					<xsl:text> wc_ro_sel</xsl:text>
				</xsl:if>
			</xsl:variable>
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space(concat('wc-togglebutton ', $additional))"/>
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
			<xsl:if test="normalize-space(text()) ne ''">
				<span class="wc-togglebutton-text wc-off" id="{@id}-lbl">
					<xsl:value-of select="text()"/>
				</span>
			</xsl:if>
		</span>
	</xsl:template>

	<xsl:template match="ui:togglebutton">
		<span id="{@id}" class="{normalize-space(concat('wc-togglebutton wc-input-wrapper ', @class))}">
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
			<button id="{concat(@id, '_input')}" name="{@id}" class="wc-nobutton wc-invite" role="checkbox" type="button" value="true">
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
				<xsl:attribute name="aria-checked">
					<xsl:choose>
						<xsl:when test="@selected">true</xsl:when>
						<xsl:otherwise>false</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:if test="@groupName">
					<xsl:attribute name="data-wc-group">
						<xsl:value-of select="@groupName"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="normalize-space(text()) ne ''">
					<span class="wc-togglebutton-text wc-off" id="{@id}-lbl">
						<xsl:value-of select="text()"/>
					</span>
				</xsl:if>
			</button>
			<xsl:apply-templates select="ui:fieldindicator"/>
			<xsl:element name="input">
				<xsl:attribute name="type">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:value-of select="concat(@id, '-h')"/>
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
		</span>
	</xsl:template>

</xsl:stylesheet>
