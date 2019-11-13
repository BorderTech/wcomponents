
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<xsl:template match="ui:fileupload[@readOnly]">
		<span id="{@id}" class="{normalize-space(concat('wc-fileupload wc-ro-input ', @class))}" data-wc-component="fileupload">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</span>
	</xsl:template>

	<xsl:template match="ui:fileupload">
		<span id="{@id}" class="{normalize-space(concat('wc-fileupload wc-input-wrapper ', @class))}">
			<xsl:if test="@disabled">
				<xsl:attribute name="aria-disabled">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:element name="input">
				<xsl:attribute name="id">
					<xsl:value-of select="concat(@id, '_input')"/>
				</xsl:attribute>
				<xsl:attribute name="type">
					<xsl:text>file</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
				<xsl:if test="@required">
					<xsl:attribute name="required">
						<xsl:text>required</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@accessibleText">
					<xsl:attribute name="aria-label">
						<xsl:value-of select="@accessibleText"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@buttonId">
					<xsl:attribute name="data-wc-submit">
						<xsl:value-of select="@buttonId"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="ui:fieldindicator">
					<xsl:if test="ui:fieldindicator[@id]">
						<xsl:attribute name="aria-describedby">
							<xsl:value-of select="ui:fieldindicator/@id" />
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="ui:fieldindicator[@type='error']">
						<xsl:attribute name="aria-invalid">
							<xsl:text>true</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
				<xsl:if test="@toolTip">
					<xsl:attribute name="title">
						<xsl:value-of select="@toolTip"/>
					</xsl:attribute>
				</xsl:if>
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
			</xsl:element>
			<xsl:apply-templates select="ui:fieldindicator"/>
		</span>
	</xsl:template>


	<xsl:template match="ui:multifileupload[@readOnly]">
		<xsl:variable name="roClass">
			<xsl:if test="@ajax">
				<xsl:text> wc-ajax</xsl:text>
			</xsl:if>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="not(ui:file)">
				<span id="{@id}" data-wc-component="multifileupload" class="{normalize-space(concat('wc-multifileupload ', @class, $roClass))}">
					<xsl:if test="@hidden">
						<xsl:attribute name="hidden">
							<xsl:text>hidden</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</span>
			</xsl:when>
			<xsl:when test="not(@cols) or number(@cols) le 1 or number(@cols) ge count(ui:file)">
				<ul id="{@id}" data-wc-component="multifileupload">
					<xsl:variable name="additional">
						<xsl:value-of select="$roClass"/>
						<xsl:choose>
							<xsl:when test="@cols = 0 or number(@cols) ge count(ui:file)">
								<xsl:text> wc-listlayout-type-flat wc-hgap-sm</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text> wc-vgap-sm</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:attribute name="class">
						<xsl:value-of select="normalize-space(concat('wc-multifileupload ', @class, $additional))"/>
					</xsl:attribute>

					<xsl:if test="@hidden">
						<xsl:attribute name="hidden">
							<xsl:text>hidden</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates/>
				</ul>
			</xsl:when>
			<xsl:otherwise>
				<div id="{@id}" data-wc-cols="{@cols}" data-wc-component="multifileupload">
					<xsl:attribute name="class">
						<xsl:value-of select="normalize-space(concat('wc-multifileupload ', @class, $roClass))"/>
					</xsl:attribute>
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
					<xsl:if test="@accessibleText">
						<xsl:attribute name="aria-label">
							<xsl:value-of select="@accessibleText"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="ui:fieldindicator">
						<xsl:if test="ui:fieldindicator[@id]">
							<xsl:attribute name="aria-describedby">
								<xsl:value-of select="ui:fieldindicator/@id" />
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="ui:fieldindicator[@type='error']">
							<xsl:attribute name="aria-invalid">
								<xsl:text>true</xsl:text>
							</xsl:attribute>
						</xsl:if>
					</xsl:if>
					<xsl:variable name="rows" select="ceiling(count(ui:file) div number(@cols))"/>
					<div class="wc_files wc-row wc-hgap-med wc-respond">
						<xsl:apply-templates mode="columns" select="ui:file[position() mod number($rows) eq 1]">
							<xsl:with-param name="rows" select="$rows"/>
						</xsl:apply-templates>
					</div>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="ui:multifileupload">
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
		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:if test="@required">
				<xsl:text> wc_req</xsl:text>
			</xsl:if>
			<xsl:if test="@ajax">
				<xsl:text> wc-ajax</xsl:text>
			</xsl:if>
		</xsl:variable>
		<fieldset id="{@id}" data-wc-cols="{$cols}" class="{normalize-space(concat('wc-multifileupload wc_noborder ', $additional))}">
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
			<xsl:if test="@accessibleText">
				<xsl:attribute name="aria-label">
					<xsl:value-of select="@accessibleText"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="ui:fieldindicator">
				<xsl:if test="ui:fieldindicator[@id]">
					<xsl:attribute name="aria-describedby">
						<xsl:value-of select="ui:fieldindicator/@id" />
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="ui:fieldindicator[@type='error']">
					<xsl:attribute name="aria-invalid">
						<xsl:text>true</xsl:text>
					</xsl:attribute>
				</xsl:if>
			</xsl:if>
			<label class="wc-off" for="{concat(@id,'_input')}">
				<xsl:text>{{#i18n}}file_inputLabel{{/i18n}}</xsl:text>
			</label>
			<xsl:element name="input">
				<xsl:attribute name="id">
					<xsl:value-of select="concat(@id, '_input')"/>
				</xsl:attribute>
				<xsl:attribute name="type">
					<xsl:text>file</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
				<xsl:if test="@toolTip">
					<xsl:attribute name="title">
						<xsl:value-of select="@toolTip"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@accessibleText">
					<xsl:attribute name="aria-label">
						<xsl:value-of select="@accessibleText"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@buttonId">
					<xsl:attribute name="data-wc-submit">
						<xsl:value-of select="@buttonId"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="ui:fieldindicator">
					<xsl:if test="ui:fieldindicator[@id]">
						<xsl:attribute name="aria-describedby">
							<xsl:value-of select="ui:fieldindicator/@id" />
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="ui:fieldindicator[@type='error']">
						<xsl:attribute name="aria-invalid">
							<xsl:text>true</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
				<xsl:if test="@submitOnChange">
					<xsl:attribute name="class">
						<xsl:text>wc_soc</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:attribute name="multiple">
					<xsl:text>multiple</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="data-dropzone">
					<xsl:choose>
						<xsl:when test="@dropzone">
							<xsl:value-of select="@dropzone"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@id"/>
							<!-- If there is no dropzone we may as well default to the file widget container -->
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
				<button class="wc_btn_camera wc_btn_icon wc-invite" data-wc-editor="{@editor}" data-wc-selector="{@id}" type="button">
					<span class="wc-off">Camera<!-- TODO i18n --></span>
					<i aria-hidden="true" class="fa fa-video-camera"/>
				</button>
			</xsl:if>
			<xsl:if test="ui:file">
				<xsl:choose>
					<xsl:when test="number($cols) gt 1">
						<div class="wc_files wc-row wc-hgap-med wc-respond wc-margin-n-sm">
							<xsl:variable name="numFiles" select="count(ui:file)"/>
							<xsl:choose>
								<xsl:when test="number($cols) ge number($numFiles)">
									<xsl:apply-templates mode="columns" select="ui:file[1]">
										<xsl:with-param name="rows" select="number($numFiles)"/>
									</xsl:apply-templates>
								</xsl:when>
								<xsl:otherwise>
									<xsl:variable name="rows" select="ceiling(number($numFiles) div number($cols))"/>
									<xsl:apply-templates mode="columns" select="ui:file[position() mod number($rows) eq 1]">
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
			<xsl:apply-templates select="ui:fieldindicator"/>
		</fieldset>
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
		<li data-wc-containerid="{../@id}" id="{@id}" class="wc-file">
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
					<xsl:value-of select="concat(@name, ' (', @size, ') ')"/>
					<!-- a space so it reads "N bytes" instead of "Nbytes" -->
					<xsl:text>{{#i18n}}file_size_{{/i18n}}</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="not(../@readOnly)">
				<button class="wc_btn_icon wc-invite" title="{concat('Delete attachment: ', @name)}" type="button">
					<i aria-hidden="true" class="fa fa-trash"/>
				</button>
			</xsl:if>
		</li>
	</xsl:template>

</xsl:stylesheet>
