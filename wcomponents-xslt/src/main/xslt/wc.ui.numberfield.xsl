
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<xsl:template match="ui:numberfield[@readOnly]">
		<span id="{@id}" class="{normalize-space(concat('wc-numberfield wc-ro-input ', @class))}" data-wc-component="numberfield" data-wc-value="{text()}">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</span>
	</xsl:template>

	<xsl:template match="ui:numberfield">
		<span id="{@id}" class="{normalize-space(concat('wc-numberfield wc-input-wrapper ', @class))}">
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
					<xsl:text>number</xsl:text>
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
				<xsl:if test="@submitOnChange">
					<xsl:attribute name="class">
						<xsl:text>wc_soc</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@toolTip">
					<xsl:attribute name="title">
						<xsl:value-of select="@toolTip"/>
					</xsl:attribute>
				</xsl:if>
				<!--
					Turning off autocomplete is CRITICAL in Internet Explorer (8, others untested, but those
					with a native HTML5 number field are probably going to be OK). It tooks me days to find this
					after tearing apart the entire framework. Here's the issue:
						In Internet Explorer the autocomplete feature on an input field causes the keydown event
						to be cancelled once there is something in the autocomplete list, i.e. once you have
						entered something into that field. So your event listeners are called with a cancelled
						event but you can find no code that cancels the event - very tricky to track down.

					TODO: check this in IE 11 and possibly implement autocomplete or move this attribute fix to JavaScript.
				-->
				<!--<xsl:attribute name="autocomplete">
					<xsl:text>off</xsl:text>
				</xsl:attribute>-->

				<xsl:if test="@autocomplete">
					<xsl:attribute name="autocomplete">
						<xsl:value-of select="@autocomplete"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@min">
					<xsl:attribute name="min">
						<xsl:value-of select="@min"/>
						<!-- NOTE: step may only be a non-integer if min is a non integer -->
						<xsl:if test="contains(@step, '.') and not(contains(@min, '.'))">
							<xsl:text>.0</xsl:text>
						</xsl:if>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@max">
					<xsl:attribute name="max">
						<xsl:value-of select="@max"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@step">
					<!-- NOTE: if min is not defined step must be an integer and step may not be 0-->
					<xsl:variable name="step">
						<xsl:choose>
							<xsl:when test="not(@min) and contains(@step, '.')">
								<xsl:number value="round(number(@step))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="number(@step)"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:if test="number($step) ne 0">
						<xsl:attribute name="step">
							<xsl:value-of select="$step"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
				<xsl:if test="@placeholder">
					<xsl:attribute name="placeholder">
						<xsl:value-of select="@placeholder"/>
					</xsl:attribute>
				</xsl:if>
			</xsl:element>
			<xsl:apply-templates select="ui:fieldindicator"/>
		</span>
	</xsl:template>

</xsl:stylesheet>
