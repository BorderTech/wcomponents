<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.ui.text.n.WStyledTextGetElementFromType.xsl"/>
	<xsl:import href="wc.ui.text.n.WStyledTextContent.xsl"/>
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
					<xsl:apply-templates select="text()" mode="para">
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
</xsl:stylesheet>
