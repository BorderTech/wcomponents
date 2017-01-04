<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:template match="ui:passwordfield">
		<xsl:variable name="id" select="@id"/>
		<xsl:choose>
			<xsl:when test="@readOnly">
				<xsl:call-template name="readOnlyControl">
					<!-- NEVER allow text content to appear as it is a value. -->
					<xsl:with-param name="applies" select="'none'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<span>
					<xsl:call-template name="commonAttributes">
						<xsl:with-param name="class">
							<xsl:text>wc_input_wrapper</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:element name="input">
						<xsl:call-template name="wrappedTextInputAttributes">
							<xsl:with-param name="type" select="'password'"/>
						</xsl:call-template>
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
						<!--<xsl:if test="@pattern">
						<xsl:attribute name="pattern">
							<xsl:value-of select="@pattern"/>
						</xsl:attribute>
					</xsl:if>-->
						<!-- #1007 - choose one or the other of these -->
						<xsl:if test="@autocomplete">
							<xsl:attribute name="autocomplete">
								<xsl:value-of select="@autocomplete"/>
							</xsl:attribute>
						</xsl:if>
						<!--<xsl:attribute name="autocomplete">
						<xsl:choose>
							<xsl:when test="@autocomplete">
								<xsl:value-of select="@autocomplete"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>off</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>-->
					</xsl:element>
				</span>
				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
