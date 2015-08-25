<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="../../../xslt/wc.constants.xsl"/>
	<xsl:import href="analytics.tracking.xsl"/>
	
	
	<xsl:template name="analytics_tableName">
		<xsl:choose>
			<xsl:when test="@caption and @caption !=''">
				<xsl:value-of select="@caption"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@id"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!--
		Simple tracking for WTable (ui:table) - used when no tracking component for the table is present.
	-->
	<xsl:template match="ui:table[@track]" mode="analytics">
		<xsl:if test="not(key('analytics_trackingKey',@id))">
			<xsl:variable name="tableName">
				<xsl:call-template name="analytics_tableName"/>
			</xsl:variable>
			<xsl:call-template name="analytics_tableHelper">
				<xsl:with-param name="name" select="$tableName"/>
				<xsl:with-param name="mode">
					<xsl:choose>
						<xsl:when test="@track">
							<xsl:number value="1"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:number value="0"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- 
		Complex tracking info component for WTable (ui:table) and its descendants ui:pagination, ui:rowExpansion,
		ui:rowSelection, ui:tr (selectable and expandable).
	-->
	<xsl:template match="ui:table" mode="analytics_tracking">
		<xsl:param name="name"/>
		<xsl:param name="cat"/>
		<xsl:param name="params"/>
		
		<xsl:variable name="tableName">
			<xsl:choose>
				<xsl:when test="$name != @id">
					<xsl:value-of select="$name"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="analytics_tableName"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:call-template name="analytics_tableHelper">
			<xsl:with-param name="name" select="$tableName"/>
			<xsl:with-param name="cat" select="$cat"/>
			<xsl:with-param name="params" select="$params"/>
			<xsl:with-param name="mode" select="2"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="ui:rowSelection" mode="analytics_tracking">
		<xsl:param name="name"/>
		<xsl:if test="@selectAll">
			<!-- set up a click tracker for select all controls -->
			<xsl:variable name="tbodyId">
				<xsl:value-of select="concat(../@id,'${wc.ui.table.id.body.suffix}')"/>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="@selectAll='text'">
					<xsl:call-template name="analytics_clickTrackingHelper">
						<xsl:with-param name="for" select="concat($tbodyId,'${wc.common.toggles.id.select}')"/>
						<xsl:with-param name="name" select="concat($name,' select all rows')"/>
					</xsl:call-template>
					<xsl:text>,</xsl:text>
					<xsl:call-template name="analytics_clickTrackingHelper">
						<xsl:with-param name="for" select="concat($tbodyId,'${wc.common.toggles.id.deselect}')"/>
						<xsl:with-param name="name" select="concat($name,' deselect all rows')"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="analytics_clickTrackingHelper">
						<xsl:with-param name="for" select="concat($tbodyId,'${wc.ui.selectToggle.id.suffix}')"/>
						<xsl:with-param name="name" select="concat($name,' select or deselect all rows')"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	<xsl:template match="ui:rowExpansion" mode="analytics_tracking">
		<xsl:param name="name"/>
		<xsl:if test="@expandAll">
			<!-- set up a click tracker for select all controls -->
			<xsl:variable name="tableId">
				<xsl:value-of select="../@id"/>
			</xsl:variable>
			<xsl:call-template name="analytics_clickTrackingHelper">
				<xsl:with-param name="for" select="concat($tableId,'${wc.common.toggles.id.expand}')"/>
				<xsl:with-param name="name" select="concat($name,' expand all rows')"/>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="analytics_clickTrackingHelper">
				<xsl:with-param name="for" select="concat($tableId,'${wc.common.toggles.id.collapse}')"/>
				<xsl:with-param name="name" select="concat($name,' collapse all rows')"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="ui:pagination" mode="analytics_tracking">
		<xsl:param name="name"/>
		<xsl:call-template name="analytics_clickTrackingHelper">
			<xsl:with-param name="for" select="concat(../@id, '.page')"/>
			<xsl:with-param name="name" select="concat($name,' change page')"/>
		</xsl:call-template>
		<xsl:call-template name="analytics_clickTrackingHelper">
			<xsl:with-param name="for" select="concat(../@id,'.pagination.1')"/>
			<xsl:with-param name="name" select="concat($name,' go to first page')"/>
		</xsl:call-template>
		<xsl:call-template name="analytics_clickTrackingHelper">
			<xsl:with-param name="for" select="concat(../@id,'.pagination.2')"/>
			<xsl:with-param name="name" select="concat($name,' go to previous page')"/>
		</xsl:call-template>
		<xsl:call-template name="analytics_clickTrackingHelper">
			<xsl:with-param name="for" select="concat(../@id,'.pagination.3')"/>
			<xsl:with-param name="name" select="concat($name,' go to next page')"/>
		</xsl:call-template>
		<xsl:call-template name="analytics_clickTrackingHelper">
			<xsl:with-param name="for" select="concat(../@id,'.pagination.4')"/>
			<xsl:with-param name="name" select="concat($name,' go to last page')"/>
		</xsl:call-template>
	</xsl:template>
	<!--
		click tracking for row expansion -->
	<xsl:template match="ui:tr" mode="analytics_trackingExpansion">
		<xsl:param name="tableId"/>
		<xsl:param name="name"/>
		<xsl:call-template name="analytics_clickTrackingHelper">
			<xsl:with-param name="for" select="concat($tableId,'-',@rowIndex, '${wc.ui.table.rowExpansion.id.suffix}')"/>
			<xsl:with-param name="name" select="concat($name,' expand row ', @rowIndex)"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- click tracking for row selection -->
	<xsl:template match="ui:tr" mode="analytics_trackingSelection">
		<xsl:param name="tableId"/>
		<xsl:param name="name"/>
		<xsl:call-template name="analytics_clickTrackingHelper">
			<xsl:with-param name="for" select="concat($tableId,'-',@rowIndex)"/>
			<xsl:with-param name="name" select="concat($name,' select or deselect row ', @rowIndex)"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- click tracking for sort -->
	<xsl:template match="ui:th" mode="analytics_trackingSort">
		<xsl:param name="name"/>
		<xsl:if test="@sortable">
			<xsl:variable name="tableId" select="../../@id"/>
			<xsl:variable name="sortControl" select="../../ui:sort"/>
			<xsl:variable name="sortDesc" select="$sortControl/@descending"/>
			<xsl:variable name="sortCol" select="$sortControl/@col"/>
			<xsl:variable name="isSorted">
				<xsl:choose>
					<xsl:when test="position()-1 = $sortCol">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="isDescSort">
				<xsl:choose>
					<xsl:when test="$isSorted=0 or not($sortDesc=$t)">
						<xsl:number value="0"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="1"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:variable name="id">
				<xsl:value-of select="concat($tableId,'${wc.ui.table.id.sort.suffix}',position())"/>
				<xsl:if test="$isDescSort=1 and not($isSorted=1)">
					<xsl:text>${wc.ui.table.id.sort.suffix.extension}</xsl:text>
				</xsl:if>
			</xsl:variable>
			
			<xsl:variable name="text">
				<xsl:value-of select="ui:decoratedLabel"/>
			</xsl:variable>
			
			<xsl:call-template name="analytics_clickTrackingHelper">
				<xsl:with-param name="for" select="$id"/>
				<xsl:with-param name="name" select="concat($name,' sort column labelled ', $text)"/>
			</xsl:call-template>
		</xsl:if>
		
	</xsl:template>
	
	<!-- NOTE:
		Use the mode rather than the presence of @tracking to determine what kind of 'I was here' tracking is present.
		This is because we do not want any element tracking if the table has neither a tracking element for it nor the 
		track attribute set.
		Modes are
			1. Simple I was here tracking (@track) plus any requi plus any required click tracking
			2. Complex I was here tracking using ui:tracking plus any required click tracking
			0. Click tracking only
	-->
	<xsl:template name="analytics_tableHelper">
		<xsl:param name="name"/>
		<xsl:param name="cat"/>
		<xsl:param name="params"/>
		<xsl:param name="mode"/>
		
		<xsl:choose>
			<xsl:when test="$mode=1">
				<!-- 'I am here' tracking - simple -->
				<xsl:call-template name="analytics_quickTrack">
					<xsl:with-param name="clickable" select="0"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$mode=2">
				<!-- 'I am here' tracking - complex -->
				<xsl:call-template name="analytics_trackingHelper">
					<xsl:with-param name="for" select="@id"/>
					<xsl:with-param name="name" select="$name"/>
					<xsl:with-param name="cat" select="$cat"/>
					<xsl:with-param name="params" select="$params"/>
					<xsl:with-param name="forElement" select="."/>
					<xsl:with-param name="isClickable" select="0"/>
				</xsl:call-template>
			</xsl:when>
			<!-- no "I was here" tracking unless tracking explicitly turned on -->
		</xsl:choose>
		
		<!-- click tracking -->
		<xsl:if test="ui:rowExpansion or ui:rowSelection or ui:pagination or ui:sort">
			<xsl:variable name="hasSelection">
				<xsl:choose>
					<xsl:when test="ui:rowSelection and ui:tbody/ui:tr[not(@unselectable)]">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="hasSelectAll">
				<xsl:choose>
					<xsl:when test="hasSelection=1 and ui:rowSelection[@selectAll]">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="hasPagination">
				<xsl:choose>
					<xsl:when test="ui:pagination">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="hasExpansion">
				<xsl:choose>
					<xsl:when test="ui:rowExpansion and ui:tbody/ui:tr/ui:subTr">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="hasExpandAll">
				<xsl:choose>
					<xsl:when test="hasExpansion=1 and ui:rowExpansion[@expandAll]">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="hasSort">
				<xsl:choose>
					<xsl:when test="ui:sort and ui:thead/ui:th[@sortable]">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:if test="$hasExpansion + $hasPagination + $hasSelection + $hasSort &gt; 0">
				<!-- click tracking -->
				<xsl:if test="$hasSelectAll=1">
					<xsl:apply-templates select="ui:rowSelection" mode="analytics_tracking">
						<xsl:with-param name="name" select="$name"/>
					</xsl:apply-templates>
				</xsl:if>
				<xsl:if test="$hasExpandAll=1">
					<xsl:apply-templates select="ui:rowExpansion" mode="analytics_tracking">
						<xsl:with-param name="name" select="$name"/>
					</xsl:apply-templates>
				</xsl:if>
				<xsl:if test="$hasPagination=1">
					<!-- we need to track each of the pagination controls -->
					<xsl:apply-templates select="ui:pagination" mode="analytics_tracking">
						<xsl:with-param name="name" select="$name"/>
					</xsl:apply-templates>
				</xsl:if>
				<xsl:variable name="tableId" select="@id"/>
				<xsl:if test="$hasExpansion =1">
					<xsl:apply-templates select="ui:tbody//ui:tr[ui:subTr and ancestor::ui:table[1]/@id = $tableId]" mode="analytics_trackingExpansion">
						<xsl:with-param name="tableId" select="$tableId"/>
						<xsl:with-param name="name" select="$name"/>
					</xsl:apply-templates>
				</xsl:if>
				<xsl:if test="$hasSelection=1">
					<xsl:apply-templates select="ui:tbody//ui:tr[not(@unselectable) and ancestor::ui:table[1]/@id = $tableId]" mode="analytics_trackingSelection">
						<xsl:with-param name="tableId" select="$tableId"/>
						<xsl:with-param name="name" select="$name"/>
					</xsl:apply-templates>
				</xsl:if>
				<xsl:if test="$hasSort = 1">
					<xsl:apply-templates select="ui:thead/ui:th" mode="analytics_trackingSort">
						<xsl:with-param name="tableId" select="$tableId"/>
						<xsl:with-param name="name" select="$name"/>
					</xsl:apply-templates>
				</xsl:if>
			</xsl:if>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>