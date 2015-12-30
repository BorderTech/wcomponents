<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Creates a caption element if required. Called from the transform for ui:table.
	-->
	<xsl:template name="caption">
		<xsl:param name="hasRowSelection" select="0"/>
		<xsl:param name="hasExpandAll" select="0"/>
		<xsl:if test="@caption or ($hasExpandAll + $hasRowSelection &gt; 0)">
			<caption>
				<xsl:if test="@caption">
					<span>
						<xsl:value-of select="@caption"/>
					</span>
				</xsl:if>
				<xsl:if test="$hasExpandAll + $hasRowSelection &gt; 0">
					<div class="wc_row">
						<xsl:choose>
							<xsl:when test="$hasExpandAll + $hasRowSelection = 2">
								<div>
									<xsl:apply-templates select="ui:rowSelection"/>
								</div>
								<div>
									<xsl:apply-templates select="ui:rowExpansion"/>
								</div>
							</xsl:when>
							<xsl:when test="$hasRowSelection = 1">
								<div>
									<xsl:apply-templates select="ui:rowSelection"/>
								</div>
							</xsl:when>
							<xsl:otherwise>
								<div>
									<xsl:apply-templates select="ui:rowExpansion"/>
								</div>
							</xsl:otherwise>
						</xsl:choose>
					</div>
				</xsl:if>
			</caption>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
