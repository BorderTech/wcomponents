<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.aria.live.xsl" />
	<xsl:import href="wc.common.hide.xsl"/>

	<xsl:template match="ui:session[count(.|((//ui:session)[1])) = 1]">
		<div id="wc_session_container" class="wc_session" role="alert" hidden="hiddden">
			<xsl:call-template name="setARIALive"/>
		</div>
	</xsl:template>
	<xsl:template match="ui:session">
		<xsl:comment>Ignoring repeats of ui:session.</xsl:comment>
	</xsl:template>
</xsl:stylesheet>
