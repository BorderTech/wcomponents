<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Creates a container for controls at the bottom of a table. 

		You probably do not need to override this but it should be safe to do so so long as any hard-coded
		class attribute values are left in place.
	-->
	<xsl:template name="tableBottomControls">
		<xsl:variable name="showPagination">
			<xsl:choose>
				<xsl:when test="ui:pagination and not(ui:pagination/@controls eq 'top')">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="ui:actions or number($showPagination) eq 1">
			<div class="wc_table_bottom_controls">
				<xsl:if test="number($showPagination) eq 1">
					<div class="wc_table_pag_cont">
						<xsl:apply-templates select="ui:pagination"/>
					</div>
				</xsl:if>
				<xsl:if test="ui:actions">
					<div class="wc_table_actions">
						<xsl:apply-templates select="ui:actions"/>
					</div>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
