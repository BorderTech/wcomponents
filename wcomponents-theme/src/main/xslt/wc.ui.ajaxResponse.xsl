<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		ui:ajaxResponse is the root element of a response to an ajax request. In
		most cases this is processed in JavaScript.
		
		When the ajaxResponse is sent as part of the pseudo-AJAX of WFileWidget then 
		this template is called directly by the XSLT processor since this pseudo-AJAX
		is actually a regular page load.
	
		An ajaxResponse consists of 0...n ajaxTargets.
	
		All ajax responses change element(s) already in the UI. These changes are
		notified to users by a visual update and by announcing the changes using
		the relevant WAI-ARIA states and properties.
		
		The defaultFocusId can be used to set focus to any WComponent which has its
		id property exposed in the UI. You cannot expect this focus request to be
		honoured as in most cases the user will continue work whilst the AJAX
		transaction takes place and the UI will almost always have a currently focussed
		element. This is <<always>> the case when the AJAX trigger component is not
		part of the replaced content.
		
		It is considered bad practice to update the UI at a point before (in source
		order) the current cursor point. For example one ought not use a button to
		replace content which occurs before that button (including in an earlier
		column). The WCAG 2.0 guidelines currently state that the trigger for an ajax
		event must <<immediately precede>> the changed content. This is considered
		controversial.
	
		Child Elements: 
			wc.ui.ajaxTarget.xsl
	
	
	
		This template undertakes one of two jobs:

		1. When no single ui:ajaxTarget child has a child of ui:file we
		create an output tree in which all children are output in a wrapper
		element for parsing by the JavaScript XSL transformer; or

		2. If an ui:ajaxTarget child has a ui:file child then the
		entire ajaxResponse is a pseudo-AJAX response from multiFileUpload and
		the transform creates a HTML document used for pseudo-AJAX file upload.
	-->
	<xsl:template match="ui:ajaxResponse">
		<xsl:choose>
			<xsl:when test="ui:ajaxTarget/node()[not(self::ui:file)]">
				<xsl:element name="div">
					<xsl:attribute name="class">wc-ajaxresponse</xsl:attribute>
					<xsl:if test="@defaultFocusId">
						<xsl:attribute name="data-focusid"><xsl:value-of select="@defaultFocusId"/></xsl:attribute>
					</xsl:if>
					<xsl:apply-templates />
					<xsl:apply-templates select=".//ui:dialog[ui:content][1]" mode="withcontent"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="html">
					<xsl:attribute name="lang">
						<xsl:value-of select="$lang"/>
					</xsl:attribute>
					<xsl:element name="head">
						<xsl:element name="title">
							<xsl:text>Pseudo AJAX iframe</xsl:text>
						</xsl:element>
					</xsl:element>
					<xsl:element name="body">
						<xsl:apply-templates mode="pseudoAjax"/>
					</xsl:element>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
