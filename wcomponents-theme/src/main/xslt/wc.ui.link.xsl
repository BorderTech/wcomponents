<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.popups.xsl"/>
	<xsl:import href="wc.common.buttonLinkCommon.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Transform for WLink and WInternalLink. This should be a simple transform to a HTML
		anchor element. However, as usual things are not that simple.

		HTML A elements do not support a disabled state. This state is created using
		aria-disabled and a javascript helper. The disabled state of WLink is under
		investigation and may be removed.

		There is a type property which allows the control to be rendered as a button.
		Whilst we can apply styles to a HTML anchor element to make it look button-like
		it is not currently possible to render a link as a button in all browsers.
		instead we output a button element for these controls. In addition it is
		current framework policy that all controls which undertake client action other
		than pure navigation are output as buttons, and we are able to style a HTML
		button element to appear to be a link.

		Therefore if the ui:link has a ui:windowAttributes child in order to
		create a pop up window, and that child has any attributes other than a name
		(which is required) we output a button. When a ui:link is rendered
		as a link and it has a ui:windowAttributes child which has only a name attribute
		then the HTML ANCHOR element will have a target attribute.
	-->
	<xsl:template match="ui:link">
		<xsl:param name="imageAltText" select="''"/>
		<xsl:param name="ajax" select="''"/>
		<xsl:variable name="type" select="@type"/>
		<xsl:variable name="hasPopup">
			<xsl:choose>
				<xsl:when test="ui:windowAttributes[count(@*) gt 1] or ($type eq 'button' and ui:windowAttributes)">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="elementType">
			<xsl:choose>
				<xsl:when test="$type eq 'button' or number($hasPopup) eq 1">
					<xsl:text>button</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>a</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:element name="{$elementType}">
			<xsl:call-template name="buttonLinkCommonAttributes">
				<xsl:with-param name="elementType" select="$elementType"/>
				<xsl:with-param name="class">
					<xsl:if test="$elementType eq 'button' and not($type eq 'button')">
						<xsl:text> wc-linkbutton</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>

			<xsl:variable name="noopener" select="'noopener'"/>
			<xsl:variable name="noreferrer" select="'noreferrer'"/>

			<xsl:choose>
				<xsl:when test="$elementType eq 'a'">
					<xsl:attribute name="href">
						<xsl:value-of select="@url"/>
					</xsl:attribute>
					<xsl:if test="$ajax != ''">
						<xsl:attribute name="data-wc-ajaxalias">
							<xsl:value-of select="$ajax"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@rel or ui:windowAttributes">
						<xsl:attribute name="rel">
							<xsl:choose>
								<xsl:when test="@rel">
									<xsl:value-of select="@rel"/>
									<xsl:if test="ui:windowAttributes">
										<xsl:if test="not(contains(@rel, $noopener))">
											<xsl:value-of select="concat(' ', $noopener)"/>
										</xsl:if>
										<xsl:if test="not(contains(@rel, $noreferrer))">
											<xsl:value-of select="concat(' ', $noreferrer)"/>
										</xsl:if>
									</xsl:if>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="concat($noopener, ' ', $noreferrer)"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="ui:windowAttributes">
						<!--
							This  bit will only be called if the ui:windowAttributes child as only a name attribute,
							otherwise we would have gone to button.
						-->
						<xsl:attribute name="target">
							<xsl:value-of select="ui:windowAttributes/@name"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="type">
						<xsl:text>button</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="data-wc-url">
						<xsl:value-of select="@url"/>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="buttonLinkCommonContent">
				<xsl:with-param name="imageAltText" select="$imageAltText"/>
			</xsl:call-template>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
