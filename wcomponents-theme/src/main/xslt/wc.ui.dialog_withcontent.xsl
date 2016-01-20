<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<xsl:template match="ui:dialog" mode="withcontent">
		<xsl:element name="${wc.dom.html5.element.dialog}">
			<xsl:attribute name="id">
				<xsl:text>${wc.ui.dialog.id}</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:call-template name="commonClassHelper"/>
				<xsl:text>wc_dragflow wc_resizeflow</xsl:text>
			</xsl:attribute>
			<xsl:element name="${wc.dom.html5.element.header}">
				<xsl:attribute name="tabindex">
					<xsl:text>0</xsl:text>
				</xsl:attribute>
				<xsl:element name="span">
					<xsl:element name="button">
						<xsl:attribute name="class">
							<xsl:text>wc_maxcont wc_btn_nada</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="type">
							<xsl:text>button</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:value-of select="$$${wc.ui.dialog.title.maxRestore}"/>
						</xsl:attribute>
						<xsl:attribute name="aria-pressed">
							<xsl:text>false</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="data-wc-resize">
							<xsl:text>${wc.ui.dialog.id}</xsl:text>
						</xsl:attribute>
						<xsl:text>&#xa0;</xsl:text>
					</xsl:element>
					<xsl:element name="button">
						<xsl:attribute name="class">
							<xsl:text>wc_dialog_close wc_btn_nada</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="type">
							<xsl:text>button</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:value-of select="$$${wc.ui.dialog.title.close}"/>
						</xsl:attribute>
						<xsl:text>&#xa0;</xsl:text>
					</xsl:element>
				</xsl:element>
				<xsl:element name="h1">
					<xsl:choose>
						<xsl:when test="@title">
							<xsl:value-of select="@title"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$$${wc.ui.dialog.title.noTitle}"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:element>
			<xsl:element name="div">
				<xsl:attribute name="aria-live">
					<xsl:text>assertive</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="id">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
				<xsl:apply-templates select="ui:content"/>
			</xsl:element>
			<xsl:element name="${wc.dom.html5.element.footer}">
				<xsl:element name="button">
					<xsl:attribute name="class">
						<xsl:text>wc_resize wc_btn_nada</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="data-wc-resize">
						<xsl:text>${wc.ui.dialog.id}</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="type">
						<xsl:text>button</xsl:text>
					</xsl:attribute>
					<xsl:text>&#xa0;</xsl:text>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
