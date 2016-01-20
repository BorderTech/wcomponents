<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.ui.tabset.n.tabsetApply.xsl"/>
	
	<!--
		This helper template outputs the element with a role of tablist. This is the
		actual list of buttons which do the tab switching. It is very unlikely you would
		need to override this.
	-->
	<xsl:template name="doTabList">
		<xsl:param name="firstOpenTab"/>
		<xsl:element name="div">
			<xsl:attribute name="role">
				<xsl:text>tablist</xsl:text>
			</xsl:attribute>
			<xsl:if test="@type='accordion'">
				<xsl:attribute name="aria-multiselectable">
					<xsl:choose>
						<xsl:when test="@single">false</xsl:when>
						<xsl:otherwise>true</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="tabsetApply">
				<xsl:with-param name="firstOpenTab" select="$firstOpenTab"/>
			</xsl:call-template>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
