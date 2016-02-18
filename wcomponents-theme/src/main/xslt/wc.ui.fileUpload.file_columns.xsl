<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.fileUpload.file.n.fileInfo.xsl"/>
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.ui.fileUpload.file.xsl"/>
<!--
	File children of ui:fileupload. These are arranged in a list for nice semantic
	reasons. Each file has a checkbox to deselect it (for removal) and a label
	which gets its content from a call to the named template {{fileInfo}}. This
	named template is not totally necessary but makes themeing the list a little
	easier should you only wish to change the displayed file info in your
	implementation.
-->

	<xsl:template match="ui:file" mode="columns">
		<xsl:param name="rows"/>
		<ul class="wc_list_nb wc_filelist">
			<xsl:call-template name="fileInList"/>
			<xsl:apply-templates select="following-sibling::ui:file[position() &lt; $rows]"/>
		</ul>
	</xsl:template>
</xsl:stylesheet>
