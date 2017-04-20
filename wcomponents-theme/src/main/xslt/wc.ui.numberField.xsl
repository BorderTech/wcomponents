<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>

	<xsl:template match="ui:numberfield">
		<xsl:variable name="myLabel" select="key('labelKey',@id)[1]"/>
		<xsl:choose>
			<xsl:when test="@readOnly">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="label" select="$myLabel"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="isError" select="key('errorKey',@id)"/>
				<xsl:if test="not($myLabel)">
					<xsl:call-template name="checkLabel">
						<xsl:with-param name="force" select="1"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:element name="input">
					<xsl:call-template name="commonControlAttributes">
						<xsl:with-param name="isError" select="$isError"/>
						<xsl:with-param name="name" select="@id"/>
						<xsl:with-param name="live" select="'off'"/>
						<xsl:with-param name="myLabel" select="$myLabel"/>
						<xsl:with-param name="value" select="text()"/>
					</xsl:call-template>
					<xsl:attribute name="type">
						<xsl:text>number</xsl:text>
					</xsl:attribute>
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
					<xsl:attribute name="autocomplete">
						<xsl:text>off</xsl:text>
					</xsl:attribute>
					
					<xsl:if test="@min">
						<xsl:attribute name="min">
							<xsl:value-of select="@min"/>
							<!-- NOTE: step may only be a non-integer if min is a non integer -->
							<xsl:if test="contains(@step,'.') and not(contains(@min,'.'))">
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
								<xsl:when test="not(@min) and contains(@step,'.')">
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
				</xsl:element>
				<xsl:call-template name="inlineError">
					<xsl:with-param name="errors" select="$isError"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
