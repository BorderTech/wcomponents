<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.ui.label.key.labelableElementKey.xsl"/>
	<!-- The error output in the message box. This must include a link to the component in an error state. -->
	<xsl:template match="ui:error">
		
		<xsl:variable name="for" select="@for"/>
		<xsl:variable name="labelableElement" select="key('labelableElementKey',$for)[1]"/>
		<xsl:variable name="targetId">
			<xsl:value-of select="$for"/>
			<xsl:if test="$for ne ''">
				<xsl:if test="local-name($labelableElement) eq 'datefield' or 
					local-name($labelableElement) eq 'textfield' or 
					local-name($labelableElement) eq 'emailfield' or 
					local-name($labelableElement) eq 'phonenumberfield'">
					<xsl:text>_input</xsl:text>
				</xsl:if>
			</xsl:if>
		</xsl:variable>
		<div class="wc-error">
			<a href="{concat('#',$targetId)}">
				<xsl:apply-templates/>
			</a>
		</div>
	</xsl:template>
	
	<!-- The inline error output for each ui:error for a component in a error state. -->
	<xsl:template match="ui:error" mode="inline">
		<span class="wc-error">
			<xsl:apply-templates/>
		</span>
	</xsl:template>
</xsl:stylesheet>
