<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.fileUpload.file.n.fileInfo.xsl"/>
	<xsl:import href="wc.ui.imageEdit.xsl"/>
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.title.xsl"/>
	
	<xsl:template name="fileInput">
		<xsl:param name="id"/>
		<xsl:variable name="maxFiles" select="@maxFiles"/>
		<xsl:element name="input">
			<xsl:attribute name="type">
				<xsl:text>file</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="id">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:call-template name="ajaxTarget">
				<xsl:with-param name="live" select="'off'"/>
			</xsl:call-template>
			<xsl:attribute name="data-dropzone">
				<xsl:choose>
					<xsl:when test="@dropzone">
						<xsl:value-of select="@dropzone"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@id"/><!-- If there is no dropzone we may as well default to the file widget container -->
					</xsl:otherwise>					
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="@editor">
				<xsl:attribute name="data-editor">
					<xsl:value-of select="@editor"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$maxFiles='1'">
					<xsl:call-template name="requiredElement"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="multiple">
						<xsl:text>multiple</xsl:text>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="disabledElement">
				<xsl:with-param name="isControl" select="1"/>
			</xsl:call-template>
			<xsl:call-template name="title"/>
			<xsl:if test="@acceptedMimeTypes">
				<xsl:attribute name="accept">
					<xsl:value-of select="@acceptedMimeTypes"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@maxFileSize">
				<xsl:attribute name="data-wc-maxfilesize">
					<xsl:value-of select="@maxFileSize"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$maxFiles">
				<xsl:attribute name="data-wc-maxfiles">
					<xsl:value-of select="$maxFiles"/>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>
		<xsl:if test="@camera=$t">
			<xsl:call-template name="imageEditButton">
				<xsl:with-param name="text">
					<xsl:text>Camera</xsl:text><!-- TODO i18n -->
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
