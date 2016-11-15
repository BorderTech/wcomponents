<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.registrationScripts.localRequiredLibraries.xsl"/>
	<!--
		Helper template for "registrationScripts" to wire up AMD/require.js requires.

		This template is never called directly except by the template "registrationScripts" and is split out for ease of
		override and maintenance.

		You probably want to override this template to remove modules which you have included in your wc.common.js
		because the descendant XPath queries are heavy/ If you simply want to add new stuff then use the helper template
		"localRequiredLibraries" to add your requires.

		One optimization you could consider is to remove all tests which include
		a library which is in your wc.common.js.
	-->
	<xsl:template name="requiredLibraries">
		<xsl:variable name="libs">
			<!--
				These are very expensive lookups, especially in poorly optimized processors such as the default
				processor used by IE. For this reason the first group are lookups which for most sensible purposes would
				be included in wc.common.js for all screens.
			-->
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
			<xsl:if test=".//ui:error or .//ui:skiplinks or .//ui:link[substring(@url, 1, 1) eq '#']">
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
			
			<xsl:if test=".//ui:togglebutton[not(@readOnly)]">
				<xsl:text>"wc/ui/checkboxAnalog",</xsl:text>
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

			<!--
				The following are in alphabetical order of local-name of the
				first element in the test, then in alphabetical order of
				attribute. Just because we think they are less common does not
				mean they should not be in wc.common.js.
			-->

			<xsl:if test=".//ui:audio or .//ui:video">
				<xsl:text>"wc/ui/mediaplayer",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:checkbox[not(@readOnly)] or .//ui:checkboxselect[not(@readOnly)]">
				<xsl:text>"wc/ui/checkBox",</xsl:text>
				<xsl:if test=".//ui:checkboxselect[not(@readOnly)]">
					<xsl:text>"wc/ui/checkBoxSelect",</xsl:text>
				</xsl:if>
			</xsl:if>
			<xsl:if test=".//ui:collapsible">
				<xsl:text>"wc/ui/collapsible",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:collapsibletoggle">
				<xsl:text>"wc/ui/collapsibleToggle",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:fileupload">
				<xsl:text>"wc/ui/multiFileUploader","wc/ui/fileUpload",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:listbox[not(@readOnly)]">
				<xsl:text>"wc/ui/dropdown",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:link[@type eq 'button' or ui:windowAttributes[count(@*) gt 1]]">
				<xsl:text>"wc/ui/navigationButton",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:menu">
				<xsl:text>"wc/ui/menu",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:multidropdown[not(@readOnly)]">
				<xsl:text>"wc/ui/multiFormComponent",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:multiselectpair[not(@readOnly)]">
				<xsl:text>"wc/ui/multiSelectPair",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:multitextfield[not(@readOnly)]">
				<xsl:text>"wc/ui/multiFormComponent","wc/ui/textField",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:printbutton">
				<xsl:text>"wc/ui/printButton",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:radiobuttonselect[not(@readOnly)] or .//ui:radiobutton[not(@readOnly)]">
				<xsl:text>"wc/ui/radioButtonSelect",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:shuffler[not(@readOnly)] or .//ui:multiselectpair[@shuffle and not(@readOnly)]">
				<xsl:text>"wc/ui/shuffler",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:suggestions or .//ui:dropdown[@type and not(@readOnly)]">
				<xsl:text>"wc/ui/comboBox",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:table">
				<xsl:text>"wc/ui/table",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:tabset">
				<xsl:text>"wc/ui/tabset",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:tree">
				<!-- htreesize requires tree and resizeable, tree requires treeitem. -->
				<xsl:text>"wc/ui/menu/htreesize",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:session">
				<xsl:text>"wc/ui/timeoutWarn",</xsl:text>
			</xsl:if>
			<xsl:if test=".//*[@submitOnChange and not(@readOnly)]">
				<xsl:text>"wc/ui/onchangeSubmit",</xsl:text>
			</xsl:if>
			<xsl:if test=".//@cancel">
				<xsl:text>"wc/ui/cancelButton",</xsl:text>
			</xsl:if>
			<xsl:if test=".//@msg">
				<xsl:text>"wc/ui/confirm",</xsl:text>
			</xsl:if>
			<!-- NOTE: not every mode SERVER needs this but the include is cheaper than the tests and mode server should eventually die -->
			<xsl:if test=".//*[@mode eq 'dynamic'] or .//*[@mode eq 'lazy'] or .//*[@mode eq 'server']">
				<xsl:text>"wc/ui/containerload",</xsl:text>
			</xsl:if>
			<xsl:call-template name="localRequiredLibraries"/>
		</xsl:variable>
		<xsl:variable name="nLibs" select="normalize-space($libs)"/>
		<xsl:if test="$nLibs ne ''">
			<xsl:text>require([</xsl:text>
			<xsl:value-of select="substring($nLibs,1,string-length($nLibs)-1)"/>
			<xsl:text>]);</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
