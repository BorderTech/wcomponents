<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<!-- WSingleSelect WMultiSelect -->
	<xsl:template match="ui:listbox">
		<xsl:choose>
			<xsl:when test="@readOnly and @single">
				<span>
					<xsl:call-template name="commonAttributes">
						<xsl:with-param name="class">
							<xsl:text>wc-ro-input</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="roComponentName"/>
					<xsl:apply-templates select=".//ui:option" mode="readOnly" />
				</span>
			</xsl:when>
			<xsl:when test="@readOnly">
				<ul>
					<xsl:call-template name="commonAttributes">
						<xsl:with-param name="class">
							<xsl:text>wc-ro-input wc-vgap-sm</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="roComponentName"/>
					<xsl:apply-templates select="ui:option|ui:optgroup[ui:option]" mode="readOnly">
						<xsl:with-param name="single" select="0"/>
					</xsl:apply-templates>
				</ul>
			</xsl:when>
			<xsl:otherwise>
				<span>
					<xsl:call-template name="commonInputWrapperAttributes"/>
					<xsl:if test="@data">
						<xsl:attribute name="data-wc-list">
							<xsl:value-of select="@data"/>
						</xsl:attribute>
					</xsl:if>
					<select>
						<xsl:call-template name="wrappedInputAttributes"/>
						<xsl:if test="not(@single)">
							<xsl:attribute name="multiple">
								<xsl:text>multiple</xsl:text>
							</xsl:attribute>
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
						</xsl:if>
						<xsl:if test="@rows">
							<xsl:attribute name="size">
								<xsl:value-of select="@rows"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="@autocomplete">
							<xsl:attribute name="autocomplete">
								<xsl:value-of select="@autocomplete"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:apply-templates select="ui:option|ui:optgroup" mode="selectableList"/>
					</select>
					<xsl:call-template name="hField"/>
					<xsl:apply-templates select="ui:fieldindicator"/>
				</span>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
