<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Transform for ui:margin which is a common sub-element of layout components. It 
		is used to place a margin on 1..4 sides of a component. Uses CSS margin 
		shorthand notation and outputs nothing if all margins are 0.
	-->
	<xsl:template match="ui:margin">
		<xsl:param name="style"/>
		<xsl:variable name="margin">
			<xsl:choose>
				<xsl:when test="@all">
					<xsl:call-template name="pxWithUnit">
						<xsl:with-param name="gap" select="@all"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<!-- Note to JAVA peeps (web peeps, here is how to suck eggs)
						the order of entries for a margin short form CSS rule is 
							north east south west
					-->
					<xsl:call-template name="pxWithUnit">
						<xsl:with-param name="gap" select="@north"/>
					</xsl:call-template>
					<xsl:value-of select="' '"/>
					<xsl:call-template name="pxWithUnit">
						<xsl:with-param name="gap" select="@east"/>
					</xsl:call-template>
					<xsl:value-of select="' '"/>
					<xsl:call-template name="pxWithUnit">
						<xsl:with-param name="gap" select="@south"/>
					</xsl:call-template>
					<xsl:value-of select="' '"/>
					<xsl:call-template name="pxWithUnit">
						<xsl:with-param name="gap" select="@west"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="$margin != '' or $style != ''">
			<xsl:attribute name="style">
				<xsl:if test="$margin != ''">
					<xsl:value-of select="concat('margin:',$margin,';')"/>
				</xsl:if>
				<xsl:if test="$style != ''">
					<xsl:value-of select="$style"/>
				</xsl:if>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="pxWithUnit">
		<xsl:param name="gap" select="'0'"/>
		<xsl:value-of select="$gap"/>
		
		<xsl:if test="$gap !='0'">
			<xsl:text>px</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
