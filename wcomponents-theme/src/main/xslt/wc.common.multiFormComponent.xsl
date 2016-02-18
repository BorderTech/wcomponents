<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.title.xsl"/>
	<xsl:import href="wc.ui.multiTextField.n.multiTextFieldContentRenderer.xsl"/>
	<xsl:import href="wc.ui.multiDropdown.n.multiDropDownContentRenderer.xsl"/>
	<xsl:import href="wc.common.makeLegend.xsl"/>
<!--
	Transforms for WMultiDropdown and WMultiTextField.

	Complex compound widgets. WMultiDropdown is a multi-selection tool which always
	has at least one selection. WMultiTextField is a compound widget to elicit
	multiple songle line text responses.
-->
	<xsl:template match="ui:multidropdown|ui:multitextfield">
		<xsl:variable name="readOnly">
			<xsl:if test="@readOnly">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="myLabel" select="key('labelKey',$id)[1]"/>
		
		<xsl:choose>
			<xsl:when test="$readOnly=1 and (self::ui:multidropdown[count(.//ui:option[@selected]) &lt;= 1])">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="applies" select=".//ui:option[@selected]"/>
					<xsl:with-param name="useReadOnlyMode" select="1"/>
					<xsl:with-param name="label" select="$myLabel"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$readOnly=1 and (self::ui:multitextfield[count(ui:value) &lt;= 1])">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="useReadOnlyMode" select="1"/>
					<xsl:with-param name="label" select="$myLabel"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$readOnly=1">
				<ul>
					<xsl:call-template name="commonAttributes"/>
					<xsl:attribute name="class">
						<xsl:call-template name="commonClassHelper"/>
						<xsl:text> -wc_list_nb</xsl:text>
					</xsl:attribute>
					<xsl:if test="$myLabel">
						<xsl:attribute name="aria-labelledby">
							<xsl:value-of select="$myLabel/@id"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="self::ui:multidropdown">
							<xsl:apply-templates select="ui:option[@selected]|ui:optgroup[ui:option[@selected]]" mode="readOnly">
								<xsl:with-param name="single" select="0"/>
							</xsl:apply-templates>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="ui:value" mode="readOnlyList"/>
						</xsl:otherwise>
					</xsl:choose>
				</ul>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="isError" select="key('errorKey',$id)"/>
				<fieldset aria-relevant="additions removals" aria-atomic="false">
					<xsl:call-template name="commonWrapperAttributes">
						<xsl:with-param name="isError" select="$isError"/>
						<xsl:with-param name="class">wc_mfc</xsl:with-param>
					</xsl:call-template>
					<xsl:if test="@min">
						<xsl:attribute name="${wc.common.attrib.min}">
							<xsl:value-of select="@min"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@max">
						<xsl:attribute name="${wc.common.attrib.max}">
							<xsl:value-of select="@max"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="makeLegend">
						<xsl:with-param name="myLabel" select="$myLabel"/>
					</xsl:call-template>
					<ul class="wc_list_nb">
						<xsl:choose>
							<!-- content transform is dependant upon the actual component being transformed-->
							<xsl:when test="self::ui:multidropdown">
								<xsl:call-template name="multiDropDownContentRenderer">
									<xsl:with-param name="myLabel" select="$myLabel"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="multiTextFieldContentRenderer">
									<xsl:with-param name="myLabel" select="$myLabel"/>
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</ul>
					<xsl:call-template name="inlineError">
						<xsl:with-param name="errors" select="$isError"/>
					</xsl:call-template>
				</fieldset>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
