
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!--
		WStyledText

		We have added some interesting CSS to the pre element to try to alleviate the
		issues of using pre and strict white space preservation. We did not want to
		merely use white-space:pre-wrap since this would not provide the strict white
		space preservation required by the component. Instead we set overflow-x:auto
		which will maintain white space but force horizontal scrolling if the element
		overflows.
	-->
	<xsl:template match="ui:text">
		<xsl:variable name="class">
			<xsl:text>wc-text</xsl:text>
			<xsl:if test="@type">
				<xsl:value-of select="concat(' wc-text-type-', @type)"/>
			</xsl:if>
			<xsl:if test="@class">
				<xsl:value-of select="concat(' ', @class)"/>
			</xsl:if>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="@space eq 'paragraphs'">
				<div class="{$class}">
					<xsl:apply-templates mode="para">
						<xsl:with-param name="type" select="@type"/>
					</xsl:apply-templates>

				</div>
			</xsl:when>
			<xsl:when test="@space">
				<pre class="{$class}">
					<xsl:apply-templates mode="pre">
						<xsl:with-param name="type" select="@type"/>
					</xsl:apply-templates>
				</pre>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="elementType">
					<xsl:call-template name="WStyledTextGetElementFromType">
						<xsl:with-param name="type" select="@type"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:element name="{$elementType}">
					<xsl:attribute name="class">
						<xsl:value-of select="$class"/>
					</xsl:attribute>
					<xsl:apply-templates />
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="*" mode="para">
		<p>
			<xsl:apply-templates select="."/>
			<xsl:if test="following-sibling::node()[1] eq following-sibling::text()[1]">
				<xsl:apply-templates select="following-sibling::text()[1]"/>
			</xsl:if>
		</p>
	</xsl:template>

	<xsl:template match="*" mode="pre">
		<xsl:apply-templates select="."/>
	</xsl:template>

	<!--
		Manipulates text nodes based on ui:text space and type attributes.

		param space: The space attribute of the parent ui:text element.
		param type: The type attribute (if any) of the parent ui:text element.
		  Defaults to 'plain' if the type attribute is not set.
	-->
	<xsl:template match="text()" mode="para">
		<xsl:param name="type" select="'plain'"/>
		<xsl:if test="not(preceding-sibling::node()) or preceding-sibling::node()[1] ne preceding-sibling::*[1]">
			<p>
				<xsl:call-template name="WStyledTextContent">
					<xsl:with-param name="type" select="$type"/>
				</xsl:call-template>
			</p>
		</xsl:if>
	</xsl:template>

	<!--
		Manipulates text nodes based on ui:text space and type attributes.

		param space: The space attribute of the parent ui:text element.
		param type: The type attribute (if any) of the parent ui:text element.
		  Defaults to 'plain' if the type attribute is not set.
	-->
	<xsl:template match="text()" mode="space">
		<xsl:param name="space" select="''"/>
		<xsl:param name="type" select="'plain'"/>
		<xsl:param name="class" select="''"/>
		<xsl:choose>
			<xsl:when test="$space eq 'paragraphs'">
				<p class="{$class}">
					<xsl:call-template name="WStyledTextContent">
						<xsl:with-param name="type" select="$type"/>
					</xsl:call-template>
				</p>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="WStyledTextContent">
					<xsl:with-param name="type" select="$type"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		Manipulates text nodes based on ui:text space and type attributes.

		param space: The space attribute of the parent ui:text element.
		param type: The type attribute (if any) of the parent ui:text element.
		  Defaults to 'plain' if the type attribute is not set.
	-->
	<xsl:template match="text()" mode="pre">
		<xsl:param name="type" select="'plain'"/>
		<xsl:call-template name="WStyledTextContent">
			<xsl:with-param name="type" select="$type"/>
		</xsl:call-template>
	</xsl:template>

	<!--
		Determines the HTML element appropriate for any give WStyledText based on
		the tyoe attribute of ui:text.
	-->
	<xsl:template name="WStyledTextGetElementFromType">
		<xsl:param name="type"/>
		<xsl:choose>
			<xsl:when test="$type eq 'emphasised' or $type eq 'highPriority'">
				<xsl:text>strong</xsl:text>
			</xsl:when>
			<xsl:when test="$type eq 'mediumPriority'">
				<xsl:text>em</xsl:text>
			</xsl:when>
			<xsl:when test="$type eq 'insert'">
				<xsl:text>ins</xsl:text>
			</xsl:when>
			<xsl:when test="$type eq 'delete'">
				<xsl:text>del</xsl:text>
			</xsl:when>
			<xsl:when test="self::ui:text or ($type ne '' and $type ne'plain')">
				<xsl:text>span</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- Some combinations of space and type mean we end up with one or more inner
		elements brfore we get to the content. This template is called from
		a moded template for text nodes. The inner element is output bfore the
		text content if required.
	-->
	<xsl:template name="WStyledTextContent">
		<xsl:param name="type"/>
		<xsl:variable name="innerElem">
			<xsl:call-template name="WStyledTextGetElementFromType">
				<xsl:with-param name="type" select="$type"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$type eq 'plain' or not($type)">
				<xsl:value-of select="."/>
			</xsl:when>
			<xsl:when test="$innerElem ne ''">
				<xsl:element name="{$innerElem}">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="ui:nl"/>
	<!--
		creates a newline character.

		NOTE: The allowed line separators (http://dev.w3.org/html5/markup/terminology.html)
		are:
		* a U+000D CARRIAGE RETURN (CR) character
		* a U+000A LINE FEED (LF) character
		* a U+000D CARRIAGE RETURN (CR) followed by a U+000A LINE FEED (LF) character

		However:
		* Using &#xD; by itself does not work in Chrome (on Windows at least);
		* Using &#xA; by itself does not work in IE,
		* Using &#xD;&#xA; does work in IE, Firefox (3.6+), Chrome (at least 19+
		but maybe earlier), Opera (at least 11.61+ but maybe earlier) and Safari
		(Windows 5.0.1+, maybe earlier) but should be tested on non-windows platforms.
	 -->

	<xsl:template match="ui:nl" mode="pre">
		<xsl:text>&#xD;&#xA;</xsl:text>
	</xsl:template>
</xsl:stylesheet>
