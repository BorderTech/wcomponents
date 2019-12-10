
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!--
		Creating a label is not as simple as it may appear. A HTML Label element is specific in its purpose. It may only
		be used to label a labelable element.

		WLabel may be "for" something which is not a labelable element, such as a multiSelectPair, or even not "for"
		anything. Therefore, in order to determine what (if anything) to output we need to find out what we are
		labelling.

		ui:label is also a child of WField and as such has special output parameters depending upon the contents of the
		field's input child. This makes labels even more complicated.

		In some cases the ui:label should be output as part of another transform and not putput at all in-situ. The
		current components which pull the ui:label are ui:radiobutton, ui:checkbox and ui:selecttoggle[@renderAs='control'].
	-->
	<xsl:template match="ui:label">
		<xsl:variable name="class">
			<xsl:text>wc-label</xsl:text>
			<xsl:if test="@hidden">
				<xsl:text> wc-off</xsl:text>
			</xsl:if>
			<xsl:if test="@required and not(@readonly)">
				<xsl:text> wc_req</xsl:text>
			</xsl:if>
			<xsl:if test="@class">
				<xsl:value-of select="concat(' ', @class)"/>
			</xsl:if>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="@readonly or not(@what) or @what eq 'group'">
				<span id="{@id}" class="{normalize-space($class)}">
					<xsl:if test="@hiddencomponent">
						<xsl:attribute name="hidden">
							<xsl:text>hidden</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@toolTip">
						<xsl:attribute name="title">
							<xsl:value-of select="@toolTip"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@what">
						<xsl:choose>
							<xsl:when test="@readonly">
								<xsl:attribute name="data-wc-rofor">
									<xsl:value-of select="@for"/>
								</xsl:attribute>
							</xsl:when>
							<xsl:when test="@what eq 'group'">
								<xsl:attribute name="data-wc-for">
									<xsl:value-of select="@for"/>
								</xsl:attribute>
								<xsl:attribute name="aria-hidden">
									<xsl:text>true</xsl:text>
								</xsl:attribute>
								<xsl:if test="@accessKey">
									<xsl:attribute name="data-wc-accesskey">
										<xsl:value-of select="@accessKey"/>
									</xsl:attribute>
									<xsl:attribute name="aria-describedby">
										<xsl:value-of select="concat(@id, '_wctt')"/>
									</xsl:attribute>
									<span hidden="hidden" id="{concat(@id,'_wctt')}" role="tooltip">
										<xsl:value-of select="@accessKey"/>
									</span>
								</xsl:if>
							</xsl:when>
						</xsl:choose>
					</xsl:if>
					<xsl:apply-templates/>
					<xsl:if test="@what eq 'group'">
						<i aria-hidden="true" class="fa fa-asterisk"/>
						<xsl:if test="@hint">
							<span class="wc-label-hint">
								<xsl:value-of select="@hint"/>
							</span>
						</xsl:if>
					</xsl:if>
				</span>
			</xsl:when>
			<xsl:otherwise>
				<!-- @what is 'input' -->
				<label for="{concat(@for,'_input')}" id="{@id}" class="{normalize-space($class)}">
					<xsl:if test="@hiddencomponent">
						<xsl:attribute name="hidden">
							<xsl:text>hidden</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@toolTip">
						<xsl:attribute name="title">
							<xsl:value-of select="@toolTip"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@accessKey">
						<xsl:attribute name="accesskey">
							<xsl:value-of select="@accessKey"/>
						</xsl:attribute>
						<xsl:attribute name="aria-describedby">
							<xsl:value-of select="concat(@id, '_wctt')"/>
						</xsl:attribute>
						<span hidden="hidden" id="{concat(@id,'_wctt')}" role="tooltip">
							<xsl:value-of select="@accessKey"/>
						</span>
					</xsl:if>
					<xsl:apply-templates/>
					<i aria-hidden="true" class="fa fa-asterisk"/>
					<xsl:if test="@hint">
						<span class="wc-label-hint">
							<xsl:value-of select="@hint"/>
						</span>
					</xsl:if>
				</label>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
