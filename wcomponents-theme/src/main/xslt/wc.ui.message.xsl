<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Transform for ui:message.
		
		ui:message is a child of either a WMessageBox, WFieldWarningIndicator or a WFieldErrorIndicator.
		See:
			wc.ui.messageBox.xsl
			wc.ui.fieldIndicator.xsl
	-->
	<xsl:template match="ui:message">
		<xsl:variable name="element">
			<xsl:choose>
				<xsl:when test="parent::ui:messagebox">
					<xsl:text>div</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>span</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:element name="{$element}">
			<xsl:call-template name="makeCommonClass"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
