<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:import href="wc.debug.common.bestPracticeHelpers.xsl"/>
	<!--
		Debug infor for ui:table/WTable
	-->
	<xsl:template name="table-debug">
		<xsl:variable name="thisTable" select="."/>
		<xsl:call-template name="debugAttributes"/>
		
		
		<xsl:variable name="hasPages">
			<xsl:if test="ui:pagination">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		
		<xsl:variable name="hasExpandables">
			<xsl:value-of select="count(ui:tbody/ui:tr[ui:subTr or @expandable])"/>
		</xsl:variable>
		
		<xsl:variable name="hasSortable">
			<xsl:value-of select="count(ui:thead/ui:th[@sortable])" />
		</xsl:variable>
		
		<xsl:variable name="hasSelectAll">
			<xsl:if test="ui:rowSelection[@multiple and @selectAll] and ui:tbody//ui:tr[not(@unselectable) and ancestor::ui:table[1]=$thisTable]">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		
		
		<!--ERROR
			None (yet): anything can be added to a table
		-->
		
		<!--WARN 
			Server mode has been removed from WTable so should go away and it is
			lame rather than harmful so has been moved to a debug level message.
		-->
		
		<!--INFO if options turned on which are not implemented such as pagination
			with 1 or fewer pages.
		-->
		<xsl:variable name="infoText">
			<xsl:if test="ui:rowExpansion and $hasExpandables &lt; 1">
				<xsl:text> Row expansion requested but no expandable rows.\n</xsl:text>
			</xsl:if>
			<xsl:if test="$hasExpandables &lt; 1 and ui:rowExpansion/@expandAll">
				<xsl:text> Expand/collapse all controls requested but no expandable rows.\n</xsl:text>
			</xsl:if>
			<xsl:if test="ui:rowSelection">
				<xsl:if test="ui:rowSelection/@selectAll and not(ui:rowSelection/@multiple)">
					<xsl:text> Select all requested with single selection.\n</xsl:text>
				</xsl:if>
				<xsl:if test="count(ui:tbody/ui:tr) = 0 or (count(ui:tbody/ui:tr/ui:subTr)=0 and count(ui:tbody/ui:tr[not(@unselectable)])=0) or count(ui:tbody//ui:tr[not(@unselectable) and ancestor::ui:table[1]=$thisTable]) =0">
					<xsl:text> Row selection requested but no selectable rows.\n</xsl:text>
				</xsl:if>
			</xsl:if>
			<xsl:if test="ui:sort and $hasSortable &lt; 1">
				<xsl:text> Table sorting requested but no sortable columns.</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="$infoText!=''">
			<xsl:call-template name="makeDebugAttrib-debug">
				<xsl:with-param name="name" select="'ata-wc-debuginfo'"/>
				<xsl:with-param name="text" select="$infoText"/>
			</xsl:call-template>
		</xsl:if>
		
		
		<!--DEBUG
			WTable must not be added to any component which:
			* can accept only phrasing content; and
			* can not have interactive content descendants if the table has any 
			  of:
				* pagination and more than one page;
				* rowSelection/@selectAll; or
				* rowExpansion and at least one expandable row; or
				* sort and at least one sortable column.
			In addition we should output an info level message if the table has
			LAME controls or rowSelection with selectAll and submitOnChange.
		-->
		
		<xsl:variable name="lame" select="'server'"/>
		
		<xsl:variable name="lameText">
			<xsl:if test="ui:pagination/@mode=$lame">
				<xsl:text> \nWTable pagination mode is SERVER.\n</xsl:text>
			</xsl:if>
			<xsl:if test="$hasExpandables &gt; 0 and ui:rowExpansion/@mode=$lame">
				<xsl:text> WTable row expansion mode is SERVER.\n</xsl:text>
			</xsl:if>
			<xsl:if test="$hasSortable &gt; 0 and ui:sort/@mode=$lame">
				<xsl:text> WTable sort mode is SERVER.\n</xsl:text>
			</xsl:if>
		</xsl:variable>
		
		<xsl:variable name="otherDebugText">
			<xsl:if test="$lameText!=''">
				<xsl:call-template name="getLameText">
					<xsl:with-param name="otherText" select="$lameText"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:variable>
		
		<xsl:variable name="hasInteractive">
			<xsl:value-of select="$hasPages + $hasExpandables + $hasSortable + $hasSelectAll"/>
		</xsl:variable>
		
		<xsl:call-template name="thisIsNotAllowedHere-debug">
			<xsl:with-param name="testForPhraseOnly" select="1"/>
			<xsl:with-param name="testForNoInteractive">
				<xsl:choose>
					<xsl:when test="$hasInteractive &gt; 0">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="otherDebugText">
				<xsl:value-of select="$otherDebugText"/>
				<xsl:if test="$hasSelectAll=1 and ui:rowSelection/@submitOnChange">
					<xsl:text> WTable has submitOnChange for selecting all rows which may cause usability problems.</xsl:text>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>