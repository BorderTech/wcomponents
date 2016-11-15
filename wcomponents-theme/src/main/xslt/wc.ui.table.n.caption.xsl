<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Creates a caption element if required. Called from the transform for ui:table.

		Safe to override.
	-->
	<xsl:template name="caption">
		<xsl:if test="@caption or ui:tbody/ui:nodata">
			<caption>
				<div class="wc-caption">
					<xsl:value-of select="@caption"/>
				</div>
				<xsl:if test="ui:tbody/ui:nodata">
					<div class="wc_tbl_nodata">
						<xsl:apply-templates select="ui:tbody/ui:nodata"/>
					</div>
				</xsl:if>
			</caption>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
