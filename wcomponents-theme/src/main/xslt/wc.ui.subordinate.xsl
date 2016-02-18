<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.root.xsl"/>
	<!--
		Transforms for WSubordinateControl. These components have no in-place artefacts in the UI.
		
		Theme developer information
		
		Subordinate controls build javascript functions directly from the XSLT. These functions
		are then stored as JSON objects and invoked as necessary from wc.ui.subordinate.js.
		It is extremely unlikely that you will need to override any of this and it should never
		be excluded.
		
		
		key conditionKey
		Key to find radio buttons which match a ui:subordinate ui:condition. Used by
		ui:subordinate because a radio buttons name is not the same as its id.
		-->
	<xsl:key name="conditionKey" match="//ui:radiobutton" use="@id"/>
	<!--
		Template match="ui:subordinate|ui:componentGroup|ui:target|ui:onTrue|ui:onFalse|ui:and|ui:or"
		 Null template to prevent inline UI artefacts.
	-->
	<xsl:template match="ui:subordinate|ui:componentGroup|ui:target|ui:onTrue|ui:onFalse|ui:and|ui:or|ui:not"/>
	<!--
		Template match="ui:subordinate" mode="JS"
		Template to output JSON objects required to wire up subordinate controls.
-->
	<xsl:template match="ui:subordinate" mode="JS">
		<xsl:text>{ id: '</xsl:text>
		<xsl:value-of select="@id"/>
		<xsl:text>', test: function (test){ return </xsl:text>
		<xsl:apply-templates select="ui:and|ui:or|ui:not|ui:condition" mode="subordinate"/>
		<xsl:text>;}, onTrue: [</xsl:text>
		<xsl:apply-templates select="ui:onTrue" mode="JS"/>
		<xsl:text>], onFalse: [</xsl:text>
		<xsl:apply-templates select="ui:onFalse" mode="JS"/>
		<xsl:text>], controllers: [</xsl:text>
		<xsl:apply-templates select="descendant::ui:condition" mode="ids"/>
		<xsl:text>]}</xsl:text>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
	<!--
		Template match="ui:and|ui:or|ui:condition|ui:not" mode="subordinate"

		Combination and condition operators in subordinate controls. ui:and, ui:or and
		ui:not map directly to normal JavaScript operators &amp;&amp;, ||, !. Each
		of these and ui:condition then create a paranthetical.
-->
	<xsl:template match="ui:and|ui:or|ui:condition|ui:not" mode="subordinate">
		<xsl:if test="preceding-sibling::ui:and|preceding-sibling::ui:or|preceding-sibling::ui:condition|preceding-sibling::ui:not">
			<xsl:choose>
				<xsl:when test="parent::ui:or">
					<xsl:text> || </xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text> &amp;&amp; </xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="self::ui:not | self::ui:and | self::ui:or">
				<xsl:if test="self::ui:not">
					<xsl:text>!</xsl:text>
				</xsl:if>
				<xsl:text>(</xsl:text>
				<xsl:apply-templates mode="subordinate"/>
				<xsl:text>)</xsl:text>
			</xsl:when>
			<xsl:when test="self::ui:condition">
				<!-- Escape backslashes because otherwise javascript will eat them all up -->
				<xsl:variable name="valSlashEscaped">
					<xsl:call-template name="replaceString">
						<xsl:with-param name="text" select="@value"/>
						<xsl:with-param name="replace" select="'\'"/>
						<xsl:with-param name="with" select="'\\'"/>
					</xsl:call-template>
				</xsl:variable>
				<!-- Escape single quotes because otherwise our string literals will be malformed -->
				<xsl:variable name="valEscaped">
					<xsl:call-template name="replaceString">
						<xsl:with-param name="text" select="$valSlashEscaped"/>
						<xsl:with-param name="replace">
							<xsl:text>'</xsl:text>
						</xsl:with-param>
						<xsl:with-param name="with">
							<xsl:text>\'</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
				<xsl:text>test('</xsl:text>
				<xsl:value-of select="@controller"/>
				<xsl:text>', '</xsl:text>
				<xsl:value-of select="$valEscaped"/>
				<xsl:if test="@operator">
					<xsl:text>', '</xsl:text>
					<xsl:value-of select="@operator"/>
				</xsl:if>
				<xsl:text>')</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<!--
		Creates a comma separated list of JSON objects with the following properties:
		name: the componentGroup id
		identifiers: an array of ui:components

		TODO: consider setting ANT properties for the property keys to prevent synchronization
		errors if these ever change (low).
 -->
	<xsl:template match="ui:componentGroup" mode="JS">
		<xsl:text>{"name":"</xsl:text>
		<xsl:value-of select="@id"/>
		<xsl:text>","identifiers":[</xsl:text>
		<xsl:apply-templates select="ui:component"/>
		<xsl:text>]}</xsl:text>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
	<!--
		Each component in a ui:componentGroup, output as a comma separated list of
		quoted ids for use in the componentGroup array.
-->
	<xsl:template match="ui:component">
		<xsl:text>"</xsl:text>
		<xsl:value-of select="@id"/>
		<xsl:text>"</xsl:text>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
	<!--
		Outputs a comma separated list of quoted ids. Used to produce the controllers
		array in the transform for ui:subordinate.
	-->
	<xsl:template match="ui:condition" mode="ids">
		<xsl:text>'</xsl:text>
		<xsl:value-of select="@controller"/>
		<xsl:text>'</xsl:text>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
	<!--
			Outputs a javascript object:

		[type] the action of the condition type

		[targets] an array of targets.
-->
	<xsl:template match="ui:onTrue|ui:onFalse" mode="JS">
		<xsl:text>{type:'</xsl:text>
		<xsl:value-of select="@action"/>
		<xsl:text>', targets:[</xsl:text>
		<xsl:apply-templates select="ui:target" mode="subordinate"/>
		<xsl:text>]}</xsl:text>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
	<!--
		Outputs a javascript object:

		[id] the id of the ui:target (the target element, this is not an xs:ID

		[groupId] the groupId for the target, this is set if the subordinate target
		is a ui:componentGroup.
-->
	<xsl:template match="ui:target" mode="subordinate">
		<xsl:text>{id:'</xsl:text>
		<xsl:value-of select="@id"/>
		<xsl:text>', groupId:'</xsl:text>
		<xsl:value-of select="@groupId"/>
		<xsl:text>'}</xsl:text>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
