<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Null template for unmoded ui:optgroup elements. This should never be invoked but is here for completeness.
	-->
	<xsl:template match="ui:optgroup"/>

	<!-- Tranforms the optgroups of a list into HTML optgroup elements. -->
	<xsl:template match="ui:optgroup" mode="selectableList">
		<optgroup label="{@label}">
			<xsl:apply-templates mode="selectableList"/>
		</optgroup>
	</xsl:template>

	<!-- Transform for emulator for an optgroup in readOnly mode. -->
	<xsl:template match="ui:optgroup" mode="readOnly">
		<xsl:param name="showOptions" select="'selected'"/>
		<xsl:param name="className"/>
		<li class="wc_optgroup">
			<xsl:value-of select="@label"/>
		</li>
		<xsl:choose>
			<xsl:when test="$showOptions eq 'all'">
				<xsl:apply-templates select="ui:option" mode="readOnly">
					<xsl:with-param name="className" select="$className"/>
					<xsl:with-param name="single" select="0"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="ui:option[@selected]" mode="readOnly">
					<xsl:with-param name="className" select="$className"/>
					<xsl:with-param name="single" select="0"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
