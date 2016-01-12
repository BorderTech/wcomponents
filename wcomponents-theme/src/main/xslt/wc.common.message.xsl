<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Transform for ui:message.
		
		ui:message is a child of either a WMessageBox, WFieldWarningIndicator or a WFieldErrorIndicator.
		See:
			wc.ui.messageBox.xsl
			wc.ui.fieldIndicator.xsl

		The String used to populate the message may contain white space (which is honoured when output in a message box 
		(wc.ui.messageBox.xsl) but not when output inline or in a WFieldWarningIndicator (wc.ui.fieldIndicator.xsl); and
		may contain HTML mark up so long as that mark up is valid, HTML5 compliant and complies with the allowed content
		type.
	-->
	<xsl:template match="ui:message">
		<li>
			<xsl:attribute name="class">
				<xsl:text>message</xsl:text>
				<xsl:if test="@class">
					<xsl:value-of select="concat(' ', @class)"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates/>
		</li>
	</xsl:template>
</xsl:stylesheet>
