<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		HTML Conformance

		These templates work around issues in a well known user agent and to provide pass throughs of HTML elements
		mixed in with the WComponents XML.

		NOTE:
		This is a rather large amount of XSLT to undertake a seemingly small job. If your application development
		environment is tightly controlled you could easily exclude this file in your implementation.
	-->

	<!--
		Elements to be hoisted to the HEAD element:

		Remove link, base and meta elements completely from flow. These are all hoisted
		into the HTML head element in ui:root.
	-->
	<xsl:template match="html:link|html:base|html:meta"/>

	<xsl:template match="html:link[ancestor::ui:ajaxTarget]">
		<xsl:copy-of select="."/>
	</xsl:template>
	<!--
		Copy link, base and meta elements in the head.
	-->
	<xsl:template match="html:link|html:base|html:meta" mode="inHead">
		<xsl:variable name="el" select="local-name()"/>
		<!-- copy without XML namespaces -->
		<xsl:element name="{$el}">
			<xsl:apply-templates select="@*"/>
		</xsl:element>
	</xsl:template>

	<!--
		HTML 'shorttag' elements

		Some elements cause problems in IE when copied from a source XML document to the destination tree. This template
		matches HTML self-closing elements and is required to work around a bug in The Microsoft CSLTProcieesor (at
		least up to IE9, posibly later) which will create a closing element if we use the regular copy method. For
		example with an input element if using:
		<xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy>
		IE8 will give you:
		<input/></input/>
	-->
	<xsl:template match="html:input|html:img|html:br">
		<xsl:copy-of select="."/>
	</xsl:template>
	<!--
		Templates for non-conforming HTML elements and attributes

		These templates will remove or change any elements which are non-conforming
		according to the HTML5 specification. See {{http://www.w3.org/TR/html5/obsolete.html#non-conforming-features}}

		Non-conforming elements

		acronym, applet, bgsound, dir, frame, frameset,
		noframes, isindex, listing, nextid, noembed, plaintext,
		rb, strike, xmp, basefont, big, blink, center,
		font, marquee, multicol, nobr, spacer, tt

		We transform acronym to abbr (see below). You could transform some others
		to conforming elements but one has to weigh the extra cost of the XSLT
		against the benefit for little used elements.

		Some examples of non-conforming elements which could be transformed
		font to span
		center to div (style='text-align:center')
		dir to ul
		tt to kbd or code or var or samp (sometimes it is not easy)
	-->
	<xsl:template match="html:applet|html:bgsound|html:dir|html:frame|html:frameset|html:noframes|html:isindex|html:listing|html:nextid|html:noembed|html:plaintext|html:rb|html:strike|html:xmp|html:basefont|html:big|html:blink|html:center|html:font|html:marquee|html:multicol|html:nobr|html:spacer|html:tt"/>
	<!--
		Conforming but problematic elements

		We can change some elements with known or suspect accessibilty issues:
			b to strong
			i to em

		Template match="html:acronym|html:b|html:i"

		This template converts the non-conforming acronym element to the conforming abbr
		element; the problematic b element to the string element and the contentious i
		element to the controversial em element. All other attributes from the original
		element are applied (subject to the attribute transforms for non-conforming
		attributes).
	-->
	<xsl:template match="html:acronym|html:b|html:i">
		<xsl:variable name="elementName">
			<xsl:choose>
				<xsl:when test="self::html:acronym">abbr</xsl:when>
				<xsl:when test="self::html:b">strong</xsl:when>
				<xsl:when test="self::html:i">em</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$elementName}">
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>


	<!--
		Non-conforming attributes

		There is a huge list of non-conforming attibutes (see {{http://www.whatwg.org/specs/web-apps/current-work/#non-conforming-features}}
		so this template removes them from any HTML elements in the application.

		This is separated from the template to remove non-conforming elements only because
		this list is unmanageably large and it makes our life easier to keep them separate.
		As with elements you could, if feeling kind, transform some to conforming attributes
		as listed in the spec.

		<<NOTE:>> any attribute which does not have a  modifier is not used in the
		WComponents schema for ui:*. This is just a shorthand to make this XSLT smaller
		but may cause future problems if the schema changes. If something new doesn't
		work: look here first.

		List of non-conforming attributes

		@abbr, @axis, @background, @bgcolor, @border, @char, @charoff, @charset, @compact,
		@hspace, @longdesc, @nowrap, @rev, @urn, @vspace, @marginheight, @marginwidth,
		@methods, */@align, */@name, */@valign, a/@coords, a/@shape,
		area/@nohref, body/@alink, body/@link, body/@marginbottom,
		body/@marginleft, body/@marginright, body/@margintop, body/@text,
		body/@vlink, br/@clear, col/@width, form/@accept, head/@profile,
		hr/@color, hr/@noshade, hr/@size, hr/@width, html/@version,
		iframe/@allowtransparency, iframe/@frameborder, iframe/@scrolling,
		img/@lowsrc, input/@usemap, li/@type, link/@target, meta/@scheme,
		object/@archive, object/@classid, object/@code, object/@codebase,
		object/@codetype, object/@declare, object/@standby, param/@type,
		param/@valuetype, pre/@width, script/@language, script/@event,
		table/@cellpadding, table/@cellspacing, table/@datapagesize,
		table/@frame, table/@rules, table/@summary, table/@width,
		td/@height, td/@scope, td/@width, th/@height, th/@width,
		ul/@type
	-->
	<xsl:template match="@abbr|@axis|@background|@bgcolor|@border|@char|@charoff|@charset|@compact|@hspace|@longdesc|@nowrap|@rev|@urn|@vspace|@marginheight|@marginwidth|@methods|
		html:*/@align|html:*/@name|html:*/@valign|
		html:a/@coords|html:a/@shape|
		html:area/@nohref|
		html:body/@alink|html:body/@link|html:body/@marginbottom|html:body/@marginleft|html:body/@marginright|html:body/@margintop|html:body/@text|html:body/@vlink|
		html:br/@clear|
		html:col/@width|
		html:form/@accept|
		html:head/@profile|
		html:hr/@color|html:hr/@noshade|html:hr/@size|html:hr/@width|
		html:html/@version|
		html:iframe/@allowtransparency|html:iframe/@frameborder|html:iframe/@scrolling|
		html:img/@lowsrc|
		html:input/@usemap|
		html:li/@type|
		html:link/@target|
		html:meta/@scheme|
		html:object/@archive|html:object/@classid|html:object/@code|html:object/@codebase|html:object/@codetype|html:object/@declare|html:object/@standby|
		html:param/@type|
		html:param/@valuetype|
		html:pre/@width|
		html:script/@language|html:script/@event|
		html:table/@cellpadding|html:table/@cellspacing|html:table/@datapagesize|html:table/@frame|html:table/@rules|html:table/@summary|html:table/@width|
		html:td/@height|html:td/@scope|html:td/@width|
		html:th/@height|html:th/@width|
		html:ul/@type"/>

	<!--
		WComponents conformance

		HTML namespace templates which are required to ensure a mixed-mode document
		will not break anything WComponents-y or cause horrific HTML problems.

		NOTE: we generally do not try to enforce specification conformance unless in
		diagnostic mode. These templates are just things we have to have to even get
		a reliably rendering page.

		Do not put a HTML form element inside a ui:application, just output its
		content.
	-->
	<xsl:template match="html:form" />

</xsl:stylesheet>
