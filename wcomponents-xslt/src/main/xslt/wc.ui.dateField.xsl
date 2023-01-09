
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<xsl:template match="ui:datefield[@readOnly]">
		<span id="{@id}" class="{normalize-space(concat('wc-datefield ', @class))}" data-wc-component="datefield">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="@date">
					<xsl:attribute name="data-wc-value">
						<xsl:value-of select="@date"/>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="text()"/>
				</xsl:otherwise>
			</xsl:choose>
		</span>
	</xsl:template>


	<!--
		DateField has been rethunk - by popular demand, and following the practice of every major booking site in the universe,
		we are favoring our own superior date field over the native HTML one.
		A future enhancement might be to build this into a Web Component, shadow DOM etc.
	-->
	<xsl:template match="ui:datefield">
		<xsl:variable name="partialClass">
			<xsl:if test="@allowPartial">
				<xsl:value-of select="'wc_datefield_partial '"/>
			</xsl:if>
		</xsl:variable>
		<div id="{@id}" class="{normalize-space(concat('wc-datefield wc-input-wrapper ', $partialClass, @class))}" role="combobox" aria-autocomplete="list" aria-expanded="false">
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
			<xsl:if test="@date">
				<xsl:attribute name="data-wc-value">
					<xsl:value-of select="@date"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:element name="input">
				<xsl:attribute name="id">
					<xsl:value-of select="concat(@id, '_input')"/>
				</xsl:attribute>
				<xsl:attribute name="type">
					<xsl:text>text</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:value-of select="@id"/>
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
				<xsl:attribute name="value">
					<xsl:choose>
						<xsl:when test="@date">
							<xsl:value-of select="@date"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="text()"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<!-- every input that implements combo should have autocomplete turned off -->
				<xsl:attribute name="autocomplete">
					<xsl:text>off</xsl:text>
				</xsl:attribute>
				<xsl:if test="@placeholder">
					<xsl:attribute name="placeholder">
						<xsl:value-of select="@placeholder"/>
					</xsl:attribute>
				</xsl:if>
			</xsl:element>
			<!-- This is the date picker launch control element. -->
			<button aria-hidden="true" class="wc_wdf_cal wc-invite" tabindex="-1" type="button" value="{concat(@id,'_input')}">
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<i aria-hidden="true" class="fa fa-calendar"/>
			</button>
			<span aria-busy="true" role="listbox"/>
			<xsl:apply-templates select="ui:fieldindicator"/>
		</div>
	</xsl:template>





</xsl:stylesheet>
