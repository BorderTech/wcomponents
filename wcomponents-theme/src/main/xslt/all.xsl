<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml"
	version="2.0">

	
	<!-- templates which transform to nothing 
		
		* Consume comments and do not pass them through
		* Remove link, base and meta elements completely from flow. These are all hoisted into the HTML head element in ui:root.
		* Do not put a HTML form inside any WApplication.
		* ui:additionalParameters no longer used or part of the Java API but still in the schema.
		-->
	<xsl:template match="comment()|ui:comment|html:link|html:base|html:meta|html:form|ui:additionalParameters"/>

	<!--
		Generic utility templates.
		
		These are templates for text nodes, unmatched elements and unmatched attributes. You will often see example templates like this:
		
		``` xml
		<xsl:template match="*|@*|node()">
			<xsl:copy>
				<xsl:apply-templates select="@*|node()/>
			</xsl:copy
		</xsl:template>
		```
		
		whereas we split these into separate templates. This because in XSLT 2 a node with no children cannot apply templates. Using a single template
		like that above is also a performance issue, making a copy of a text node is slower than outputting its value.
		
		There is also one more caveat with element nodes:
		
		Template for unmatched elements. Make a copy of the element. We make an element using local-name() rather than the more obvious xsl:copy 
		because copy will retain the namespace attributes..
	-->
	<xsl:template match="*">
		<xsl:element name="{local-name()}">
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<!--
		Unmatched attributes: make a copy of the attribute.
	-->
	<xsl:template match="@*">
		<xsl:copy />
	</xsl:template>
	
	<!--
		For text nodes we use value-of rather than apply-templates on text nodes as this provides improved performance. This is actually redundant as
		it is the default rule but I have seen too many variations on `match node(), copy, apply templates` as in the above comment to leave this to
		chance!
	-->
	<xsl:template match="text()">
		<xsl:value-of select="."/>
	</xsl:template>


	<!--
		html:link can appear in a ui:ajaxtarget and in this case cannot be moved to a HEAD element so we just output it 
		in-situ.
	-->
	<xsl:template match="html:link[ancestor::ui:ajaxtarget]">
		<xsl:choose>
			<xsl:when test="@rel='stylesheet'">
				<script type="text/javascript">
					<xsl:text>require(["wc/loader/style"],function(s){s.add("</xsl:text>
					<xsl:value-of select="@href"/>
					<xsl:text>","</xsl:text>
					<xsl:if test="@media">
						<xsl:value-of select="@media"/>
					</xsl:if>
					<xsl:text>", true);});</xsl:text>
				</script>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="html:link[@rel='stylesheet']" mode="inHead">
		<xsl:text>s.add("</xsl:text>
		<xsl:value-of select="@href"/>
		<xsl:if test="@media">
			<xsl:text>","</xsl:text>
			<xsl:value-of select="@media"/>
		</xsl:if>
		<xsl:text>");</xsl:text>
	</xsl:template>

	<!-- Copy without XML namespaces. Also prevents double output of XHTML self-closing elements like `br` and `hr` -->
	<xsl:template name="copyHtml">
		<xsl:element name="{local-name(.)}">
			<xsl:apply-templates select="@*"/>
		</xsl:element>
	</xsl:template>

	<!--
		Copy link, base and meta elements in the head.
	-->
	<xsl:template match="html:link|html:base|html:meta" mode="inHead">
		<xsl:call-template name="copyHtml"/>
	</xsl:template>

	<xsl:template match="html:input|html:img|html:br|html:hr">
		<xsl:call-template name="copyHtml"/>
	</xsl:template>


	<xsl:include href="wc.checkablegroup.xsl"/>
	<xsl:include href="wc.fileupload.xsl"/>
	<xsl:include href="wc.inputs.xsl"/>
	<xsl:include href="wc.shuffleable.xsl"/>
	<xsl:include href="wc.ui.dateField.xsl"/>
	<xsl:include href="wc.ui.dropdown.xsl"/>
	<xsl:include href="wc.ui.listbox.xsl"/>
	<xsl:include href="wc.ui.multidropdown.xsl"/>
	<xsl:include href="wc.ui.multitextfield.xsl"/>
	<xsl:include href="wc.ui.numberfield.xsl"/>
	<xsl:include href="wc.ui.textarea.xsl"/>
	<xsl:include href="wc.ui.togglebutton.xsl"/>

</xsl:stylesheet>