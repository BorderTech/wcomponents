
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<xsl:template match="ui:datefield[@readOnly and @allowPartial='true']">
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

	<xsl:template match="ui:datefield[@readOnly]">
		<time id="{@id}" class="{normalize-space(concat('wc-datefield ', @class))}" data-wc-component="datefield">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="@date">
					<xsl:attribute name="datetime">
						<xsl:value-of select="@date"/>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="text()"/>
				</xsl:otherwise>
			</xsl:choose>
		</time>
	</xsl:template>


	<xsl:template match="ui:datefield/@*">
		<xsl:attribute name="data-wc-{lower-case(name())}">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>

	<!--
		##############################################################################################################
		##############################################################################################################
		ui:datefield[@allowPartial='true' and not(@readOnly)]
		##############################################################################################################
		##############################################################################################################
	-->
	<xsl:template match="ui:datefield[not(@readOnly)]">
		<xsl:element name="wc-dateinput">
			<xsl:apply-templates select="@*"/>
			<xsl:attribute name="aria-busy">true</xsl:attribute>
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
			<xsl:choose>
				<xsl:when test="@date">
					<xsl:value-of select="@date"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="text()"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:element name="wc-fieldindicator">
				<xsl:apply-templates select="ui:fieldindicator"/>
			</xsl:element>
		</xsl:element>
		<!--
		<div id="{@id}" class="{normalize-space(concat('wc-datefield wc-input-wrapper ', @class))}" >
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
			
			
		</div>
		-->
	</xsl:template>

</xsl:stylesheet>
