<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!--
		Transform for WImage. Simple 1:1 map with HTML IMG element
	-->
	<xsl:template match="ui:image">
		<xsl:element name="img">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:attribute name="src">
				<xsl:value-of select="@src"/>
			</xsl:attribute>
			<xsl:attribute name="alt">
				<xsl:value-of select="@alt"/>
			</xsl:attribute>
			<xsl:if test="@width">
				<xsl:attribute name="width">
					<xsl:value-of select="@width"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@height">
				<xsl:attribute name="height">
					<xsl:value-of select="@height"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@hidden"><xsl:attribute name="hidden"><xsl:text>hidden</xsl:text></xsl:attribute></xsl:if>
		</xsl:element>
		<xsl:if test="@editor">
			<button type="button" data-wc-editor="{@editor}" data-wc-selector="{@editor}" data-wc-img="{@id}" class="wc_btn_icon wc-invite">
				<span class="wc-off">Edit</span>
				<i aria-hidden="true" class="fa fa-pencil-square-o"></i>
			</button>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
