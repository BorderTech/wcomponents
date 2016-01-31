<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
<!--

	Global XSLT templates, params and variables

	This file contains global variables and params for all XSLT. If a global variable or global param is used in more
	than one component it should be	here.

	Global Parameters

	Debug flag. This is a global parameter as it is pulled out of the compressed
	XSLT and we do not want it renamed.
-->
	<xsl:param name="isDebug" select="1"/>

<!--
	Global Variables
-->

<!--
	This variable is used as a shorthand for testing xs:boolean attribute values
	as it allows us to further compress the XSLT. We do not need a test for
	@*='false' because in almost all cases false results in the attribute not
	being output.
 -->
	<xsl:variable name="t" select="'true'"/>

<!--
	Keys for common root-down lookups

	Key for matching error messages to components in an error state. Pass in the
	component's id and key will provide a list of errors with matching for
	attribute
-->
	<xsl:key name="errorKey" match="//ui:error" use="@for"/>
<!--
	Key used to find a component's label. Pass in the component's id and key
	will provide a list of labels with matching for attribute
-->
	<xsl:key name="labelKey" match="//ui:label" use="@for"/>
<!--
	Key to find all ajax targets to determine if a control is an ajaxTarget so
	that it can be marked up with appropriate WAI-ARIA properties.
-->
	<xsl:key name="targetKey" match="//ui:ajaxTargetId" use="@targetId"/>

	<!--
		Generic utility templates

		These are templates for text nodes, unmatched elements and unmatched
		attributes. You will often see example templates like this:

			<xsl:template match="*|@*|node()">
				<xsl:copy>
					<xsl:apply-templates select="@*|node()/>
				</xsl:copy
			</xsl:template>

		Whereas we split these into separate templates. This is to prevent a fault becoming buggy if one of the more 
		advanced browsers ever implements XSLT 2 where a node with no children cannot apply templates. Try it in Saxon 9
		or later and look at the debug output. Using a single template like that above is also a performance issue, 
		making a copy of a text node is slower than outputting its value.

		There is also one more caveat with element nodes:

		Template for unmatched elements. Make a copy of the element. We make an element using local-name() rather than 
		the more obvious xsl:copy because of a need to work around a bug in IE if the element being copied has 
		namespace attributes.
	-->
	<xsl:template match="*">
		<xsl:element name="{local-name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<!--
		Unmatched attributes: make a copy of the attribute.
	-->
	<xsl:template match="@*">
		<xsl:copy />
	</xsl:template>

	<!--
		For text nodes we use value-of rather than apply-templates on text nodes as this provides improved performance. 
		This is actually redundant as it is the default rule but I have seen too many Variations on match node(), copy, 
		apply templates as in the above comment to leave this to chance!
	-->
	<xsl:template match="text()">
		<xsl:value-of select="."/>
	</xsl:template>
</xsl:stylesheet>
