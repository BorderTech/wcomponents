<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<!--  WCheckBoxSelect -->
	<xsl:template match="ui:checkboxselect">
		<xsl:choose>
			<xsl:when test="@readOnly and count(ui:option) le 1">
				<xsl:call-template name="readOnlyControl"/>
			</xsl:when>
			<xsl:when test="@readOnly and not(@layoutColumnCount) or @layoutColumnCount &lt;= 1">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="isList" select="1"/>
					<xsl:with-param name="class">
						<xsl:if test="count(ui:option) gt 1">
							<xsl:choose>
								<xsl:when test="@layout eq 'flat'">
									<xsl:text> wc-hgap-med wc-listlayout-type-flat</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text> wc-vgap-sm</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@readOnly">
				<div>
					<xsl:call-template name="commonWrapperAttributes"/>
					<xsl:call-template name="roComponentName"/>
					<xsl:variable name="roRows" select="ceiling(count(ui:option) div number(@layoutColumnCount))"/>
					<xsl:if test="ui:option">
						<div class="wc-row wc-hgap-med wc-respond">
							<xsl:apply-templates select="ui:option[position() mod number($roRows) eq 1]" mode="checkableGroup">
								<xsl:with-param name="rows" select="$roRows"/>
							</xsl:apply-templates>
						</div>
					</xsl:if>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<fieldset>
					<xsl:call-template name="commonWrapperAttributes"/>
					<xsl:if test="@min">
						<xsl:attribute name="data-wc-min">
							<xsl:value-of select="@min"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@max">
						<xsl:attribute name="data-wc-max">
							<xsl:value-of select="@max"/>
						</xsl:attribute>
					</xsl:if>
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
							<xsl:text>checkbox</xsl:text>
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
					<xsl:apply-templates select="ui:diagnostic"/>
				</fieldset>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
