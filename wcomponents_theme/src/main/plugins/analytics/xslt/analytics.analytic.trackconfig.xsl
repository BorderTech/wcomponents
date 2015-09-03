<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Config info for the page child of ui:application and ui:ajaxResponse
		
	-->
	<xsl:template match="ui:analytic" mode="analyticsconfig">
			<xsl:if test="@clientId or @cd or @dcd">
				<xsl:variable name="cid" select="@clientId"/>
				<xsl:variable name="cd" select="@cd"/>
				<xsl:variable name="dcd" select="@dcd"/>
				
				<xsl:text>,"${analytics.core.path.name}/${analytics.core.module.name}":{</xsl:text>
				<xsl:if test="$cid">
					<xsl:value-of select="concat('clientId:&quot;',$cid,'&quot;')"/>
				</xsl:if>
				<xsl:if test="$cd">
					<xsl:if test="$cid">
						<xsl:text>,</xsl:text>
					</xsl:if>
					<xsl:value-of select="concat('cd:&quot;',$cd,'&quot;')"/>
				</xsl:if>
				<xsl:if test="$dcd">
					<xsl:if test="$cid or $cd">
						<xsl:text>,</xsl:text>
					</xsl:if>
					<xsl:value-of select="concat('dcd:&quot;',$dcd,'&quot;')"/>
				</xsl:if>
				<xsl:text>}</xsl:text>
			</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>