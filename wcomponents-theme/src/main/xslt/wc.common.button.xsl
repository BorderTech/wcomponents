<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.buttonLinkCommon.xsl" />
	<!-- WButton (including WConfirmationButton and WCancelButton) and WPrintButton.

		The value of s button is a fixed value for all buttons and its purpose is only to inform WComponents which button was used in a submission.
	-->
	<xsl:template match="ui:button | ui:printbutton">
		<button name="{@id}" value="x">
			<xsl:call-template name="buttonLinkCommonAttributes">
				<xsl:with-param name="class">
					<xsl:if test="self::ui:button">
						<xsl:if test="@unsavedChanges">
							<xsl:text> wc_unsaved</xsl:text>
						</xsl:if>
						<xsl:if test="@cancel">
							<xsl:text> wc_btn_cancel</xsl:text>
						</xsl:if>
						<xsl:if test="parent::ui:action">
							<xsl:text> wc_table_cond</xsl:text>
						</xsl:if>
					</xsl:if>
					<xsl:if test="@type eq 'link'">
						<xsl:text> wc-linkbutton</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:attribute name="type">
				<xsl:choose>
					<xsl:when test="self::ui:printbutton or @client">
						<xsl:text>button</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>submit</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<!-- nothing else applies to print buttons -->
			<xsl:if test="self::ui:button">
				<xsl:if test="@msg">
					<xsl:attribute name="data-wc-btnmsg">
						<xsl:value-of select="@msg" />
					</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="@cancel or parent::ui:action">
						<xsl:attribute name="formnovalidate">
							<xsl:text>formnovalidate</xsl:text>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="@validates">
						<xsl:attribute name="data-wc-validate">
							<xsl:value-of select="@validates" />
						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
				<xsl:if test="parent::ui:action">
					<xsl:variable name="conditions">
						<xsl:apply-templates select="../ui:condition" mode="action" />
					</xsl:variable>
					<xsl:if test="$conditions ne ''">
						<xsl:attribute name="data-wc-condition">
							<xsl:text>[</xsl:text>
							<xsl:value-of select="$conditions" />
							<xsl:text>]</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
			</xsl:if>
			<xsl:call-template name="buttonLinkCommonContent" />
		</button>
	</xsl:template>
</xsl:stylesheet>
