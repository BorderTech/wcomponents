<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.registrationScripts.commonRequiredLibraries.xsl"/>
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
			<xsl:call-template name="commonRequiredLibraries"/>

			<!--
				The following are in alphabetical order of local-name of the
				first element in the test, then in alphabetical order of
				attribute. Just because we think they are less common does not
				mean they should not be in wc.common.js.
			-->

			<xsl:if test=".//ui:audio or .//ui:video">
				<xsl:text>"wc/ui/mediaplayer",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:checkBox[not(@readOnly)] or .//ui:checkBoxSelect[not(@readOnly)]">
				<xsl:text>"wc/ui/checkBox",</xsl:text>
				<xsl:if test=".//ui:checkBoxSelect[not(@readOnly)]">
					<xsl:text>"wc/ui/checkBoxSelect",</xsl:text>
				</xsl:if>
			</xsl:if>
			<xsl:if test=".//ui:collapsible">
				<xsl:text>"wc/ui/collapsible",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:expandCollapseAll">
				<xsl:text>"wc/ui/collapsibleToggle",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:fileUpload">
				<xsl:text>"wc/ui/multiFileUploader","wc/ui/fileUpload",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:filterControl">
				<xsl:text>"wc/ui/filterControl",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:listBox[not(@readOnly)]">
				<xsl:text>"wc/ui/dropdown",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:link[@type='button' or ui:windowAttributes[count(@*) &gt; 1]]">
				<xsl:text>"wc/ui/navigationButton",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:menu">
				<xsl:text>"wc/ui/menu",</xsl:text>
				<!--
					NOTE: these are expensive lookups for what is actually pretty tiny JS load savings.	That is why we
					built the meta-module.
				<xsl:if test=".//ui:menu[@type='bar' or @type='flyout']">
					<xsl:text>"wc/ui/menu/bar",</xsl:text>
				</xsl:if>
				<xsl:if test=".//ui:menu[@type='column']">
					<xsl:text>"wc/ui/menu/column",</xsl:text>
				</xsl:if>
				<xsl:if test=".//ui:menu[@type='tree']">
					<xsl:text>"wc/ui/menu/tree",</xsl:text>
				</xsl:if>
				<xsl:if test=".//ui:menuItem[@url]">
					<xsl:text>"wc/ui/navigationButton",</xsl:text>
				</xsl:if>-->
			</xsl:if>
			<xsl:if test=".//ui:multiDropdown[not(@readOnly)]">
				<xsl:text>"wc/ui/multiFormComponent",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:multiSelectPair[not(@readOnly)]">
				<xsl:text>"wc/ui/multiSelectPair",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:multiTextField[not(@readOnly)]">
				<xsl:text>"wc/ui/multiFormComponent","wc/ui/textField",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:printButton">
				<xsl:text>"wc/ui/printButton",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:radioButtonSelect[not(@readOnly)] or .//ui:radioButton[not(@readOnly)]">
				<xsl:text>"wc/ui/radioButtonSelect",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:shuffler[not(@readOnly)] or .//ui:multiSelectPair[@shuffle and not(@readOnly)]">
				<xsl:text>"wc/ui/shuffler",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:suggestions or .//ui:dropdown[@type and not(@readOnly)]">
				<xsl:text>"wc/ui/comboBox",</xsl:text>
			</xsl:if>
			<xsl:if test=".//ui:table">
				<xsl:text>"wc/ui/table",</xsl:text>
				<!--<xsl:if test=".//ui:table/ui:actions">
					<xsl:text>"wc/ui/table/action",</xsl:text>
				</xsl:if>
				<xsl:if test=".//ui:rowSelection">
					<xsl:text>"wc/ui/rowAnalog",</xsl:text>
				</xsl:if>
				<xsl:if test=".//ui:rowExpansion">
					<xsl:text>"wc/ui/table/rowExpansion",</xsl:text>
				</xsl:if>
				<xsl:if test=".//ui:pagination">
					<xsl:text>"wc/ui/table/pagination",</xsl:text>
				</xsl:if>
				<xsl:if test=".//ui:sort">
					<xsl:text>"wc/ui/table/sort",</xsl:text>
				</xsl:if>-->
			</xsl:if>
			<xsl:if test=".//ui:tabset">
				<xsl:text>"wc/ui/tabset",</xsl:text>
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
			<xsl:if test=".//@mode='dynamic' or .//@mode='lazy' or .//@mode='server'">
				<xsl:text>"wc/ui/containerload",</xsl:text>
			</xsl:if>
			<xsl:call-template name="localRequiredLibraries"/>
		</xsl:variable>
		<xsl:variable name="nLibs" select="normalize-space($libs)"/>
		<xsl:if test="$nLibs !=''">
			<xsl:text>require([</xsl:text>
			<xsl:value-of select="substring($nLibs,1,string-length($nLibs)-1)"/>
			<xsl:text>]);</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
