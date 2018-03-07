<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>

	<xsl:template match="ui:multifileupload[@readOnly]">
		<xsl:variable name="roClass">
			<xsl:if test="@ajax">
				<xsl:text>wc-ajax</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="not(ui:file)">
				<span>
					<xsl:call-template name="commonAttributes"/>
					<xsl:call-template name="roComponentName"/>
				</span>
			</xsl:when>
			<xsl:when test="not(@cols) or number(@cols) le 1 or number(@cols) ge count(ui:file)">
				<ul>
					<xsl:call-template name="commonAttributes">
						<xsl:with-param name="class">
							<xsl:value-of select="$roClass"/>
							<xsl:choose>
								<xsl:when test="@cols = 0 or number(@cols) ge count(ui:file)">
									<xsl:text> wc-listlayout-type-flat wc-hgap-sm</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text> wc-vgap-sm</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="roComponentName"/>
					<xsl:apply-templates />
				</ul>
			</xsl:when>
			<xsl:otherwise>
				<div data-wc-cols="{@cols}">
					<xsl:call-template name="commonWrapperAttributes">
						<xsl:with-param name="class" select="$roClass"/>
					</xsl:call-template>
					<xsl:call-template name="roComponentName"/>
					<xsl:variable name="rows" select="ceiling(count(ui:file) div number(@cols))"/>
					<div class="wc_files wc-row wc-hgap-med wc-respond">
						<xsl:apply-templates select="ui:file[position() mod number($rows) eq 1]" mode="columns">
							<xsl:with-param name="rows" select="$rows"/>
						</xsl:apply-templates>
					</div>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
