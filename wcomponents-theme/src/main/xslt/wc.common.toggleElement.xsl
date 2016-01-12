<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.disabledElement.xsl"/>
<!--
	Toggle controls
	
	Helper templates and keys for common state toggling elements. 
-->

	<!--
		Creates an element which does state toggling. Called from selectToggle and collapsibleToggle
		wc.common.selectToggle.xsl and wc.common.collapsibleToggle.xsl.
		 
		param submit:
			If 1 the state change is done on the server and the toggle element must be a 
			form submit control.
		 
		param id: The id to apply to the HTML button element
		
		param for: The identifier of the component controlled by the button, may be a container
			id or a groupName for {{expandCollapseAll}}
		
		param name: The name (or name analog) attribute to give to the button element 
		
		param value: The value (or value analog) to give to the button
		 
		param class: Any extra class(es) to apply to the button based on the required functionality
			or appearance of controls for each type of component which calls this template.
		 
		param text: The text description of the purpose of the button.
		
		param selected: The current selected state used by selectToggle to indicate the aria-checked state of the button.
	-->
	<xsl:template name="toggleElement">
		<xsl:param name="mode"/>
		<xsl:param name="id"/>
		<xsl:param name="for"/>
		<xsl:param name="name"/>
		<xsl:param name="value"/>
		<xsl:param name="class"/>
		<xsl:param name="text"/>
		<xsl:param name="selected"/>
		<xsl:param name="labelId"/>
	
		<xsl:variable name="localClass">
			<xsl:text>wc_btn_nada</xsl:text>
			<xsl:if test="$class!=''">
				<xsl:value-of select="concat(' ',$class)"/>
			</xsl:if>
		</xsl:variable>

		<button id="{$id}" role="radio" class="{$localClass}" data-wc-value="{$value}">
			<xsl:if test="$name !=''">
				<xsl:attribute name="data-wc-name">
					<xsl:value-of select="$name"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="type">
				<xsl:choose>
					<xsl:when test="$mode='client' or $mode='' or not($mode)">
						<xsl:text>button</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>submit</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="$labelId != ''">
				<xsl:attribute name="aria-labelledby">
					<xsl:value-of select="$labelId"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="aria-checked">
				<xsl:choose>
					<xsl:when test="$selected=1">
						<xsl:copy-of select="$t"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="$for!=''">
				<xsl:attribute name="aria-controls">
					<xsl:value-of select="$for"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$mode='dynamic' or $mode='lazy'">
					<xsl:attribute name="data-wc-ajaxalias">
						<xsl:value-of select="$for"/>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="$mode='server'">
					<xsl:attribute name="formnovalidate">
						<xsl:text>formnovalidate</xsl:text>
					</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="self::ui:rowSelection or self::ui:rowExpansion">
					<!--
						TODO: this applies only to WDataTable's intrinsic disabled mode. It is on the list to remove.
					-->	
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="isControl" select="1"/>
						<xsl:with-param name="field" select="parent::ui:table"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="self::ui:selectToggle"><!-- WCollapsibleToggle does not have a disabled state. -->
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="isControl" select="1"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
			<xsl:value-of select="$text"/>
		</button>
	</xsl:template>
</xsl:stylesheet>
