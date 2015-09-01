<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		The transform for optGroups within a multiSelectPair option list.
		
		param applyWhich:
			Use: "selected", "unselected" or "all"; default "all"
			
			This parameter indicates which options in the optGroup should be included in
			apply-templates. It will depend upon whether we are building the unselected
			list, the selected list or the reference list.
		
		param readOnly: the read only state of the parent multiSelectPair
	-->
	<xsl:template match="ui:optgroup" mode="multiselectPair">
		<xsl:param name="applyWhich" select="'all'"/>
		<xsl:param name="readOnly"/>
		<xsl:choose>
			<xsl:when test="$readOnly!=1">
				<optgroup label="{@label}">
					<xsl:choose>
						<xsl:when test="$applyWhich='selected'">
							<xsl:apply-templates select="ui:option[@selected]" mode="multiselectPair"/>
						</xsl:when>
						<xsl:when test="$applyWhich='unselected'">
							<xsl:apply-templates select="ui:option[not(@selected)]" mode="multiselectPair"/>
						</xsl:when>
						<xsl:otherwise>
							<!--the order list comes here -->
							<xsl:apply-templates mode="multiselectPair"/>
						</xsl:otherwise>
					</xsl:choose>
				</optgroup>
			</xsl:when>
			<xsl:otherwise>
				<li class="wc_optgroup">
					<xsl:value-of select="@label"/>
				</li>
				<xsl:apply-templates select="ui:option[@selected]" mode="multiselectPair">
					<xsl:with-param name="readOnly" select="$readOnly"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
