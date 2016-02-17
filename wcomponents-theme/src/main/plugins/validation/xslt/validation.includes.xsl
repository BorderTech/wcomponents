<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Client side validation plugin: creating the requirejs requirements array to enable validation of each
		component.

		This template is called from plugin_validation_root and should not be called from anywhere else. It has been
		split out to allow for easier implementation override.

		What it Does
		============

		This template adds the requires for the validation JavaScript. There are various degrees of optimisation you
		could use and a few examples are given below.

		Possible optimisations
		======================

		0. Do not include a test (or a require) for any component your theme excludes. If your theme is for a single
		specific application (unusual but not unheard of) you could include only requires for components you KNOW will
		be used - though this is VERY dangerous (because designers are good at putting in last minute changes)!

		1. You could remove all of the element tests and just require everything.

		2. Have element tests for less common components and include common components untested. Note, though, that
		cancelUpdate is always required when client side validation is on.

		3. You could (actually SHOULD) cross reference your common.js file and include an untested require for anything
		in that. Your common.js should contain all components which are so commonly used that the cost of testing for
		them is greater than the cost of including unused scrips. Note, though, that cancelUpdate is always required
		when client side validation is on.

		I have included some example optimisations below.
	-->

	<!--
		UNOPTIMISED VERSION
		===================

		Tests for everything. Use this if you do not want to load ANY superfluous JavaScript. This is handy if your
		application has views which have radically differing form component content or if it is likely to be used over
		slower networks. This is probably what you want for public use in Australia for example where the Internet
		infrastructure is particularly primitive.

	<xsl:template name="plugin_validation_includes">
		<xsl:text>require([</xsl:text>
		<xsl:if test=".//ui:checkbox[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/checkBox",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:checkboxselect[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/checkBoxSelect",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:datefield[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/dateField",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:dropdown[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/dropdown","${validation.core.path.name}/textField",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:fieldset">
			<xsl:text>"${validation.core.path.name}/fieldset",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:fileupload[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/fileUpload",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:multidropdown[not(@readOnly)] or .//ui:multitextfield[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/multiFormComponent",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:multiselectpair[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/multiSelectPair",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:numberfield[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/numberField",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:radiobuttonselect[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/radioButtonSelect",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:textarea[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/textArea",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:textfield[not(@readOnly)] or .//ui:emailfield[not(@readOnly)] or .//ui:phonenumberfield[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/textField",</xsl:text>
		</xsl:if>
		<xsl:if test=".//*[@required='true']">
			<xsl:text>"${validation.core.path.name}/required",</xsl:text>
		</xsl:if>
		<xsl:text>"${validation.core.path.name}/cancelUpdate"</xsl:text>
		<xsl:text>]);</xsl:text>
	</xsl:template>
	-->

	<!--
		OPTIMISATION 1
		==============
		Test nothing - include everything: all.js is an empty require which includes all of the	component level
		validation scripts. The validation JavaScript is not huge once it is compressed and the component lookups are
		time consuming (relatively - the XSLT is fast with modern processors).

	<xsl:template name="plugin_validation_includes">
			<xsl:text>require(["${validation.core.path.name}/all"]);</xsl:text>
	</xsl:template>

		An extension of this optimisation (1a?): you could override all.js to always include only those components you
		want to include - a bit of a back-handed exclude.
	-->

	<!--
		OPTIMISATION 2
		==============

		Include some very common things, test for others.

		This optimisation assumes we always want to include mandatory field testing (pretty common) and that most
		screens will include ui:textfield or its extensions (also really very common). I have included an untested
		require for fieldsets as well as many compound controls use a fieldset as a wrapper and a properly structured
		accessible application will use fieldsets quite commonly.

		You could base the tests on the components which are included in your common.js: include those components'
		validation scripts without testing and only test for components not in your common. Note, though, that
		cancelUpdate is always required when client side validation is on.
	 -->
	<xsl:template name="plugin_validation_includes">
		<xsl:text>require([</xsl:text>
		<xsl:if test=".//ui:checkbox[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/checkBox",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:checkboxselect[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/checkBoxSelect",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:datefield[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/dateField",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:dropdown[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/dropdown",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:fileupload[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/fileUpload",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:multidropdown[not(@readOnly)] or .//ui:multitextfield[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/multiFormComponent",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:multiselectpair[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/multiSelectPair",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:numberfield[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/numberField",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:radiobuttonselect[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/radioButtonSelect",</xsl:text>
		</xsl:if>
		<xsl:if test=".//ui:textarea[not(@readOnly)]">
			<xsl:text>"${validation.core.path.name}/textArea",</xsl:text>
		</xsl:if>
		<xsl:text>"${validation.core.path.name}/cancelUpdate","${validation.core.path.name}/fieldset","${validation.core.path.name}/required","${validation.core.path.name}/textField"]);</xsl:text>
	</xsl:template>
</xsl:stylesheet>