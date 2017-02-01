<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!-- 
		WLink and WInternalLink. 
	-->
	<xsl:template match="ui:link">
		<xsl:param name="imageAltText" select="''"/>
		<xsl:param name="ajax" select="''"/>
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
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="isControl">
					<xsl:choose>
						<xsl:when test="@type">
							<xsl:number value="1"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:number value="0"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="title"/>
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
					<xsl:if test="ui:windowAttributes">
						<xsl:attribute name="target">
							<xsl:value-of select="ui:windowAttributes/@name"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="accessKey"/>
			<xsl:choose>
				<xsl:when test="@imageUrl">
					<xsl:if test="@imagePosition">
						<span>
							<xsl:apply-templates />
						</span>
					</xsl:if>
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
					<img src="{@url}" alt="{$alt}" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	
	<!-- Window Attrributes applied to WLink -->
	<xsl:template match="ui:windowAttributes"/>
</xsl:stylesheet>
