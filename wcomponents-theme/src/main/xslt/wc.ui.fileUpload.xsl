<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
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
			<xsl:if test="@readOnly">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="legacy">
			<xsl:if test="@async='false'">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="maxFiles" select="@maxFiles"/>
		<xsl:variable name="myLabel" select="key('labelKey',$id)[1]"/>

		<xsl:variable name="cols">
			<xsl:choose>
				<xsl:when test="@cols">
					<xsl:value-of select="@cols"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$readOnly=1 and $legacy=1">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="label" select="$myLabel"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$legacy=1">
				<xsl:call-template name="fileInput">
					<xsl:with-param name="id" select="$id"/>
				</xsl:call-template>
				<xsl:call-template name="inlineError">
					<xsl:with-param name="errors" select="$isError"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="containerTag">
					<xsl:if test="$readOnly!=1">
						<xsl:text>fieldset</xsl:text>
					</xsl:if>
					<xsl:if test="$readOnly=1">
						<xsl:text>div</xsl:text>
					</xsl:if>
				</xsl:variable>
				<xsl:element name="{$containerTag}">
					<xsl:call-template name="commonWrapperAttributes">
						<xsl:with-param name="isError" select="$isError"/>
						<xsl:with-param name="myLabel" select="$myLabel"/>
					</xsl:call-template>
					<xsl:if test="@ajax=$t">
						<xsl:attribute name="data-wc-ajaxalias">
							<xsl:value-of select="@id"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="data-wc-cols">
						<xsl:value-of select="$cols"/>
					</xsl:attribute>
					<xsl:if test="$readOnly!=1">
						<xsl:call-template name="makeLegend">
							<xsl:with-param name="myLabel" select="$myLabel"/>
						</xsl:call-template>

						<xsl:variable name="inputId" select="concat($id,generate-id())"/>
						<label class="wc_off" for="{$inputId}">
							<xsl:value-of select="$$${wc.ui.multiFileUploader.i18n.inputLabel}"/>
						</label>
						<xsl:call-template name="fileInput">
							<xsl:with-param name="id" select="$inputId"/>
						</xsl:call-template>
						<xsl:call-template name="inlineError">
							<xsl:with-param name="errors" select="$isError"/>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="ui:file">
						<xsl:variable name="numFiles" select="count(ui:file)"/>
						<xsl:choose>
							<xsl:when test="$cols &gt; 1 and $cols &gt;= $numFiles">
								<div class="wc_files">
									<xsl:apply-templates select="ui:file[1]" mode="columns">
										<xsl:with-param name="rows" select="$numFiles"/>
									</xsl:apply-templates>
								</div>
							</xsl:when>
							<xsl:when test="$cols &gt; 1">
								<xsl:variable name="rows">
									<xsl:value-of select="ceiling($numFiles div $cols)"/>
								</xsl:variable>
								<div class="wc_files">
									<xsl:apply-templates select="ui:file[position() mod $rows = 1]" mode="columns">
										<xsl:with-param name="rows" select="$rows"/>
									</xsl:apply-templates>
								</div>
							</xsl:when>
							<xsl:otherwise>
								<ul>
									<xsl:attribute name="class">
										<xsl:text>wc_filelist wc_list_nb</xsl:text>
										<xsl:if test="$cols = 0">
											<xsl:text> wc_list_flat</xsl:text>
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
