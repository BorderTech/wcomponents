<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.registrationScripts.coreRegistrationScripts.xsl"/>
	<xsl:import href="wc.common.registrationScripts.requiredLibraries.xsl"/>
	<!-- Common bootstrapping scripts -->
	<xsl:template name="registrationScripts">
		<xsl:variable name="rego">
			<xsl:call-template name="coreRegistrationScripts"/>
			<xsl:call-template name="requiredLibraries"/>
		</xsl:variable>
		<xsl:if test="$rego ne ''">
			<xsl:choose>
				<xsl:when test="self::ui:root">
					<xsl:value-of select="$rego"/>
				</xsl:when>
				<xsl:otherwise>
					<script type="text/javascript" class="registrationScripts">
						<xsl:text>require(["wc/compat/compat!"], function(){</xsl:text>
						<xsl:text>require(["wc/common"], function(){</xsl:text>
						<xsl:value-of select="$rego"/>
						<xsl:text>});});</xsl:text>
					</script>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
