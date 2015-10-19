<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
<!--
	####################################################################################################################
	# READ THIS IF YOU ARE EVEN THINKING ABOUT CHANGING THE XSLT
	####################################################################################################################

	WComponents inserts a DOCTYPE definition into the XML to allow lazy use of &nbsp; The upshot of this is that IE
	believes the XML is HTML and this has a flow on effect with some elements. Any HTML shorttag elements will cause a
	problem if the XSLT contains a <shorttag></shorttag> as IE (at least up to IE8) will output

		<shorttag></shorttag></shorttag><//shorttag/>.

	To mitigate against accidentally doing this we use <xsl:element> for element contruction and the build process XSLT
	compression includes a step to reducethe verbosity of this for elements which are not HTML self-closing.

	Example

		<input>
			<xsl:attribute...>
		</input>

		will be output by IE as
		<input attributes /></input/>

		So when you make any HTML element in XSLT please use
		<xsl:element name="elementname">
			<xsl:attribute...> etc
		</xsl:element>
		unless you are absolutely sure it is safe (preferably do not be lazy); then the XSLT compressor will make it
		into <elementname attribute=".." /> if it is safe to do so.

	####################################################################################################################
	# Default Namespace
	####################################################################################################################

	xmlns="http://www.w3.org/1999/xhtml"
	removed due to bug: https://bugzilla.mozilla.org/show_bug.cgi?id=631455

	NOTE: This line also causes MASSIVE bugs in IE, these bugs do not show symptoms very often in IE but when you hit
	the right combination then you have some very serious issues to deal with so basically NEVER put it back.

	Hmm ok this line also caused serious problems in Chrome. Caused line breaks to double up for example. Maybe we were
	wrong to ever put this in.

	####################################################################################################################
	# Applying templates
	####################################################################################################################

	If the schema for an element indicates that the element has only element children (for example ui:menu) then using
	<xsl:apply-templates /> to apply those templates should be safe enough. However, if the XML is indented or has any
	extraneous space then this space is a text node and will be copied through. This shouldn't matter in HTML, but
	sometimes, and in some browsers, it does. Therefore, if we are applying templates in an element which should not
	have non-element children we prefer to use <apply-templates select="*"/> to ensure we do not get these extraneous
	text nodes. They can play havoc with content wrapping in closely styled buttons (such as those in a BAR menu) in
	Chrome, for example.

	####################################################################################################################
	# Why online XSLT tutorials are dangerous
	####################################################################################################################

	If you ever use <xsl:for-each> without providing a detailed justification in triplicate and signed in blood we will
	hunt you down and mock you. Do not learn your XSLT chops online.

	<xsl:for-each> is incredibly slow. We have cleaned up other transforms where removing xsl:for-each and replacing
	them with apply-templates or apply-imports has resulted in transform times being reduced from hundreds of seconds to
	tens of milliseconds with one extreme case (admittedly in IE6) being reduced from over 14 minutes to around 600
	milliseconds.
		YES: 14 MINUTES to 600 MILLISECONDS BY REMOVING xsl:for-each

	####################################################################################################################
	# OK, NOW YOU CAN CARRY ON!
	####################################################################################################################

	Global XSLT templates, params and variables

	This file contains global variables and params for all XSLT. If a global variable or global param is used in more
	than one component it should be	here.

	Global Parameters

	Debug flag. This is a global parameter as it is pulled out of the compressed
	XSLT and we do not want it renamed.
-->
	<xsl:param name="isDebug" select="1"/>
<!--
	This is used to undertake browser specific transformations.
	Note - this is a param so it can be injected by the transform engine to
	enable server side transforms.
-->
	<xsl:param name="xslVendor" select="system-property('xsl:vendor')"/>

<!--
	Global Variables

	This variable is used as a short hand to determine if the xsl:vendor
	property is 'Microsoft' which implies Internet Explorer as browser.

	This is still required to provide a means to style some elements by position in IE8.

	TODO: drop this ASAP!
-->
	<xsl:variable name="isIE">
		<xsl:choose>
			<xsl:when test="$xslVendor='Microsoft'">
				<xsl:number value="1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:number value="0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
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

		Whereas we split these into separate templates.
		This is to prevent a fault becoming buggy if one of the more advanced
		browsers ever implements XSLT 2 where a node with no children cannot
		apply templates. Try it in Saxon 9 or later and look at the debug
		output. Using a single template like that above is also a performance
		issue, making a copy of a text node is slower than outputting its value.

		There is also one more caveat with element nodes:

		Template for unmatched elements. Make a copy of the element. We make an
		element using local-name() rather than the more obvious xsl:copy because
		of a need to work around a bug in IE if the element being copied has
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
		For ext nodes we use value-of rather than apply-templates on text nodes
		as this provides improved performance. This is actually redundant as it
		is the default rule but I have seen too many Variations on match node(),
		copy, apply templates as in the above comment to leave this to chance!
	-->
	<xsl:template match="text()">
		<xsl:value-of select="."/>
	</xsl:template>
</xsl:stylesheet>
