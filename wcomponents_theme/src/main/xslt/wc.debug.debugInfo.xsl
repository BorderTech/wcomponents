<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.constants.xsl"/>

	<xsl:key name="debugInfo" match="//ui:debugInfo" use="@for"/>

	<xsl:template match="ui:debug">
		<xsl:if test="ancestor::ui:root">
			<xsl:element name="${wc.dom.html5.element.aside}">
				<xsl:attribute name="id">
					<xsl:text>wcdebugcontainer</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="role">
					<xsl:text>dialog</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="class">
					<xsl:text>wc_dragflow wc_resizeflow</xsl:text>
				</xsl:attribute>
				<xsl:call-template name="hiddenElement"/>
				<xsl:element name="${wc.dom.html5.element.header}">
					<xsl:attribute name="tabindex">
						<xsl:text>0</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="data-wc-draggable">
						<xsl:copy-of select="$t"/>
					</xsl:attribute>
					<xsl:attribute name="data-wc-dragfor">
						<xsl:text>wcdebugcontainer</xsl:text>
					</xsl:attribute>
					<xsl:element name="h1">
						<xsl:text>Debug Information</xsl:text>
					</xsl:element>
					<span>
						<button type="button" class="wc_maxcont wc_btn_nada" aria-pressed="false">
							<xsl:attribute name="title">
								<xsl:value-of select="$$${wc.ui.dialog.title.maxRestore}"/>
							</xsl:attribute>
							<xsl:attribute name="data-wc-resize">
								<xsl:text>wcdebugcontainer</xsl:text>
							</xsl:attribute>
							<xsl:text>&#x2002;</xsl:text>
						</button>
						<button type="button" class="wc_dialog_close wc_btn_nada">
							<xsl:attribute name="title">
								<xsl:value-of select="$$${wc.ui.dialog.title.close}"/>
							</xsl:attribute>
							<xsl:text>&#x2002;</xsl:text>
						</button>
					</span>
				</xsl:element>
				<div aria-live="assertive">
					<!--breadcrumbs-->
					<ul class="wc_db_crumbs">&#x2002;</ul>
					<h2>WComponent details</h2>
					<div class="wc_db_infobox">&#x2002;</div>
					<!-- debugable 'children' -->
					<h2>Next component(s)</h2>
					<ul class="wc_db_children feature">&#x2002;</ul>
				</div>
				<xsl:element name="${wc.dom.html5.element.footer}">
					<button type="button" class="wc_btn_nada wc_resize">
						<xsl:attribute name="data-wc-resize">
							<xsl:text>wcdebugcontainer</xsl:text>
						</xsl:attribute>
						<xsl:text>&#x2002;</xsl:text>
					</button>
				</xsl:element>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="ui:debugInfo">
		<xsl:variable name="for" select="@for"/>
		<xsl:text>{"id":"</xsl:text>
		<xsl:value-of select="concat($for,'${wc.debug.debugInfo.id.suffix}',generate-id())"/>
		<xsl:text>","javaclass":"</xsl:text>
		<xsl:value-of select="@class"/>
		<xsl:text>","javaId":"</xsl:text>
		<xsl:value-of select="$for"/>
		<xsl:text>","detail":[</xsl:text>
		<xsl:apply-templates/>
		<xsl:text>]</xsl:text>
		<xsl:if test="@type">
			<xsl:text>,"javatype":"</xsl:text>
			<xsl:value-of select="@type"/>
			<xsl:text>"</xsl:text>
		</xsl:if>
		<xsl:text>}</xsl:text>
		<xsl:if test="position()!=last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ui:debugDetail">
		<xsl:text>{"key":"</xsl:text>
		<xsl:value-of select="@key"/>
		<xsl:text>","value":"</xsl:text>
		<xsl:value-of select="@value"/>
		<xsl:text>"}</xsl:text>
		<xsl:if test="position()!=last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ui:debugInfo" mode="inline">
		<xsl:value-of select="concat(@for,'${wc.debug.debugInfo.id.suffix}',generate-id())"/>
		<xsl:if test="position()!=last()">
			<xsl:value-of select="' '"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ui:debugInfo" mode="getType">
		<xsl:attribute name="${wc.debug.debugInfo.attrib.debugType}">
			<xsl:value-of select="@type"/>
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="ui:debugInfo" mode="getClass">
		<xsl:attribute name="${wc.debug.debugInfo.attrib.debugClass}">
			<xsl:value-of select="@class"/>
		</xsl:attribute>
	</xsl:template>

	<xsl:template name="debugAttributes">
		<xsl:param name="id" select="@id"/>
		<xsl:variable name="hasDebugInfo" select="key('debugInfo',$id)"/>
		<xsl:if test="$hasDebugInfo">
			<xsl:attribute name="${wc.debug.debugInfo.attrib.hasDebugInfo}">
				<xsl:apply-templates select="$hasDebugInfo" mode="inline"/>
			</xsl:attribute>
			<xsl:apply-templates select="$hasDebugInfo[1]" mode="getClass"/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="getDebugInfoId">
		<xsl:value-of select="concat(@for,'${wc.debug.debugInfo.id.suffix}',generate-id())"/>
	</xsl:template>
	
	<!--
		Helper template to output an attribute with a given name and text content.
		param name: the attribute name one of:
		* data-wc-debugwarn
		* data-wc-debugerr
		* ata-wc-debuginfo
		* data-wc-dubugdebug
		param text: the text to show in the attribute.
		
		NOTE: you can add line breaks (<br/>) in the final debugInfo HTML output using "\n" in the attribute.
	-->
	<xsl:template name="makeDebugAttrib-debug">
		<xsl:param name="name"/>
		<xsl:param name="text"/>
		<xsl:attribute name="{$name}">
			<xsl:value-of select="$text"/>
		</xsl:attribute>
	</xsl:template>


	<!--
		This is a debug helper template which is used to get the id of any component
		 which has to be listed in one of the warnings.
	-->
	<xsl:template match="*" mode="listById">
		<xsl:choose>
			<xsl:when test="@id">
				<xsl:value-of select="@id"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="local-name(.)"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>