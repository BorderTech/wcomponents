
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!-- WCollapsible -->
	<xsl:template match="ui:collapsible">
		<xsl:variable name="margin">
			<xsl:apply-templates select="ui:margin" mode="asclass"/>
		</xsl:variable>
		<details id="{@id}" class="{normalize-space(concat('wc-collapsble ', @class, $margin))}">
			<xsl:if test="not(@collapsed)">
				<xsl:attribute name="open">
					<xsl:text>open</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@groupName and @groupName != @id">
				<xsl:attribute name="data-wc-group">
					<xsl:value-of select="@groupName"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<summary tabindex="0">
				<xsl:variable name="iconclass">
					<xsl:text>fa-caret-</xsl:text>
					<xsl:choose>
						<xsl:when test="@collapsed">right</xsl:when>
						<xsl:otherwise>down</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<i aria-hidden="true" class="fa {$iconclass}"/>
				<xsl:choose>
					<xsl:when test="@level">
						<xsl:element name="h{@level}">
							<xsl:apply-templates select="ui:decoratedlabel"/>
						</xsl:element>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="ui:decoratedlabel"/>
					</xsl:otherwise>
				</xsl:choose>
			</summary>
			<xsl:variable name="isAjax">
				<xsl:choose>
					<xsl:when test="@mode eq 'dynamic' or @mode eq 'eager' or (@mode eq 'lazy' and @collapsed)">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:apply-templates select="ui:content" mode="collapsible">
				<xsl:with-param name="class">
					<xsl:if test="number($isAjax) eq 1">
						<xsl:text>wc_magic</xsl:text>
						<xsl:if test="@mode eq 'dynamic'">
							<xsl:text> wc_dynamic</xsl:text>
						</xsl:if>
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="ajaxId">
					<xsl:if test="number($isAjax) eq 1">
						<xsl:value-of select="@id"/>
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="labelId" select="ui:decoratedlabel/@id"/>
			</xsl:apply-templates>
		</details>
	</xsl:template>


	<xsl:template match="ui:content" mode="collapsible">
		<xsl:param name="class" select="''"/>
		<xsl:param name="ajaxId" select="''"/>
		<xsl:param name="labelId" select="''"/>
		<div class="{normalize-space(concat('wc-section ', $class))}">
			<xsl:if test="@id">
				<xsl:attribute name="id">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$ajaxId ne ''">
				<xsl:attribute name="data-wc-ajaxalias">
					<xsl:value-of select="$ajaxId"/>
				</xsl:attribute>
				<xsl:attribute name="aria-live">
					<xsl:text>polite</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$labelId ne ''">
				<xsl:attribute name="aria-describedby">
					<xsl:value-of select="$labelId"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates />
		</div>
	</xsl:template>
</xsl:stylesheet>
