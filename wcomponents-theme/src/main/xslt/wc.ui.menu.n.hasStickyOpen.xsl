<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		This template is used to indicate whether a WSubMenu open state should be honoured.
		
		param type:
		The WMenu Type. If type is not defined we are calling this template from a WSubMenu in an ajax response. These
		should be closed.
	-->
	<xsl:template name="hasStickyOpen">
		<xsl:param name="type" select="''"/>
		<xsl:choose>
			<!-- if type is not defined we are calling it from a submenu in
					an ajax response and do not allow anything to be open -->
			<xsl:when test="$type eq 'tree'">
				<xsl:number value="1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:number value="0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
