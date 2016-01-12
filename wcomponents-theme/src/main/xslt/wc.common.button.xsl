<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.buttonLinkCommon.xsl"/>
	<!--
		This is a group transform for WButton (including WConfirmationButton and
		WCancelButton) and WPrintButton.
	
		A WButton will always POST the form without confirmation unless:
		
		1 it has its @cancel property set in which case it will require 
		  confirmation if either:
			
			a the user has made changes; or
			
			b there are unsaved changes on the server as flagged by the
			WApplication or button's unsavedChanges attribute;
	
		or:
		
		2 it has a msg attribute set, in which case it will always require 
		  confirmation; or
		
		3 it is a child of a {{{./wc.ui.dialog.html}WDialog}} in which case it
		will launch the dialog but not POST the form; or
		
		4 it is an AJAX trigger, in which case it will fire the AJAX request.
	
		A WConfirmButton will POST the form only if the user clicks the OK 
		button on the confirm dialog <unless> it is an AJAX trigger in which 
		case the AJAX request will be fired after confirmation.
		
		A WButton with @cancel="true" will POST a form without confirmation if 
		the user has made no changes unless the button or the WApplication has
		an unsavedChanges attribute then the form will be POSTed only if the 
		user confirms they wish to cancel.
		
		A WPrintButton has only client side behaviour and will never POST the 
		form.
		
		
			
		The value of s button is a fixed value for all buttons and its purpose is only to inform WComponents which 
		button was used in a submission.	
	-->
	<xsl:template match="ui:button|ui:printButton">
		<button name="{@id}" value="x">
			<xsl:attribute name="type">
				<xsl:choose>
					<xsl:when test="self::ui:printButton or parent::ui:dialog">
						<xsl:text>button</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>submit</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			
			<xsl:attribute name="class">
				<xsl:value-of select="local-name(.)"/>
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
				<xsl:if test="@type='link'">
					<xsl:text> wc_btn_link</xsl:text>
				</xsl:if>
				<xsl:if test="@imagePosition">
					<xsl:value-of select="concat(' wc_btn_img',@imagePosition)"/>
				</xsl:if>
				<xsl:if test="@class">
					<xsl:value-of select="concat(' ', @class)"/>
				</xsl:if>
			</xsl:attribute>
			

			<!-- nothing else applies to print buttons -->
			<xsl:if test="self::ui:button">
				<xsl:if test="@msg">
					<xsl:attribute name="${wc.ui.button.attrib.confirmMessage}">
						<xsl:value-of select="@msg"/>
					</xsl:attribute>
				</xsl:if>
				<!--
				 The following do not do client side validation (if enabled in theme):
					* WCancelButton/@cancel="true""
					* Table action button
					* Ajax Triggers (unless the validates attribute is set specifically).
				-->
				<xsl:choose>
					<xsl:when test="@cancel or parent::ui:action">
						<xsl:attribute name="formnovalidate">
							<xsl:text>formnovalidate</xsl:text>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="@validates">
						<xsl:attribute name="${wc.ui.button.attribute.validates}">
							<xsl:value-of select="@validates"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="key('triggerKey',@id)">
						<!-- do not merge this with the top when as we _do_ validate on AJAX if the validates attribute is set. -->
						<xsl:attribute name="formnovalidate">
							<xsl:text>formnovalidate</xsl:text>
						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
				<xsl:if test="@popup or parent::ui:dialog">
					<xsl:attribute name="aria-haspopup">
						<xsl:copy-of select="$t"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="parent::ui:dialog">
						<xsl:attribute name="data-wc-dialogconf">
							<xsl:value-of select="parent::ui:dialog/@id"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="parent::ui:action">
						<xsl:variable name="conditions">
							<xsl:apply-templates select="../ui:condition" mode="action"/>
						</xsl:variable>
						<xsl:if test="$conditions != ''">
							<xsl:attribute name="${wc.ui.table.actions.attribute.conditions}">
								<xsl:text>[</xsl:text>
								<xsl:value-of select="$conditions"/>
								<xsl:text>]</xsl:text>
							</xsl:attribute>
						</xsl:if>
					</xsl:when>
				</xsl:choose>
			</xsl:if>
			<xsl:call-template name="buttonLinkCommon"/>
		</button>
	</xsl:template>
</xsl:stylesheet>
