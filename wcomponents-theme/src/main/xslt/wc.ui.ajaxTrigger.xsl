<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		The ui:ajaxtrigger element is used to register components which make AJAX requests in JavaScript.

		Each UI control which is an ajax trigger is marked up with a WAI-ARIA property
		(aria-controls) which consists of a space separated list of the components which
		are targets of the trigger.
		
		When a trigger is invoked these targets are placed in a busy state (aria-busy="true")
		to indicate to users that they are in a transitory state. In addition, where
		possible the content of the controls are made invisible and a loading indicator
		is placed within the control.
		
		Child Elements: ui:ajaxtargetid (minOccurs="1" maxOccurs="unbounded")
			
		NOTE:
		these templates remain in a single file because they are all javascript
		related and you should not have to change any of them.
	-->

	<!-- No inline output -->
	<xsl:template match="ui:ajaxtrigger"/>

	<!--
		This creates the JSON objects required to register the triggers (see
		wc.common.registrationScripts.xsl).
		TODO: determine if the "loads" property is made redundant by the aria-controls attribute and can be removed.
	-->
	<xsl:template match="ui:ajaxtrigger" mode="JS">
		<xsl:text>{"oneShot":</xsl:text>
		<xsl:choose>
			<xsl:when test="not(@allowedUses)">
				<xsl:text>false</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$t"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>,"loads":[</xsl:text>
		<xsl:apply-templates select="*"/>
		<xsl:text>],"id":"</xsl:text>
		<xsl:value-of select="@triggerId"/>
		<xsl:text>","alias":"</xsl:text>
		<xsl:value-of select="@triggerId"/>
		<xsl:text>"}</xsl:text>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>

	<!--
		This creates the function used to make a delayed call to an ajaxTrigger on page
 		load which is used, for example, for polling slow applications (see
		wc.common.registrationScripts.xsl).
	-->
	<xsl:template match="ui:ajaxtrigger" mode="JSdelay">
		<xsl:text>{"id":"</xsl:text>
		<xsl:value-of select="@triggerId"/>
		<xsl:text>","delay":</xsl:text>
		<xsl:value-of select="@delay"/>
		<xsl:text>}</xsl:text>
		<xsl:if test="position()!=last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- This template will apply-templates to generate a list of ids of controlled elements.-->
	<xsl:template match="ui:ajaxtrigger" mode="controlled">
		<xsl:apply-templates mode="controlled"/>
	</xsl:template>
</xsl:stylesheet>
