
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!--
		WLink and WInternalLink.
	-->
	<xsl:template match="ui:link">
		<xsl:param name="imageAltText" select="''"/>
		<xsl:param name="ajax" select="''"/>
		<!-- file in multi-file-upload -->
		<xsl:variable name="elementType">
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:text>button</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>a</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$elementType}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>wc-link</xsl:text>
				<xsl:if test="not(@type) and @imageUrl and @imagePosition">
					<xsl:text> wc_a_ilb</xsl:text>
				</xsl:if>
				<xsl:if test="@type">
					<xsl:value-of select="concat(' wc-link-type-', @type)"/>
				</xsl:if>
				<xsl:if test="@class">
					<xsl:value-of select="concat(' ', @class)"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test="@disabled">
				<xsl:choose>
					<xsl:when test="@type">
						<xsl:attribute name="disabled">
							<xsl:text>disabled</xsl:text>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="aria-disabled">
							<xsl:text>true</xsl:text>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
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
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:attribute name="type">
						<xsl:text>button</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="data-wc-url">
						<xsl:value-of select="@url"/>
					</xsl:attribute>
					<xsl:if test="ui:windowAttributes">
						<xsl:attribute name="data-wc-window">
							<xsl:value-of select="ui:windowAttributes/@name"/>
						</xsl:attribute>
						<xsl:attribute name="aria-haspopup">
							<xsl:text>true</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="href">
						<xsl:value-of select="@url"/>
					</xsl:attribute>
					<xsl:if test="ui:windowAttributes">
						<xsl:attribute name="target">
							<xsl:value-of select="ui:windowAttributes/@name"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$ajax != ''">
						<xsl:attribute name="data-wc-ajaxalias">
							<xsl:value-of select="$ajax"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@rel or ui:windowAttributes">
						<xsl:variable name="noopener" select="'noopener'"/>
						<xsl:variable name="noreferrer" select="'noreferrer'"/>
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
				</xsl:otherwise>
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
							<xsl:when test="$imageAltText ne ''">
								<xsl:value-of select="$imageAltText"/>
							</xsl:when>
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
		</xsl:element>
	</xsl:template>

	<!-- Window Attrributes applied to WLink -->
	<xsl:template match="ui:windowAttributes"/>
</xsl:stylesheet>
