<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!--
		WMenuItem forms part of a single compound widget with the WMenu at its root.

		The transform for WMenuItem. In general this is pretty straightforwards. The menuItem is rendered as a single control.
	-->
	<xsl:template match="ui:menuitem">
		<xsl:variable name="actionType">
			<xsl:choose>
				<xsl:when test="@url">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:when test="@submit">
					<xsl:number value="2"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="role">
			<xsl:choose>
				<xsl:when test="@role">
					<xsl:value-of select="@role"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>menuitem</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<button role="{$role}">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="isControl" select="1"/>
				<xsl:with-param name="class">
					<xsl:text>wc-invite wc-nobutton</xsl:text>
					<xsl:if test="number($actionType) gt 0">
						<xsl:if test="@cancel">
							<xsl:text> wc_btn_cancel</xsl:text>
						</xsl:if>
						<xsl:if test="@unsavedChanges">
							<xsl:text> wc_unsaved</xsl:text>
						</xsl:if>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:attribute name="type">
				<xsl:choose>
					<xsl:when test="number($actionType) eq 2">
						<xsl:text>submit</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>button</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:call-template name="title"/>
			<xsl:choose>
				<xsl:when test="number($actionType) eq 1">
					<xsl:attribute name="data-wc-url">
						<xsl:value-of select="@url"/>
					</xsl:attribute>
					<xsl:if test="@targetWindow">
						<xsl:attribute name="data-wc-window">
							<xsl:value-of select="@targetWindow"/>
						</xsl:attribute>
						<xsl:attribute name="aria-haspopup">
							<xsl:text>true</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:when>
				<xsl:when test="number($actionType) eq 2">
					<xsl:attribute name="name">
						<xsl:value-of select="@id"/>
					</xsl:attribute>
					<xsl:attribute name="value">
						<xsl:text>x</xsl:text>
					</xsl:attribute>
					<!--
						client validation:
						* cancel does not validate; else
						* if validation target is set then validate in that target; else
						* if the menuItem is an ajaxTrigger do not validate.
					-->
					<xsl:choose>
						<xsl:when test="@cancel">
							<xsl:attribute name="formnovalidate">
								<xsl:text>formnovalidate</xsl:text>
							</xsl:attribute>
						</xsl:when>
						<xsl:when test="@validates">
							<xsl:attribute name="data-wc-validate">
								<xsl:value-of select="@validates"/>
							</xsl:attribute>
						</xsl:when>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
			<xsl:if test="@msg">
				<xsl:attribute name="data-wc-btnmsg">
					<xsl:value-of select="@msg"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$role ne 'menuitem'">
				<xsl:attribute name="aria-checked">
					<xsl:choose>
						<xsl:when test="@selected">
							<xsl:text>true</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>false</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="accessKey"/>
			<xsl:apply-templates select="ui:decoratedlabel"/>
		</button>
	</xsl:template>
	
	<!--
		This template is used to determine if a WMenuItem should be selectable based on its selectable property, or ancestry. The params are passed in
		because they are expensive to calculate.
	-->
	<xsl:template name="menuRoleIsSelectable">
		<xsl:param name="type"/>
		<xsl:param name="myAncestorMenu"/>
		<xsl:param name="myAncestorSubmenu"/>
		<xsl:choose>
			<xsl:when test="@selectable eq 'false'">
				<xsl:number value="0"/>
			</xsl:when>
			<xsl:when test="@selectable">
				<xsl:number value="1"/>
			</xsl:when>
			<!-- 
				If we do not have a context menu at all then let the ajax subscriber javascript worry about selection mode based on the transient 
				attribute set from @selectable
			-->
			<xsl:when test="not($myAncestorMenu or $myAncestorSubmenu)">
				<xsl:number value="0"/>
			</xsl:when>
			<!-- from here down we know we have an ancestor menu -->
			<xsl:when test="$myAncestorSubmenu/@selectMode">
				<xsl:number value="1"/>
			</xsl:when>
			<xsl:when test="$myAncestorSubmenu">
				<xsl:number value="0"/>
			</xsl:when>
			<xsl:when test="$myAncestorMenu/@selectMode">
				<xsl:number value="1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:number value="0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
