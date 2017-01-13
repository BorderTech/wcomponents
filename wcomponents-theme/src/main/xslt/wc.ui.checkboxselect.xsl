<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<!--  WCheckBoxSelect -->
	<xsl:template match="ui:checkboxselect">
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
		<xsl:choose>
			<xsl:when test="@readOnly and count(ui:option[@selected]) le 1">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="applies" select="ui:option[@selected]"/>
					<xsl:with-param name="useReadOnlyMode" select="1"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@readOnly">
				<xsl:variable name="rows">
					<xsl:choose>
						<xsl:when test="number($cols) eq 1">
							<xsl:number value="0"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="ceiling(count(ui:option[@selected]) div $cols)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<div>
					<xsl:call-template name="commonWrapperAttributes">
						<xsl:with-param name="isControl" select="0"/>
						<xsl:with-param name="class">
							<xsl:text>wc_chkgrp</xsl:text>
							<xsl:if test="not(@frameless)">
								<xsl:text> wc_chkgrp_bdr</xsl:text>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="roComponentName"/>
					<xsl:choose>
						<xsl:when test="number($rows) eq 0">
							<ul>
								<xsl:attribute name="class">
									<xsl:text>wc_list_nb</xsl:text>
									<xsl:choose>
										<xsl:when test="@layout eq 'flat'">
											<xsl:text> wc-hgap-med</xsl:text>
										</xsl:when>
										<xsl:otherwise>
											<xsl:text> wc-vgap-sm</xsl:text>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
								<xsl:apply-templates select="ui:option[@selected]" mode="checkableGroupInList">
									<xsl:with-param name="inputName" select="@id"/>
									<xsl:with-param name="type" select="$inputType"/>
									<xsl:with-param name="readOnly" select="1"/>
								</xsl:apply-templates>
							</ul>
						</xsl:when>
						<xsl:when test="number($rows) eq 1">
							<xsl:apply-templates select="ui:option[@selected]" mode="checkableGroup">
								<xsl:with-param name="inputName" select="@id"/>
								<xsl:with-param name="type" select="$inputType"/>
								<xsl:with-param name="readOnly" select="1"/>
								<xsl:with-param name="rows" select="0"/>
							</xsl:apply-templates>
						</xsl:when>
						<xsl:otherwise>
							<div class="wc-row wc-hgap-med wc-respond">
								<xsl:apply-templates select="ui:option[@selected][position() mod number($rows) eq 1]" mode="checkableGroup">
									<xsl:with-param name="inputName" select="@id"/>
									<xsl:with-param name="type" select="$inputType"/>
									<xsl:with-param name="readOnly" select="1"/>
									<xsl:with-param name="rows" select="$rows"/>
								</xsl:apply-templates>
							</div>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</xsl:when>
			<xsl:otherwise>
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
						<xsl:with-param name="isControl" select="1"/>
						<xsl:with-param name="class">
							<xsl:text>wc_chkgrp</xsl:text>
							<xsl:if test="not(@frameless)">
								<xsl:text> wc_chkgrp_bdr</xsl:text>
							</xsl:if>
						</xsl:with-param>
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
					<xsl:variable name="rowClass">
						<xsl:text>wc-row wc-hgap-med wc-respond</xsl:text>
					</xsl:variable>
					<xsl:if test="ui:option">
						<xsl:choose>
							<xsl:when test="number($rows) eq 0 and ui:option">
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
										<xsl:with-param name="readOnly" select="0"/>
									</xsl:apply-templates>
								</div>
							</xsl:when>
							<xsl:when test="number($rows) eq 1 and  ui:option">
								<xsl:apply-templates select="ui:option" mode="checkableGroup">
									<xsl:with-param name="inputName" select="@id"/>
									<xsl:with-param name="type" select="$inputType"/>
									<xsl:with-param name="readOnly" select="0"/>
									<xsl:with-param name="rows" select="0"/>
								</xsl:apply-templates>
							</xsl:when>
							<xsl:when test="ui:option">
								<div class="{$rowClass}">
									<xsl:apply-templates select="ui:option[position() mod number($rows) eq 1]" mode="checkableGroup">
										<xsl:with-param name="inputName" select="@id"/>
										<xsl:with-param name="type" select="$inputType"/>
										<xsl:with-param name="readOnly" select="0"/>
										<xsl:with-param name="rows" select="$rows"/>
									</xsl:apply-templates>
								</div>
							</xsl:when>
						</xsl:choose>
					</xsl:if>
					<xsl:call-template name="hField"/>
				</fieldset>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
