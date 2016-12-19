<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>

	<xsl:template match="ui:datefield">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="pickId">
			<xsl:value-of select="concat($id, '_cal')"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="@readOnly">
				<xsl:variable name="tagName">
					<xsl:choose>
						<xsl:when test="@allowPartial">
							<xsl:text>span</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>time</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:element name="{$tagName}">
					<xsl:call-template name="commonAttributes">
						<xsl:with-param name="class">
							<xsl:text> wc_datero wc_ro</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="roComponentName"/>
					<xsl:choose>
						<xsl:when test="@date">
							<xsl:variable name="datetimeattrib">
								<xsl:choose>
									<xsl:when test="$tagName eq 'span'">
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
				<xsl:variable name="inputId" select="concat($id,'_input')"/>
				<div id="{$id}">
					<xsl:call-template name="commonAttributes">
						<xsl:with-param name="class">
							<xsl:text>wc_input_wrapper</xsl:text>
							<xsl:if test="@allowPartial">
								<xsl:text> wc_datefield_partial</xsl:text>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:if test="@allowPartial">
						<xsl:attribute name="role">
							<xsl:text>combobox</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="aria-autocomplete">
							<xsl:text>list</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="aria-expanded">
							<xsl:text>false</xsl:text>
						</xsl:attribute>
						<xsl:if test="@date">
							<xsl:attribute name="data-wc-value">
								<xsl:value-of select="@date"/>
							</xsl:attribute>
						</xsl:if>
					</xsl:if>
					<xsl:call-template name="ariaLabel"/>
					<xsl:element name="input">
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
						<xsl:attribute name="value">
							<xsl:choose>
								<xsl:when test="@date">
									<xsl:value-of select="@date"/>
								</xsl:when>
								<xsl:when test="@allowPartial">
									<xsl:value-of select="."/>
								</xsl:when>
								<!-- QC157989 -->
								<xsl:otherwise>
									<xsl:value-of select="."/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:choose>
							<xsl:when test="@allowPartial">
								<!-- every input that implements combo should have autocomplete turned off -->
								<xsl:attribute name="autocomplete">
									<xsl:text>off</xsl:text>
								</xsl:attribute>
							</xsl:when>
							<xsl:otherwise>
								<xsl:if test="@min">
									<xsl:attribute name="min">
										<xsl:value-of select="@min"/>
									</xsl:attribute>
								</xsl:if>
								<xsl:if test="@max">
									<xsl:attribute name="max">
										<xsl:value-of select="@max"/>
									</xsl:attribute>
								</xsl:if>
								<xsl:if test="@autocomplete">
									<xsl:attribute name="autocomplete">
										<xsl:value-of select="@autocomplete"/>
									</xsl:attribute>
								</xsl:if>
							</xsl:otherwise>
						</xsl:choose>

						<xsl:call-template name="requiredElement">
							<xsl:with-param name="useNative" select="1"/>
						</xsl:call-template>
						<xsl:if test="@required">
							<xsl:attribute name="placeholder">
								<xsl:text>{{t 'requiredPlaceholder'}}</xsl:text>
							</xsl:attribute>
						</xsl:if>
						<xsl:call-template name="title">
							<xsl:with-param name="contentAfter">
								<xsl:text>{{t 'datefield_title_default'}}</xsl:text>
							</xsl:with-param>
						</xsl:call-template>
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="isControl" select="1"/>
						</xsl:call-template>
					</xsl:element>
					<xsl:if test="@allowPartial">
						<!-- This is the date picker launch control element.
						Calendar needs an ID so that if the date input itself is the target of an AJAX
						"replace" the calendar icon will get cleaned up by our duplicate ID prevention
						logic (assumes the new date field has the same ID which in WComponents is always the case).
						-->
						<button value="{$inputId}" tabindex="-1" id="{$pickId}" type="button" aria-hidden="true" class="wc_wdf_cal wc_btn_icon wc-invite">
							<xsl:call-template name="disabledElement">
								<xsl:with-param name="isControl" select="1"/>
							</xsl:call-template>
						</button>
						<span role="listbox" aria-busy="true"></span>
					</xsl:if>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
