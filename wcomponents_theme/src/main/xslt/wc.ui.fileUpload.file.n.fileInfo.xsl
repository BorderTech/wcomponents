<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
<!--
	Text information about a file. This named template was split out of the
	transform for ui:file to make implementation changes simpler.
	
	<<TODO:>> Should this build a link to the file?
-->
	<xsl:template name="fileInfo">
		<xsl:value-of select="concat(@name,' (',@size,' ',') ',$$${wc.ui.multiFileUploader.i18n.fileDesc.size})"/>
		<!-- a space so it reads "N bytes" instead of "Nbytes" -->
	</xsl:template>
</xsl:stylesheet>
