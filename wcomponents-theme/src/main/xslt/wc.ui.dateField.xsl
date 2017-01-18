<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	
	<xsl:template match="ui:datefield[@readOnly]" priority="2">
		<xsl:call-template name="readOnlyControl">
			<xsl:with-param name="class">
				<xsl:text>wc_datero wc-ro-input</xsl:text>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
<xsl:template match="ui:datefield[@allowPartial]">
		<div>
			<xsl:call-template name="commonInputWrapperAttributes">
				<xsl:with-param name="class">
					<xsl:text>wc_datefield_partial</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
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
			<xsl:element name="input">
				<xsl:call-template name="wrappedTextInputAttributes">
					<xsl:with-param name="type" select="'text'"/>
				</xsl:call-template>
				<xsl:attribute name="value">
					<xsl:choose>
						<xsl:when test="@date">
							<xsl:value-of select="@date"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="."/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<!-- every input that implements combo should have autocomplete turned off -->
				<xsl:attribute name="autocomplete">
					<xsl:text>off</xsl:text>
				</xsl:attribute>
			</xsl:element>
			<!-- This is the date picker launch control element. -->
			<button value="{concat(@id,'_input')}" tabindex="-1" type="button" aria-hidden="true" class="wc_wdf_cal wc_btn_icon wc-invite">
				<xsl:call-template name="disabledElement">
					<xsl:with-param name="isControl" select="1"/>
				</xsl:call-template>
			</button>
			<span role="listbox" aria-busy="true"></span>
		</div>
	</xsl:template>

	<xsl:template match="ui:datefield">
		<div>
			<xsl:call-template name="commonInputWrapperAttributes"/>
			<xsl:element name="input">
				<xsl:call-template name="wrappedTextInputAttributes">
					<xsl:with-param name="type" select="'date'"/>
				</xsl:call-template>
				<xsl:attribute name="value">
					<xsl:choose>
						<xsl:when test="@date">
							<xsl:value-of select="@date"/>
						</xsl:when>
						<!-- QC157989 Keep bad value because of very bad reasons even though it won't work in browsers. -->
						<xsl:otherwise>
							<xsl:value-of select="."/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
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
				<xsl:if test="not(@date) and normalize-space(.) ne ''">
					<xsl:attribute name="aria-invalid">
						<xsl:text>true</xsl:text>
					</xsl:attribute>
				</xsl:if>
			</xsl:element>
		</div>
	</xsl:template>
</xsl:stylesheet>
