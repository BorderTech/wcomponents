<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.root.variables.xsl"/>
	<!--
		Point to a favicon. Most browsers insist on looking for one, it is not ideal to simply let them 404. 
		If there is a html:link element in the XML it will be picked and used, otherwise we need to make one.
		
		TODO: remove support for html:link[@rel='shortcut icon' or @rel='icon']
		since this can now be better and more efficiently handled using @icon.
	-->
	<xsl:template name="includeFavicon">
		<xsl:choose>
			<xsl:when test="ui:application/@icon">
				<xsl:call-template name="faviconHelper">
					<xsl:with-param name="href">
						<xsl:value-of select="ui:application[@icon][1]/@icon"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="//html:link[@rel='shortcut icon' or @rel='icon']">
				<xsl:comment>using link element</xsl:comment>
				<xsl:apply-templates select="//html:link[@rel='shortcut icon' or @rel='icon'][1]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="faviconHelper">
					<xsl:with-param name="href" select="concat($resourceRoot,'${images.target.dir.name}/favicon.ico')"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="faviconHelper">
		<xsl:param name="href"/>
		<xsl:if test="$href!=''">
			<xsl:element name="link">
				<xsl:attribute name="rel">
					<xsl:text>shortcut icon</xsl:text><!-- Invalid but the only cross browser option -->
				</xsl:attribute>
				<xsl:attribute name="href">
					<xsl:value-of select="$href"/>
				</xsl:attribute>
			</xsl:element>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
