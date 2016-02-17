<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.registrationScripts.localRequiredLibraries.xsl"/>
	<!-- 
		These are very expensive lookups, especially in poorly optimized processors such as the default processor used 
		by IE <= 9. For this reason the first group are lookups which for most sensible purposes would be included in
		wc.common.js for all screens.
		
		The format of the text output if the tests is passed is the quoted module name folowed by a comma(,). The
		final trailing comma is expected and you might [i.e. WILL] break stuff if it is not there.
		
		You really should override this with an empty template (see bottom comment) and include ALL of these in your
		wc.common.js.
		
		Just to make sure you read the previous:
		################################################################################################################
		# 1. OVERRIDE THIS WITH AN EMPTY TEMPLATE IF YOU WANT BETTER PERFORMANCE.
		#
		# 2. YOU ALWAYS WANT BETTER PERFORMANCE!
		################################################################################################################
		
		There is nothing important about the order of the tests. They are element then attribute and in alpha by 
		local-name of the first element within the element tests. It is debateable whether it would be better to 
		test for element[1] as the processor may calculate the entire node list then return the first node or stop 
		traversing the tree at the first match. It is not worth the research effort since YOU SHOULD NOT HAVE THIS 
		TEMPLATE IN A REAL SCENARIO!
		
		If you override template name="requiredLibraries" so it does not call this template you can ignore it completely
		as the XSLT compressor will delete unused templates. But it is maybe better to do both so this large template
		doesn't appear in your debug version (it will not be used if it is not invoked but it adds noise when hunting
		for snarks).
	-->
	<xsl:template name="commonRequiredLibraries">
		<xsl:if test=".//ui:datefield">
			<!--
				calendar uses dateField, dateField does not use calendar, I might fix that one day. The calendar 
				polyfill uses number field.
			-->
			<xsl:text>"wc/ui/numberField","wc/ui/calendar",</xsl:text>
		</xsl:if>
		
		<xsl:if test=".//ui:dropdown[not(@readOnly)]">
			<xsl:text>"wc/ui/dropdown","wc/ui/selectboxSearch",</xsl:text>
		</xsl:if>
		
		<!-- In this test I have tested for ui:skiplinks before ui:link even thoug ui:link is more common because of
			the extra processing of the predicate. It is probably a negligible overhead. -->
		<xsl:if test=".//ui:error or .//ui:skiplinks or .//ui:link[substring(@url, 1, 1)='#']">
			<xsl:text>"wc/ui/internalLink",</xsl:text>
		</xsl:if>
		
		<xsl:if test=".//ui:fieldlayout">
			<xsl:text>"wc/ui/field",</xsl:text>
		</xsl:if>
		
		<!-- If you have any sense then these are in your wc.common.js. Labels are ubiquitous. This is why we test 
			ui:label before ui:fieldset simple because it is more likly to get one early in source order.
		-->
		<xsl:if test=".//ui:label or .//ui:fieldset">
			<xsl:text>"wc/ui/label",</xsl:text>
		</xsl:if>
		
		<xsl:if test=".//ui:numberfield[not(@readOnly)]">
			<xsl:text>"wc/ui/numberField",</xsl:text>
		</xsl:if>
		
		<xsl:if test=".//ui:textarea[not(@readOnly)]">
			<xsl:text>"wc/ui/textArea",</xsl:text>
		</xsl:if>
		
		<!-- 
			These are in order of 'likelihood'. We use or rather than | as most decent processors will stop after the 
			first successful nodeset is found. You REALLY want wc/ui/textField in your wc.common.js though.
		-->
		<xsl:if test=".//ui:textfield[not(@readOnly)] or .//ui:numberfield[not(@readOnly)] or .//ui:emailfield[not(@readOnly)] or .//ui:passwordfield[not(@readOnly)] or .//ui:phonenumberfield[not(@readOnly)]">
			<xsl:text>"wc/ui/textField",</xsl:text>
		</xsl:if>
		
		<xsl:if test=".//@accessKey">
			<xsl:text>"wc/ui/tooltip",</xsl:text>
		</xsl:if>
		<xsl:if test=".//@buttonId">
			<xsl:text>"wc/ui/defaultSubmit",</xsl:text>
		</xsl:if>
	</xsl:template>
	
	<!--
		Here is what I would consider the best variation on this template and the one I use in all real implementations:
		<xsl:template name="commonRequiredLibraries"/>
		Simple eh!
	-->
</xsl:stylesheet>
