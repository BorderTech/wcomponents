<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" 
	version="2.0">
	<xsl:import href="wc.common.attributes.xsl" />

	<!-- WButton (including WConfirmationButton and WCancelButton) -->
	<xsl:template match="ui:button">
		<button id="{@id}" name="{@id}" value="x">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="@unsavedChanges">
						<xsl:text> wc_unsaved</xsl:text>
					</xsl:if>
					<xsl:if test="@cancel">
						<xsl:text> wc_btn_cancel</xsl:text>
					</xsl:if>
					<xsl:if test="@type">
						<xsl:text> wc-linkbutton</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:if test="@disabled">
				<xsl:attribute name="disabled">
					<xsl:text>disabled</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@toolTip">
				<xsl:attribute name="title">
					<xsl:value-of select="@toolTip"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="type">
				<xsl:choose>
					<xsl:when test="@client">
						<xsl:text>button</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>submit</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="@popup">
				<xsl:attribute name="aria-haspopup">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@msg">
				<xsl:attribute name="data-wc-btnmsg">
					<xsl:value-of select="@msg"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="@cancel">
					<xsl:attribute name="formnovalidate">
						<xsl:text>formnovalidate</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="@validates">
					<xsl:attribute name="data-wc-validate">
						<xsl:value-of select="@validates"/>
					</xsl:attribute>
				</xsl:when>
			</xsl:choose>
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
			<xsl:choose>
				<xsl:when test="@imageUrl">
					<xsl:variable name="alt">
						<xsl:choose>
							<xsl:when test="@imagePosition">
								<xsl:value-of select="''"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="text()"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<span>
						<xsl:attribute name="class">
							<xsl:choose>
								<xsl:when test="@imagePosition">
									<xsl:value-of select="concat('wc_btn_img wc_btn_img', @imagePosition)"/>
									<!-- no gap after 2nd `_img` -->
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>wc_nti</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:if test="@imagePosition">
							<span>
								<xsl:apply-templates/>
							</span>
						</xsl:if>
						<img alt="{$alt}" src="{@imageUrl}"/>
					</span>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</button>
	</xsl:template>

	<!-- 
		WPrintButton
	-->
	<xsl:template match="ui:printbutton">
		<button id="{@id}" name="{@id}" type="button">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="@type">
						<xsl:text>wc-linkbutton</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:if test="@disabled">
				<xsl:attribute name="disabled">
					<xsl:text>disabled</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@hidden">
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
			<xsl:choose>
				<xsl:when test="@imageUrl">
					<xsl:variable name="alt">
						<xsl:choose>
							<xsl:when test="@imagePosition">
								<xsl:value-of select="''"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="text()"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<span>
						<xsl:attribute name="class">
							<xsl:choose>
								<xsl:when test="@imagePosition">
									<xsl:value-of select="concat('wc_btn_img wc_btn_img', @imagePosition)"/>
									<!-- no gap after 2nd `_img` -->
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>wc_nti</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:if test="@imagePosition">
							<span>
								<xsl:apply-templates/>
							</span>
						</xsl:if>
						<img alt="{$alt}" src="{@imageUrl}"/>
					</span>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</button>
	</xsl:template>
</xsl:stylesheet>
