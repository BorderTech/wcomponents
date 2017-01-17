<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<!--  WCheckBoxSelect -->
	<xsl:template match="ui:checkboxselect">
		<xsl:variable name="commonclass">
			<xsl:text>wc_chkgrp</xsl:text>
			<xsl:if test="not(@frameless)">
				<xsl:text> wc_chkgrp_bdr</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="@readOnly and (not(@layoutColumnCount) or @layoutColumnCount &lt;= 1 or count(ui:option) le 1)">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="isList" select="1"/>
					<xsl:with-param name="useReadOnlyMode" select="1"/>
					<xsl:with-param name="class">
						<xsl:value-of select="$commonclass"/>
						<xsl:if test="count(ui:option) gt 1">
							<xsl:choose>
								<xsl:when test="@layout eq 'flat'">
									<xsl:text> wc-hgap-med</xsl:text>
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
					<xsl:call-template name="commonWrapperAttributes">
						<xsl:with-param name="class">
							<xsl:value-of select="$commonclass"/>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="roComponentName"/>
					<xsl:variable name="cols" select="@layoutColumnCount"/>
					<xsl:variable name="rows" select="ceiling(count(ui:option) div $cols)"/>
					<xsl:if test="ui:option">
						<div class="wc-row wc-hgap-med wc-respond">
							<xsl:apply-templates select="ui:option[position() mod number($rows) eq 1]" mode="checkableGroup">
								<xsl:with-param name="inputName" select="@id"/>
								<xsl:with-param name="type" select="'checkbox'"/>
								<xsl:with-param name="readOnly" select="1"/>
								<xsl:with-param name="rows" select="$rows"/>
							</xsl:apply-templates>
						</div>
					</xsl:if>
					<xsl:call-template name="hField"/>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="inputType">
					<xsl:text>checkbox</xsl:text>
				</xsl:variable>
				<xsl:variable name="cols">
					<xsl:choose>
						<xsl:when test="not(@layoutColumnCount)">
							<xsl:number value="1"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:number value="number(@layoutColumnCount)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="rows">
					<xsl:choose>
						<xsl:when test="number($cols) eq 1">
							<xsl:number value="0"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="ceiling(count(ui:option) div $cols)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<fieldset>
					<xsl:call-template name="commonWrapperAttributes">
						<xsl:with-param name="class" select="$commonclass"/>
					</xsl:call-template>
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
						<xsl:choose>
							<xsl:when test="number($rows) eq 0">
								<div>
									<xsl:attribute name="class">
										<xsl:choose>
											<xsl:when test="@layout eq 'flat'">
												<xsl:text>wc-hgap-med</xsl:text>
											</xsl:when>
											<xsl:otherwise>
												<xsl:text>wc-vgap-sm</xsl:text>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:attribute>
									<xsl:apply-templates select="ui:option" mode="checkableGroupInList">
										<xsl:with-param name="inputName" select="@id"/>
										<xsl:with-param name="type" select="$inputType"/>
									</xsl:apply-templates>
								</div>
							</xsl:when>
							<xsl:when test="number($rows) eq 1">
								<xsl:apply-templates select="ui:option" mode="checkableGroup">
									<xsl:with-param name="inputName" select="@id"/>
									<xsl:with-param name="type" select="$inputType"/>
									<xsl:with-param name="rows" select="0"/>
								</xsl:apply-templates>
							</xsl:when>
							<xsl:otherwise>
								<div class="wc-row wc-hgap-med wc-respond">
									<xsl:apply-templates select="ui:option[position() mod number($rows) eq 1]" mode="checkableGroup">
										<xsl:with-param name="inputName" select="@id"/>
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
