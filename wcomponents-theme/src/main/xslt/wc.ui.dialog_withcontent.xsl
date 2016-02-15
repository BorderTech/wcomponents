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
				<xsl:text> wc_dragflow wc_resizeflow</xsl:text>
			</xsl:attribute>
			<xsl:element name="${wc.dom.html5.element.header}">
				<xsl:attribute name="tabindex">
					<xsl:text>0</xsl:text>
				</xsl:attribute>
				<span>
					<button class="wc_maxcont wc_btn_nada" type="button" title="{$$${wc.ui.dialog.title.maxRestore}}" aria-pressed="false" data-wc-resize="${wc.ui.dialog.id}">
						<xsl:text>&#xa0;</xsl:text>
					</button>
					<button class="wc_dialog_close wc_btn_nada" type="button" title="{$$${wc.ui.dialog.title.close}}">
						<xsl:text>&#xa0;</xsl:text>
					</button>
				</span>
				<h1>
					<xsl:choose>
						<xsl:when test="@title">
							<xsl:value-of select="@title"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$$${wc.ui.dialog.title.noTitle}"/>
						</xsl:otherwise>
					</xsl:choose>
				</h1>
			</xsl:element>
			<div aria-live="assertive" id="{@id}">
				<xsl:apply-templates select="ui:content"/>
			</div>
			<xsl:element name="${wc.dom.html5.element.footer}">
				<button class="wc_resize wc_btn_nada" data-wc-resize="${wc.ui.dialog.id}" type="button">
					<xsl:text>&#xa0;</xsl:text>
				</button>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
