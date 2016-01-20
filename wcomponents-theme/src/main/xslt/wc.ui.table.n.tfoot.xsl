<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Creates a container for controls at the bottom of a table. You probably do not need to override this.
		See transform for ui:table in wc.ui.table.xsl for information about the parameters.
	-->
	<xsl:template name="tableBottomControls">
		<xsl:variable name="showPagination">
			<xsl:choose>
				<xsl:when test="ui:pagination and not(ui:pagination/@controls='top')">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="ui:actions or $showPagination=1">
			<div>
				<xsl:if test="$showPagination=1">
					<div class="wc_table_pag_cont">
						<xsl:apply-templates select="ui:pagination"/>
					</div>
				</xsl:if>
				<xsl:if test="ui:actions">
					<div>
						<xsl:apply-templates select="ui:actions"/>
					</div>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
