<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<!--  WRadioButtonSelect -->
	<xsl:template match="ui:radiobuttonselect">
		<xsl:choose>
			<xsl:when test="@readOnly">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="useReadOnlyMode" select="1"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<fieldset>
					<xsl:call-template name="commonWrapperAttributes"/>
					<xsl:if test="ui:option">
						<xsl:variable name="rows">
							<xsl:choose>
								<xsl:when test="@layout eq 'flat'">
									<xsl:number value="1"/>
								</xsl:when>
								<xsl:when test="not(@layoutColumnCount)">
									<xsl:number value="0"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="ceiling(count(ui:option) div number(@layoutColumnCount))"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:variable name="inputType">
							<xsl:text>radio</xsl:text>
						</xsl:variable>
						<xsl:choose>
							<xsl:when test="number($rows) le 1">
								<ul role="presentation">
									<xsl:attribute name="class">
										<xsl:choose>
											<xsl:when test="number($rows) eq 1">
												<xsl:text>wc-listlayout-type-flat wc-hgap-med</xsl:text>
											</xsl:when>
											<xsl:otherwise>
												<xsl:text>wc-vgap-sm</xsl:text>
											</xsl:otherwise>
										</xsl:choose>
										<xsl:text> wc_list_nb</xsl:text>
									</xsl:attribute>
									<xsl:apply-templates select="ui:option" mode="checkableGroupInList">
										<xsl:with-param name="type" select="$inputType"/>
									</xsl:apply-templates>
								</ul>
							</xsl:when>
							<xsl:otherwise>
								<div class="wc-row wc-hgap-med wc-respond">
									<xsl:apply-templates select="ui:option[position() mod number($rows) eq 1]" mode="checkableGroup">
										<xsl:with-param name="type" select="$inputType"/>
										<xsl:with-param name="rows" select="$rows"/>
									</xsl:apply-templates>
								</div>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
					<xsl:call-template name="hField"/>
				</fieldset>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
