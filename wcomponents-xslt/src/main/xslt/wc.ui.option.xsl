
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!--
		Option is a child of many components which derive from AbstractList:
		Null template for unmoded ui:option elements. This should never be invoked but is here for completeness.
	-->
	<xsl:template match="ui:option"/>

	<!-- Tranforms the options of a list into HTML option elements. -->
	<xsl:template match="ui:option" mode="selectableList">
		<xsl:variable name="value">
			<xsl:choose>
				<xsl:when test="@value">
					<xsl:value-of select="@value"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<option class="wc-option" value="{$value}">
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
		<xsl:variable name="element">
			<xsl:choose>
				<xsl:when test="number($single) eq 1">span</xsl:when>
				<xsl:otherwise>li</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$element}">
			<xsl:attribute name="class">
				<xsl:text>wc-option</xsl:text>
				<xsl:if test="parent::ui:optgroup">
					<xsl:text> wc_inoptgroup</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
