<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:import href="wc.debug.common.bestPracticeHelpers.xsl"/>
	
<!--
Diagnostic information for submenu
	
	There are several contraints on WSubMenus many of which are constraints on the
	labelling element: wc.ui.decoratedLabel.xsl.
	
	One opaque (to the JAVA api) issue is whether a WSubMenu with its open property
	set true will actually render as open on page load. This is determined by the
	menu implementation and is calculated in the helper "stickyOpen" in wc.ui.menu.n.stickyOpen.xsl.
	
	A WSubMenu which cannot be displayed open on page load cannot be used in mode 
	SERVER and is therefore an ERROR whereas a submenu which is able to be displayed
	open on load can be used but may cause poor performance or other usability issues
	so is a WARNING.
	
	ERROR states
	The WSubmenu should show an ERROR diagnostic if:
		1 the WDecoratedLabel used to create the WSubMenu label contains interactive content; or
		2 the menu does not support sticky open and the mode is SERVER.
	
	WARNING states
	The WSubmenu should show a WARNING diagnostic if the ancestor menu does 
	support sticky open and the mode is SERVER.
-->
	<xsl:template name="submenu-debug">
		<xsl:param name="stickyOpen"/>
		<xsl:if test="$isDebug=1">
			<xsl:call-template name="debugAttributes"/>
			<!--ERRORS
					We always need to do the nested content test on the WDecoratedLabel
					Which is why we cannot pull the error testing into a choose based
					on stickyOpen.
				-->
			<xsl:variable name="modeErrorText">
				<xsl:if test="$stickyOpen=0 and @mode='server'">
					<xsl:text>This WSubMenu cannot be opened and may cause unexpected form submissions on hover.</xsl:text>
					<xsl:choose>
						<xsl:when test="@mode='server'">
							<xsl:text> Do not use Mode.SERVER for WSubMenus in this WMenu Type.</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text> Ensure this WSubMenu has at least one visible child.</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
			</xsl:variable>
			<xsl:call-template name="nesting-debug">
				<xsl:with-param name="testInteractive" select="1"/>
				<xsl:with-param name="el" select="ui:decoratedLabel"/>
				<xsl:with-param name="otherErrorText" select="$modeErrorText"/>
			</xsl:call-template>
			<!--WARNINGS-->
			<xsl:if test="$stickyOpen=1 and @mode='server'">
				<xsl:choose>
					<xsl:when test="@mode='server'">
						<xsl:call-template name="lameMode">
							<xsl:with-param name="level" select="'data-wc-debugwarn'"/>
							<xsl:with-param name="otherText">
								<xsl:text> Opening this WSubMenu will cause a trip to the server. Consider using Mode.DYNAMIC instead.</xsl:text>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="makeDebugAttrib-debug">
							<xsl:with-param name="name" select="'data-wc-debugwarn'"/>
							<xsl:with-param name="text">
								<xsl:text>Opening this WSubMenu will cause a trip to the server. Ensure this WSubMenu has at least one visible child</xsl:text>
								<xsl:if test="not(@disabled)">
									<xsl:text>, disable this WSubMenu</xsl:text>
								</xsl:if>
								<xsl:text> or use setVisible(false) on this WSubMenu in this state.</xsl:text>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>