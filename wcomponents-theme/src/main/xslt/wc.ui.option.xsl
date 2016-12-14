<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.fauxOption.xsl"/>
	<!--
		Option is a child of many components which derive from AbstractList:
		Null template for unmoded ui:option elements. This should never be invoked but is here for completeness.
	-->
	<xsl:template match="ui:option"/>

	<!-- Tranforms the options of a list into HTML option elements. -->
	<xsl:template match="ui:option" mode="selectableList">
		<xsl:variable name="value" select="@value"/>
		<option>
			<xsl:attribute name="value">
				<xsl:choose>
					<xsl:when test="$value">
						<xsl:value-of select="$value"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="."/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="@selected">
				<xsl:attribute name="selected">selected</xsl:attribute>
			</xsl:if>
			<xsl:if test="@isNull">
				<xsl:attribute name="data-wc-null">
					<xsl:text>1</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="."/>
		</option>
	</xsl:template>

	<!--
		Outputs an option emulator. This is a list item which, if it is a child of an optgroup element, is classed to be styled similar to a HTML
		option element nested in an optgroup element in a select element.
	-->
	<xsl:template match="ui:option" mode="readOnly">
		<xsl:param name="single" select="1"/>
		<xsl:param name="className" select="''"/>
		<xsl:choose>
			<xsl:when test="number($single) eq 1">
				<xsl:value-of select="."/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="class">
					<xsl:if test="parent::ui:optgroup">wc_inoptgroup</xsl:if>
					<xsl:if test="$className ne ''">
						<xsl:value-of select="concat(' ',$className)"/>
					</xsl:if>
				</xsl:variable>
				<li>
					<xsl:if test="normalize-space($class) ne ''">
						<xsl:attribute name="class">
							<xsl:value-of select="normalize-space($class)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="."/>
				</li>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		Output the options in a datalist element for a native HTML5 Combo. NOTE: we do not output a name attribute as in WComponents for
		WDropdown.COMBO the name and value are always identical.
	-->
	<xsl:template match="ui:option" mode="comboDataList">
		<xsl:call-template name="fauxOption">
			<xsl:with-param name="value">
				<xsl:value-of select="."/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
