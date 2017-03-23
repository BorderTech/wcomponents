<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.imageEditButton.xsl"/>
	<!--
		Transform for WMultiFileWidget.
	-->	
	<xsl:template match="ui:multifileupload">
		<xsl:variable name="roClass">
			<xsl:text>wc_ro</xsl:text>
			<xsl:if test="@ajax">
				<xsl:text> wc-ajax</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="@readOnly and not(ui:file)">
				<xsl:call-template name="readOnlyControl"/>
			</xsl:when>
			<xsl:when test="@readOnly and (not(@cols) or number(@cols ) le 1 or number(@cols) ge count(ui:file))">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="isList" select="1"/>
					<xsl:with-param name="class">
						<xsl:value-of select="$roClass"/>
						<xsl:choose>
							<xsl:when test="@cols = 0 or number(@cols) ge count(ui:file)">
								<xsl:text> wc-listlayout-type-flat wc-hgap-sm</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text> wc-vgap-sm</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@readOnly">
				<div data-wc-cols="{@cols}">
					<xsl:call-template name="commonWrapperAttributes">
						<xsl:with-param name="class" select="$roClass"/>
					</xsl:call-template>
					<xsl:call-template name="roComponentName"/>
					<xsl:variable name="rows" select="ceiling(count(ui:file) div number(@cols))"/>
					<div class="wc_files wc-row wc-hgap-med wc-respond">
						<xsl:apply-templates select="ui:file[position() mod number($rows) eq 1]" mode="columns">
							<xsl:with-param name="rows" select="$rows"/>
						</xsl:apply-templates>
					</div>
				</div>
			</xsl:when>
			<xsl:otherwise>
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
				<fieldset data-wc-cols="{$cols}">
					<xsl:call-template name="commonWrapperAttributes">
						<xsl:with-param name="class">
							<xsl:if test="@ajax">
								<xsl:text>wc-ajax</xsl:text>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
					<label class="wc-off" for="{concat(@id,'_input')}">
						<xsl:text>{{t 'file_inputLabel'}}</xsl:text>
					</label>
					<xsl:element name="input">
						<xsl:call-template name="wrappedInputAttributes">
							<xsl:with-param name="type">
								<xsl:text>file</xsl:text>
							</xsl:with-param>
						</xsl:call-template>
						<xsl:attribute name="multiple">
							<xsl:text>multiple</xsl:text>
						</xsl:attribute>
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
						<xsl:if test="@editor">
							<xsl:attribute name="data-wc-editor">
								<xsl:value-of select="@editor"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="@maxFiles">
							<xsl:attribute name="data-wc-maxfiles">
								<xsl:value-of select="@maxFiles"/>
							</xsl:attribute>
						</xsl:if>
					</xsl:element>
					<xsl:if test="@camera">
						<xsl:call-template name="imageEditButton">
							<xsl:with-param name="text">
								<xsl:text>Camera</xsl:text><!-- TODO i18n -->
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="ui:file">
						<xsl:choose>
							<xsl:when test="number($cols) gt 1">
								<div class="wc_files wc-row wc-hgap-med wc-respond wc-margin-n-sm">
									<xsl:variable name="numFiles" select="count(ui:file)"/>
									<xsl:choose>
										<xsl:when test="number($cols) ge number($numFiles)">
											<xsl:apply-templates select="ui:file[1]" mode="columns">
												<xsl:with-param name="rows" select="number($numFiles)"/>
											</xsl:apply-templates>
										</xsl:when>
										<xsl:otherwise>
											<xsl:variable name="rows" select="ceiling(number($numFiles) div number($cols))"/>
											<xsl:apply-templates select="ui:file[position() mod number($rows) eq 1]" mode="columns">
												<xsl:with-param name="rows" select="$rows"/>
											</xsl:apply-templates>
										</xsl:otherwise>
									</xsl:choose>
								</div>
							</xsl:when>
							<xsl:otherwise>
								<ul>
									<xsl:attribute name="class">
										<xsl:text>wc_list_nb wc_filelist wc-margin-n-sm</xsl:text>
										<xsl:choose>
											<xsl:when test="@cols = 0">
												<xsl:text> wc-listlayout-type-flat</xsl:text>
											</xsl:when>
											<xsl:otherwise>
												<xsl:text> wc-vgap-sm</xsl:text>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:attribute>
									<xsl:apply-templates select="ui:file"/>
								</ul>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
				</fieldset>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		File children of ui:multifileupload. These are arranged in a list for nice semantic
		reasons. Each file has a checkbox to deselect it (for removal) and a label
		which gets its content from a call to the named template `fileInfo`. This
		named template is not totally necessary but makes themeing the list a little
		easier should you only wish to change the displayed file info in your
		implementation.
	-->

	<xsl:template match="ui:file">
		<xsl:call-template name="fileInList"/>
	</xsl:template>

	<xsl:template match="ui:file" mode="columns">
		<xsl:param name="rows" select="0"/>
		<ul class="wc_list_nb wc_filelist wc-column wc-vgap-small">
			<xsl:call-template name="fileInList"/>
			<xsl:apply-templates select="following-sibling::ui:file[position() lt number($rows)]"/>
		</ul>
	</xsl:template>

	<xsl:template name="fileInList">
		<!-- Note that when part of an AJAX upload this ID is generated by the client and sent to the server as wc_fileid -->
		<li id="{@id}" data-wc-containerid="{../@id}">
			<xsl:call-template name="makeCommonClass"/><!-- This helps the widget identify file items -->
			<xsl:choose>
				<xsl:when test="ui:link">
					<xsl:apply-templates select="ui:link">
						<xsl:with-param name="imageAltText" select="concat('Thumbnail for uploaded file: ', @name)"/>
						<!-- The following is only needed when writing a multifileupload with files in a readonly state, never if the file is in an 
							ajax esponse by itself (as that is not possible in a readonly state) -->
						<xsl:with-param name="ajax">
							<xsl:if test="parent::ui:multifileupload[@ajax] and ../@readOnly">
								<xsl:value-of select="../@id"/>
							</xsl:if>
						</xsl:with-param>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<!-- This case should not happen because it is lame -->
					<xsl:value-of select="concat(@name,' (',@size,') ')"/><!-- a space so it reads "N bytes" instead of "Nbytes" -->
					<xsl:text>{{t 'file_size_'}}</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="not(../@readOnly)">
				<button type="button" class="wc_btn_icon wc-invite" title="{concat('Delete attachment: ', @name)}">
					<xsl:call-template name="icon">
						<xsl:with-param name="class">fa-trash</xsl:with-param>
					</xsl:call-template>
				</button>
			</xsl:if>
		</li>
	</xsl:template>
</xsl:stylesheet>
