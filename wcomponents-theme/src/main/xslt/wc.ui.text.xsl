<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
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
		<xsl:variable name="type" select="@type"/>
		<xsl:choose>
			<xsl:when test="@space='paragraphs'">
				<div>
					<xsl:call-template name="makeCommonClass"/>
					<xsl:apply-templates mode="para">
						<xsl:with-param name="type" select="@type"/>
					</xsl:apply-templates>
					
				</div>
			</xsl:when>
			<xsl:when test="@space">
				<pre>
					<xsl:call-template name="makeCommonClass"/>
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
					<xsl:call-template name="makeCommonClass"/>
					<xsl:apply-templates />
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="*" mode="para">
		<p>
			<xsl:apply-templates select="."/>
			<xsl:if test="following-sibling::node()[1] = following-sibling::text()[1]">
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
		<xsl:if test="not(preceding-sibling::node()) or preceding-sibling::node()[1] != preceding-sibling::*[1]">
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
		<xsl:param name="space"/>
		<xsl:param name="type" select="'plain'"/>
		<xsl:param name="class"/>
		<xsl:choose>
			<xsl:when test="$space='paragraphs'">
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
			<xsl:when test="$type='emphasised' or $type='highPriority'">
				<xsl:text>strong</xsl:text>
			</xsl:when>
			<xsl:when test="$type='mediumPriority'">
				<xsl:text>em</xsl:text>
			</xsl:when>
			<xsl:when test="$type='insert'">
				<xsl:text>ins</xsl:text>
			</xsl:when>
			<xsl:when test="$type='delete'">
				<xsl:text>del</xsl:text>
			</xsl:when>
			<xsl:when test="self::ui:text or ($type!='' and $type !='plain')">
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
			<xsl:when test="$type='plain' or not($type)">
				<xsl:value-of select="."/>
			</xsl:when>
			<xsl:when test="$innerElem !=''">
				<xsl:element name="{$innerElem}">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
