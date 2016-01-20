<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.missingLabel.xsl"/>
	<xsl:import href="wc.common.title.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		A dateField is a compound control consisting of a text input and a button used
		to launch a date picker calendar. The text input allows for typeahead to
		select a date as the user types and for the following shortcuts:
		
			* t = today
			* y = yesterday
			* m = tomorrow
			* [+|-][1-9][0-9]\* increment/decrement today's date by the number of days
		
		We do not implement HTML input element types for dates (date, dateTime
		etc) as their implementation is patchy and no current browser does a good
		enough job on the date pickers. In addition the date format enforced in the
		HTML5 date inputs is (whilst eminently reasonable and unambiguous) not one
		which is commonly used by real people.
		
		The picker calendar is built in JavaScript based on an XML template. It is not
		transformed here. A single calendar is used for every dateField in a form by
		attachment.
		
	-->
	<xsl:template match="ui:dateField">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="pickId">
			<xsl:value-of select="concat('${wc.ui.dateField.id.prefix.picker}', $id)"/>
		</xsl:variable>
		<xsl:variable name="isError" select="key('errorKey',$id)"/>
		<xsl:variable name="myLabel" select="key('labelKey',$id)[1]"/>
		<xsl:choose>
			<xsl:when test="@readOnly">
				<xsl:variable name="tagName">
					<xsl:choose>
						<xsl:when test="@allowPartial">
							<xsl:text>span</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>${wc.dom.html5.element.time}</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:element name="{$tagName}">
					<xsl:call-template name="commonAttributes"/>
					<xsl:attribute name="class">
						<xsl:call-template name="commonClassHelper"/>
						<xsl:text> wc_datero wc_ro</xsl:text>
					</xsl:attribute>
					<xsl:if test="$myLabel">
						<xsl:attribute name="aria-labelledby">
							<xsl:value-of select="$myLabel/@id"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="@date">
							<xsl:variable name="datetimeattrib">
								<xsl:choose>
									<xsl:when test="$tagName='span'">
										<xsl:text>data-wc-value</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>datetime</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:attribute name="{$datetimeattrib}">
								<xsl:value-of select="@date"/>
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="normalize-space(.)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="inputId">
					<xsl:value-of select="concat($id,'${wc.ui.dateField.id.input.suffix}')"/>
				</xsl:variable>
				<xsl:if test="not($myLabel)">
					<xsl:call-template name="checkLabel">
						<xsl:with-param name="for" select="$inputId"/>
						<xsl:with-param name="force" select="1"/>
					</xsl:call-template>
				</xsl:if>
				<!-- NOTE:
					it would be nice to use a span for wrapping a date control which really is just an input and a button. 
					The suggestion list used in WComponents is, however, a ul so the wrapper for date fields is a div by
					necessity which changes its content type to non-phrase.
				-->
				<div id="{$id}">
					<xsl:call-template name="hideElementIfHiddenSet"/>
					<xsl:call-template name="ajaxTarget">
						<xsl:with-param name="live" select="'off'"/>
					</xsl:call-template>
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="isControl" select="0"/>
					</xsl:call-template>
					<xsl:call-template name="makeCommonClass"/>
					<xsl:attribute name="role">
						<xsl:text>combobox</xsl:text>
					</xsl:attribute>
					<xsl:call-template name="requiredElement">
						<xsl:with-param name="useNative" select="0"/>
					</xsl:call-template>
					<xsl:attribute name="aria-autocomplete">
						<xsl:text>both</xsl:text>
					</xsl:attribute>
					<xsl:call-template name="disabledElement"/>
					<xsl:attribute name="aria-expanded">
						<xsl:text>false</xsl:text>
					</xsl:attribute>
					<xsl:if test="$isError">
						<xsl:call-template name="invalid"/>
					</xsl:if>
					<xsl:attribute name="data-wc-name">
						<xsl:value-of select="@id"/>
						<xsl:text>${wc.ui.dateField.name.suffix}</xsl:text>
					</xsl:attribute>
					<xsl:if test="@date">
						<xsl:attribute name="data-wc-value">
							<xsl:value-of select="@date"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="not($myLabel)">
						<xsl:call-template name="ariaLabel"/>
					</xsl:if>
					<xsl:element name="input">
						<!-- type is currently a property whilst HTML5 date fields mature -->
						<xsl:attribute name="type">
							<xsl:choose>
								<xsl:when test="@allowPartial">
									<xsl:text>text</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>date</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="id">
							<xsl:value-of select="$inputId"/>
						</xsl:attribute>
						<xsl:attribute name="name">
							<xsl:value-of select="$id"/>
						</xsl:attribute>
						<xsl:if test="not(@date)">
							<xsl:attribute name="value">
								<xsl:value-of select="."/>
							</xsl:attribute>
						</xsl:if>

						<!-- every input that implements combo should have autocomplete turned off -->
						<xsl:attribute name="autocomplete">
							<xsl:text>off</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="aria-owns">
							<xsl:value-of select="$pickId"/>
						</xsl:attribute>
						<xsl:call-template name="title">
							<xsl:with-param name="contentAfter" select="$$${wc.ui.dateField.i18n.title.default}"/>
						</xsl:call-template>

						<xsl:if test="@min">
							<xsl:choose>
								<xsl:when test="@allowPartial">
									<xsl:attribute name="${wc.common.attrib.min}">
										<xsl:value-of select="@min"/>
									</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="min">
										<xsl:value-of select="@min"/>
									</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
						<xsl:if test="@max">
							<xsl:choose>
								<xsl:when test="@allowPartial">
									<xsl:attribute name="${wc.common.attrib.max}">
										<xsl:value-of select="@max"/>
									</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="max">
										<xsl:value-of select="@max"/>
									</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
						<xsl:if test="@required">
							<xsl:attribute name="placeholder">
								<xsl:value-of select="$$${wc.common.i18n.requiredPlaceholder}"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:call-template name="ajaxController"/>
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="isControl" select="1"/>
						</xsl:call-template>
					</xsl:element>
					<!-- This is the date picker launch control element. -->
					<xsl:element name="button">
						<xsl:attribute name="value">
							<xsl:value-of select="$inputId"/>
						</xsl:attribute>
						<xsl:attribute name="tabindex">
							<xsl:text>-1</xsl:text>
						</xsl:attribute>
						<!--
							Calendar needs an ID so that if the date input itself is the target of an AJAX
							"replace" the calendar icon will get cleaned up by our duplicate ID prevention
							logic (assumes the new date field has the same ID which in WComponents is always the case).
						-->
						<xsl:attribute name="id">
							<xsl:value-of select="$pickId"/>
						</xsl:attribute>
						<xsl:attribute name="type">
							<xsl:text>button</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="aria-haspopup">
							<xsl:copy-of select="$t"/>
						</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:text>wc_wdf_cal wc_btn_nada</xsl:text>
						</xsl:attribute>
						<xsl:call-template name="hideElementIfHiddenSet"/>
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="isControl" select="1"/>
						</xsl:call-template>
						<xsl:attribute name="title">
							<xsl:value-of select="$$${wc.ui.dateField.i18n.calendarLaunchButton}"/>
						</xsl:attribute>
					</xsl:element>
					<xsl:element name="ul">
						<xsl:attribute name="role">
							<xsl:text>listbox</xsl:text>
						</xsl:attribute>
						<xsl:element name="li"><!-- a listbox must contain an option -->
							<xsl:attribute name="role">option</xsl:attribute>
						</xsl:element>
					</xsl:element>
				</div>
				<xsl:call-template name="inlineError">
					<xsl:with-param name="errors" select="$isError"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
