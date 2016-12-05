<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.aria.live.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
<!--
	Ajax Helpers
	
	Helper templates and keys for common ajax markup. These templates are accessibility
	helpers and the accessibility forms the basis for the functionality.
	
	DO NOT CHANGE THESE TEMPLATES unless you *really* know what you are doing!
	
	Key to find all ajaxTriggers to determine if a control is an ajaxTrigger so that
	it can be marked up with appropriate WAI-ARIA properties.
-->
	<xsl:key name="triggerKey" match="//ui:ajaxtrigger" use="@triggerId"/>
	
<!--
	Creates an aria-controls attribute for any element which is an AJAX trigger.
	(see wc.ui.ajaxTrigger.xsl).
	
	param id default @id: the id of the component which outputs the element to
	which we are applying the attribute. 
	
-->
	<xsl:template name="ajaxController">
		<xsl:param name="id" select="@id"/>
		<xsl:variable name="trigger" select="key('triggerKey',$id)"/>
		<xsl:if test="$trigger">
			<xsl:variable name="idList">
				<xsl:apply-templates select="$trigger" mode="controlled"/>
			</xsl:variable>
			<xsl:if test="$idList ne ''">
				<xsl:attribute name="aria-controls">
					<xsl:value-of select="normalize-space($idList)"/>
				</xsl:attribute>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	
<!--
	Creates an aria-live attribute for any element which is an AJAX target.

	param id default @id: the id of the component which outputs the element to
	which we are applying the attribute.
-->
	<xsl:template name="ajaxTarget">
		<xsl:param name="id" select="@id"/>
		<xsl:param name="live" select="'polite'"/>
		<xsl:if test="key('targetKey',$id) or parent::ui:ajaxtarget[@action eq 'replace']">
			<xsl:call-template name="setARIALive">
				<xsl:with-param name="live" select="$live"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
