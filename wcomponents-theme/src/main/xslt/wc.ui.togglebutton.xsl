<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	
	<xsl:template match="ui:togglebutton">
		<xsl:variable name="id">
			<xsl:value-of select="@id"/>
		</xsl:variable>
		<xsl:variable name="name">
			<xsl:value-of select="$id"/>
		</xsl:variable>
		<xsl:variable name="myLabel" select="key('labelKey',$id)"/>
		<xsl:choose>
			<xsl:when test="@readOnly">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="class">
						<xsl:text>wc-icon</xsl:text>
						<xsl:if test="@selected">
							<xsl:text> wc_ro_sel</xsl:text>
						</xsl:if>
					</xsl:with-param>
					<xsl:with-param name="label" select="$myLabel[1]"/>
				</xsl:call-template>
				<xsl:call-template name="togglebuttonlabeltext"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="isError" select="key('errorKey',$id)"/>
				<button type="button" data-wc-name="{$id}" data-wc-value="{$t}" role="checkbox">
					<xsl:call-template name="commonControlAttributes">
						<xsl:with-param name="isError" select="$isError"/>
						<xsl:with-param name="myLabel" select="$myLabel[1]"/>
						<xsl:with-param name="class" select="'wc-nobutton wc-invite'"/>
					</xsl:call-template>
					<!-- Fortunately commonControlAttributes will only output a value attribute if
						the XML element has a value attribute; so we can add the ui:checkbox value
						here without changing the called template.
					-->
					<xsl:attribute name="value">
						<xsl:copy-of select="$t"/>
					</xsl:attribute>
					<xsl:attribute name="aria-checked">
						<xsl:choose>
							<xsl:when test="@selected">true</xsl:when>
							<xsl:otherwise>false</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:if test="@groupName">
						<xsl:attribute name="data-wc-cbgroup">
							<xsl:value-of select="@groupName"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="togglebuttonlabeltext"/>
				</button>
				<xsl:call-template name="hField">
					<xsl:with-param name="name" select="$name"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="togglebuttonlabeltext">
		<xsl:if test="normalize-space(text()) ne ''">
			<span class="wc-togglebutton-text">
				<xsl:apply-templates/>
			</span>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
