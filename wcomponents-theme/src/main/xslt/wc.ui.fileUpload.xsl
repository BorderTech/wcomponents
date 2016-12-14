<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.ui.fileUpload.file.n.fileInput.xsl"/>
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.makeLegend.xsl"/>
	<!--
		Transform for WFileWidget and WMultiFileWidget. Should be a pretty simple HTML
		input element of type file. But it isn't.

		The template will output an unordered list of files if read only and files are
		present. When read only and no files are present we output a placeholder with
		an ID as a potential AJAX target.

		When not read only the element is a potential compound control and so gets
		wrapped in a container with the component ID. The compound control consists of
		the file input and the list of files.
	-->
	<xsl:template match="ui:fileupload">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="isError" select="key('errorKey',$id)"/>
		<xsl:variable name="readOnly">
			<xsl:choose>
				<xsl:when test="@readOnly">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="legacy">
			<xsl:choose>
				<xsl:when test="@async eq 'false'">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="myLabel" select="key('labelKey',$id)[1]"/>

		<xsl:variable name="cols">
			<xsl:choose>
				<xsl:when test="@cols">
					<xsl:number value="number(@cols)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="number($readOnly) eq 1 and number($legacy) eq 1">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="label" select="$myLabel"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="number($legacy) eq 1">
				<xsl:call-template name="fileInput">
					<xsl:with-param name="id" select="$id"/>
				</xsl:call-template>
				<xsl:call-template name="inlineError">
					<xsl:with-param name="errors" select="$isError"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="containerTag">
					<xsl:choose>
						<xsl:when test="number($readOnly) eq 1">
							<xsl:text>div</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>fieldset</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:element name="{$containerTag}">
					<xsl:call-template name="commonWrapperAttributes">
						<xsl:with-param name="isError" select="$isError"/>
						<xsl:with-param name="myLabel" select="$myLabel"/>
						<xsl:with-param name="class">
							<xsl:choose>
								<xsl:when test="number($readOnly) eq 1">
									<xsl:text>wc_ro</xsl:text>
								</xsl:when>
								<xsl:when test="@ajax">
									<xsl:text>wc-ajax</xsl:text>
								</xsl:when>
							</xsl:choose>
							<xsl:if test="number($readOnly) eq 1">
								<xsl:text>wc_ro</xsl:text>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:attribute name="data-wc-cols">
						<xsl:value-of select="$cols"/>
					</xsl:attribute>
					<xsl:choose>
						<xsl:when test="number($readOnly) eq 1">
							<xsl:call-template name="title"/>
							<xsl:call-template name="roComponentName"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="makeLegend">
								<xsl:with-param name="myLabel" select="$myLabel"/>
							</xsl:call-template>
							<xsl:variable name="inputId" select="concat($id,'_input')"/>
							<label class="wc-off" for="{$inputId}">
								<xsl:text>{{t 'file_inputLabel'}}</xsl:text>
							</label>
							<xsl:call-template name="fileInput">
								<xsl:with-param name="id" select="$inputId"/>
							</xsl:call-template>
							<xsl:call-template name="inlineError">
								<xsl:with-param name="errors" select="$isError"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:if test="ui:file">
						<xsl:variable name="numFiles" select="count(ui:file)"/>
						<xsl:choose>
							<xsl:when test="number($cols) gt 1 and number($cols) ge number($numFiles)">
								<div class="wc_files">
									<xsl:apply-templates select="ui:file[1]" mode="columns">
										<xsl:with-param name="rows" select="number($numFiles)"/>
									</xsl:apply-templates>
								</div>
							</xsl:when>
							<xsl:when test="number($cols) gt 1">
								<xsl:variable name="rows">
									<xsl:value-of select="ceiling(number($numFiles) div number($cols))"/>
								</xsl:variable>
								<div class="wc_files">
									<xsl:apply-templates select="ui:file[position() mod number($rows) eq 1]" mode="columns">
										<xsl:with-param name="rows" select="$rows"/>
									</xsl:apply-templates>
								</div>
							</xsl:when>
							<xsl:otherwise>
								<ul>
									<xsl:attribute name="class">
										<xsl:text>wc_list_nb wc_filelist</xsl:text>
										<xsl:if test="number($cols) eq 0">
											<xsl:text> wc-listlayout-type-flat</xsl:text>
										</xsl:if>
									</xsl:attribute>
									<xsl:apply-templates select="ui:file"/>
								</ul>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
