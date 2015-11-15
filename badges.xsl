<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:variable name="COLOUR.ERROR" select="'#E05D44'"/>
	<xsl:variable name="COLOUR.OK" select="'#97CA00'"/>

	<!-- This template drives the rest of the output -->
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="/pmd">
				<xsl:call-template name="handle_pmd"/>
			</xsl:when>
			<xsl:when test="/checkstyle">
				<xsl:call-template name="handle_checkstyle"/>
			</xsl:when>
			<xsl:when test="/BugCollection">
				<xsl:call-template name="handle_findbugs"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- PMD -->
	<xsl:template name="handle_pmd">

		<!-- Extract Status -->
		<xsl:variable name="info" select="count(//pmd//violation[@priority='5'])" />
		<xsl:variable name="minor" select="count(//pmd//violation[@priority='4'])" />
		<xsl:variable name="major" select="count(//pmd//violation[@priority='3'])" />
		<xsl:variable name="critical" select="count(//pmd//violation[@priority='2'])" />
		<xsl:variable name="blocker" select="count(//pmd//violation[@priority='1'])" />

		<!-- Create Status Text. -->
		<xsl:variable name="status" select="concat('B:', $blocker, ' C:', $critical, ' M:', $major, ' m:', $minor, ' I:', $info)" />

		<!-- Check Status Colour -->
		<xsl:variable name="colour">
		<xsl:choose>
			<xsl:when test="$blocker &gt; 0 or $critical &gt; 0">
				<xsl:value-of select="$COLOUR.ERROR" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$COLOUR.OK" />
			</xsl:otherwise>
		</xsl:choose>
		</xsl:variable>
		
		<!-- Create Badge. -->
		<xsl:call-template name="createsvg">
			<xsl:with-param name="SUBJECT.TEXT" select="'PMD'" />
			<xsl:with-param name="STATUS.COLOUR" select="$colour" />
			<xsl:with-param name="STATUS.TEXT" select="$status" />
		</xsl:call-template>
	</xsl:template>

	<!-- Checkstyle -->
	<xsl:template name="handle_checkstyle">
		<!-- Extract Status -->
		<xsl:variable name="info" select="count(//checkstyle//file//error[@severity='info'])" />
		<xsl:variable name="warning" select="count(//checkstyle//file//error[@severity='warning'])" />
		<xsl:variable name="error" select="count(//checkstyle//file//error[@severity='error'])" />

		<!-- Create Status Text. -->
		<xsl:variable name="status" select="concat('E:', $error, ' W:', $warning, ' I:', $info)" />

		<!-- Check Status Colour -->
		<xsl:variable name="colour">
		<xsl:choose>
			<xsl:when test="$error &gt; 0">
				<xsl:value-of select="$COLOUR.ERROR" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$COLOUR.OK" />
			</xsl:otherwise>
		</xsl:choose>
		</xsl:variable>
		
		<!-- Create Badge. -->
		<xsl:call-template name="createsvg">
			<xsl:with-param name="SUBJECT.TEXT" select="'Checkstyle'" />
			<xsl:with-param name="STATUS.COLOUR" select="$colour" />
			<xsl:with-param name="STATUS.TEXT" select="$status" />
		</xsl:call-template>
	</xsl:template>

	<!-- Findbugs -->
	<xsl:template name="handle_findbugs">
		<!-- Extract Status -->
		<xsl:variable name="low" select="count(//BugCollection//BugInstance[@priority='3'])" />
		<xsl:variable name="medium" select="count(//BugCollection//BugInstance[@priority='2'])" />
		<xsl:variable name="high" select="count(//BugCollection//BugInstance[@priority='1'])" />

		<!-- Create Status Text. -->
		<xsl:variable name="status" select="concat('H:', $high, ' M:', $medium, ' L:', $low)" />
		
		<!-- Check Status Colour -->
		<xsl:variable name="colour">
		<xsl:choose>
			<xsl:when test="$high &gt; 0">
				<xsl:value-of select="$COLOUR.ERROR" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$COLOUR.OK" />
			</xsl:otherwise>
		</xsl:choose>
		</xsl:variable>

				
		<!-- Create Badge. -->
		<xsl:call-template name="createsvg">
			<xsl:with-param name="SUBJECT.TEXT" select="'Findbugs'" />
			<xsl:with-param name="STATUS.COLOUR" select="$colour" />
			<xsl:with-param name="STATUS.TEXT" select="$status" />
		</xsl:call-template>
	</xsl:template>

	<!-- SVG Template. Based on shields.io template. -->
	<xsl:template name="createsvg">
		<xsl:param name="SUBJECT.TEXT" select="'Subject'"/>
		<xsl:param name="STATUS.TEXT" select="'Status'"/>
		<xsl:param name="STATUS.COLOUR" select="$COLOUR.OK"/>
		
		<xsl:variable name="W0" select="string-length($SUBJECT.TEXT) * 8 + 12"/>
		<xsl:variable name="W1" select="string-length($STATUS.TEXT) * 7.5 + 12 "/>
		<xsl:variable name="WT" select="$W0 + $W1"/>

		<xsl:variable name="W2" select="$W0 div 2"/>
		<xsl:variable name="W3" select="$W0 + round($W1 div 2) - 1"/>

		<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="{$WT}" height="20">
			<linearGradient id="smooth" x2="0" y2="100%">
				<stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
				<stop offset="1" stop-opacity=".1"/>
			</linearGradient>

			<mask id="round">
				<rect width="{$WT}" height="20" rx="3" fill="#fff"/>
			</mask>

			<g mask="url(#round)">
				<rect width="{$W0}" height="20" fill="#555"/>
				<rect x="{$W0}" width="{$W1}" height="20" fill="{$STATUS.COLOUR}"/>
				<rect width="{$WT}" height="20" fill="url(#smooth)"/>
			</g>

			<g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="11">
				<text x="{$W2}" y="15" fill="#010101" fill-opacity=".3">
					<xsl:value-of select="$SUBJECT.TEXT"/>
				</text>
				<text x="{$W2}" y="14">
					<xsl:value-of select="$SUBJECT.TEXT"/>
				</text>
				<text x="{$W3}" y="15" fill="#010101" fill-opacity=".3">
					<xsl:value-of select="$STATUS.TEXT"/>
				</text>
				<text x="{$W3}" y="14">
					<xsl:value-of select="$STATUS.TEXT"/>
				</text>
			</g>
		</svg>
	</xsl:template>

</xsl:stylesheet>
