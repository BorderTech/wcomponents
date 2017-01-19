<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Common helper template for generating the attributes for accessKey implementation.
		
		*** READ THIS ***
		If you are changing the XSLT: If you call this template without this parameter set to an int other than 1 then this MUST (ABSOLUTELY MUST) be
		the last attribute on the element as it adds content to the element.
	-->
	<xsl:template name="accessKey">
		<xsl:if test="@accessKey">
			<xsl:attribute name="accesskey">
				<xsl:value-of select="@accessKey"/>
			</xsl:attribute>
			<xsl:attribute name="aria-describedby">
				<xsl:value-of select="concat(@id,'_wctt')"/>
			</xsl:attribute>
			<span id="{concat(@id,'_wctt')}" role="tooltip" hidden="hidden">
				<xsl:value-of select="@accessKey"/>
			</span>
		</xsl:if>
	</xsl:template>

	<!--
		This helper template sets aria-label attribtue if the component has its accessibleText property set.
	-->
	<xsl:template name="ariaLabel">
		<xsl:if test="@accessibleText">
			<xsl:attribute name="aria-label">
				<xsl:value-of select="@accessibleText"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	
	<!-- Helper to create the HTML class attribute's content. -->
	<xsl:template name="commonClassHelper">
		<xsl:param name="additional" select="''"/>
		<xsl:variable name="baseClass" select="concat('wc-', local-name(.))"/>
		<xsl:value-of select="$baseClass"/>
		<xsl:if test="@type and not(self::ui:file)">
			<xsl:value-of select="concat(' ',$baseClass,'-type-', @type)"/>
		</xsl:if>
		<xsl:if test="@align">
			<xsl:value-of select="concat(' wc-align-', @align)"/>
		</xsl:if>
		<xsl:if test="@layout">
			<xsl:value-of select="concat(' wc-layout-', @layout)"/>
		</xsl:if>
		<xsl:if test="@track">
			<xsl:text> wc_here</xsl:text>
		</xsl:if>
		<xsl:if test="@class">
			<xsl:variable name="classes" select="normalize-space(@class)"/>
			<xsl:if test="$classes ne ''">
				<xsl:value-of select="concat(' ', $classes)"/>
			</xsl:if>
		</xsl:if>
		<xsl:if test="$additional ne ''">
			<xsl:variable name="moreclasses" select="normalize-space($additional)"/>
			<xsl:if test="$moreclasses ne ''">
				<xsl:value-of select="concat(' ', $moreclasses)"/>
			</xsl:if>
		</xsl:if>
		<xsl:apply-templates select="ui:margin" mode="class" />
	</xsl:template>

	<!-- Make the HTML class attribute and populate it. -->
	<xsl:template name="makeCommonClass">
		<xsl:param name="additional"/>
		<xsl:attribute name="class">
			<xsl:call-template name="commonClassHelper">
				<xsl:with-param name="additional" select="$additional"/>
			</xsl:call-template>
		</xsl:attribute>
	</xsl:template>

	<!--
		Common helper template for marking a component as disabled. Used by all components which may be disabled. 
		
		param isControl default 0
		This determines if we use the disabled or aria-disabled attribute to mark the component as disabled. If true the
		disabled attribute is set; this may only be set on components which output a form control or fieldset element.
		
		param field default  '.'
		The component to disable, ususally though not always, the current node. See wc.ui.menuItem.xsl or 
		wc.ui.submenu.xsl for extreme cases where the component being disabled may not be the component which has the 
		@disabled attribute.
	-->
	<xsl:template name="disabledElement">
		<xsl:param name="isControl" select="0"/>
		<xsl:param name="field" select="."/>
		<xsl:if test="$field/@disabled">
			<xsl:choose>
				<xsl:when test="number($isControl) eq 1">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="aria-disabled">
						<xsl:text>true</xsl:text>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- Templates used to hide components in the UI. -->
	<xsl:template name="hiddenElement">
		<xsl:attribute name="hidden">
			<xsl:text>hidden</xsl:text>
		</xsl:attribute>
	</xsl:template>
	
	<!--
		Hides an HTML element from all user agents if the element's hidden attribute is set 'true'.
	-->
	<xsl:template name="hideElementIfHiddenSet">
		<xsl:if test="@hidden">
			<xsl:call-template name="hiddenElement"/>
		</xsl:if>
	</xsl:template>

	<!--
		Common helper template for marking interactive controls as required if its required attribute is set 'true'.

		param field:
			The element to test for required-ness. This is usually, though not
			always, the current node. Default . (current node)
		param useNative:
			Indicates whther to use the attribute "required" which is supported by
			form controls or set to 0 to use "aria-required. Default 1.
	-->
	<xsl:template name="requiredElement">
		<xsl:param name="field" select="."/>
		<xsl:param name="useNative" select="1"/>
		<xsl:if test="$field/@required">
			<xsl:choose>
				<xsl:when test="number($useNative) eq 1">
					<xsl:attribute name="required">
						<xsl:text>required</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="aria-required">
						<xsl:text>true</xsl:text>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!--
		Common helper template to create "title" attributes.
		param title: string to use instead of component's toolTip attribute.
		param contentAfter: additional content to apply after the title content.
	-->
	<xsl:template name="title">
		<xsl:variable name="text">
			<xsl:value-of select="@toolTip"/>
		</xsl:variable>
		<xsl:if test="$text ne ''">
			<xsl:attribute name="title">
				<xsl:value-of select="$text"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	
	<!--
		A set of attributes commonly applied to the transformed output of many components.
	-->
	<xsl:template name="commonAttributes">
		<xsl:param name="isControl" select="0" />
		<xsl:param name="isWrapper" select="0" />
		<xsl:param name="class" select="''" />

		<xsl:attribute name="id">
			<xsl:value-of select="@id" />
		</xsl:attribute>
		<xsl:call-template name="makeCommonClass">
			<xsl:with-param name="additional">
				<xsl:value-of select="$class" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="hideElementIfHiddenSet" />
		<xsl:if test="not(@readOnly or number($isWrapper) eq 1)">
			<xsl:call-template name="disabledElement">
				<xsl:with-param name="isControl" select="$isControl" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--
		TODO: these need a good clean up.

		A set of helper templates to add commonly used groups of attributes to
		elements. We cannot use actual XSLT attribute-sets because the
		attributes require calculation and parameters.
	-->

	<!--
		Attributes applied to the outer 'wrapper' element of complex components.
		param id: The component's id, default @id.
		param isError: A node list of ui:errors (if any) associated with the
			component.
		param live: The value to set to aria-live if the component is an ajax
			target,  default 'polite'.
		param isControl: see "commonAtrributes: below, default 1.
	-->
	<xsl:template name="commonWrapperAttributes">
		<xsl:param name="class" select="''"/>
		<!--normally fieldset-->
		<xsl:call-template name="commonAttributes">
			<xsl:with-param name="isWrapper" select="1" />
			<xsl:with-param name="class">
				<xsl:value-of select="$class"/>
				<xsl:if test="not(@readonly or self::ui:checkboxselect[not(@frameless)] or self::ui:radiobuttonselect[not(@frameless)])">
					<xsl:text> wc_noborder</xsl:text>
				</xsl:if>
				<xsl:if test="not(@readOnly) and @required">
					<xsl:text> wc_req</xsl:text>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="title"/>
		<xsl:call-template name="ariaLabel" />
	</xsl:template>

	<!-- 
		Simple inputs are wrapped in a span. These attributes are common to all of them.
	-->
	<xsl:template name="wrappedInputAttributes">
		<xsl:param name="name" select="@id"/>
		<xsl:param name="useTitle" select="1"/>
		<xsl:param name="useRequired" select="1"/>
		<xsl:param name="type" select="''"/>

		<xsl:attribute name="id">
			<xsl:value-of select="concat(@id,'_input')"/>
		</xsl:attribute>
		<xsl:if test="$name ne ''">
			<xsl:attribute name="name">
				<xsl:value-of select="$name"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="$type ne ''">
			<xsl:attribute name="type">
				<xsl:value-of select="$type"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="number($useTitle) eq 1">
			<xsl:call-template name="title"/>
		</xsl:if>
		<xsl:if test="number($useRequired) eq 1">
			<xsl:call-template name="requiredElement"/>
		</xsl:if>
		<xsl:call-template name="disabledElement">
			<xsl:with-param name="isControl" select="1"/>
		</xsl:call-template>
		<xsl:call-template name="ariaLabel" />
		<xsl:if test="@buttonId">
			<xsl:attribute name="data-wc-submit">
				<xsl:value-of select="@buttonId" />
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="@submitOnChange and not(@list)">
			<xsl:attribute name="class">
				<xsl:text>wc_soc</xsl:text>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>

	<xsl:template name="wrappedTextInputAttributes">
		<xsl:param name="name" select="@id"/>
		<xsl:param name="useTitle" select="1"/>
		<xsl:param name="useRequired" select="1"/>
		<xsl:param name="type" select="''"/>
		<xsl:call-template name="wrappedInputAttributes">
			<xsl:with-param name="name" select="$name"/>
			<xsl:with-param name="useTitle" select="$useTitle"/>
			<xsl:with-param name="useRequired" select="$useRequired"/>
			<xsl:with-param name="type" select="$type"/>
		</xsl:call-template>
		<xsl:if test="@placeholder or @required">
			<xsl:attribute name="placeholder">
				<xsl:choose>
					<xsl:when test="@placeholder">
						<xsl:value-of select="@placeholder"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>{{t 'requiredPlaceholder'}}</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>

	<xsl:template name="commonInputWrapperAttributes">
		<xsl:param name="class" select="''"/>
		<xsl:call-template name="commonAttributes">
			<xsl:with-param name="class">
				<!-- 
					NOTE: the space after `wc-input-wrapper` is for safety as makeCommonClass will normalise for us.
					It allows the calling template to not have to include a preceding space in its param.
				-->
				<xsl:value-of select="concat('wc-input-wrapper ', $class)"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
