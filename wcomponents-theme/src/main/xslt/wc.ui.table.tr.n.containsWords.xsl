<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--

		TODO: remove this when WFilterControl is no longer part of the Java API

		Test if a string contains all words in any order. You may want to normalize-space
		on the strings before you call this.

		param testString: The string to test (usually a class name or filter list)
		param testWords: A space separated list of words, these are the words we are
		looking for in the testString
		return 1 if testString contains all of the words in testWords, otherwise 0

	<xsl:template name="containsWords">
		<xsl:param name="testString"/>
		<xsl:param name="testWords"/>
		<xsl:choose>
			<xsl:when test="$testString=$testWords">
				<xsl:value-of select="1"/>
			</xsl:when>
			<xsl:when test="not(contains($testWords, ' '))">
				<xsl:call-template name="containsWord">
					<xsl:with-param name="testString" select="$testString"/>
					<xsl:with-param name="testWord" select="$testWords"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="firstWord">
					<xsl:value-of select="substring-before($testWords, ' ')"/>
				</xsl:variable>
				<xsl:variable name="otherWords">
					<xsl:value-of select="normalize-space(substring-after($testWords, ' '))"/>
				</xsl:variable>
				<xsl:variable name="firstWordInString">
					<xsl:call-template name="containsWord">
						<xsl:with-param name="testString" select="$testString"/>
						<xsl:with-param name="testWord" select="$firstWord"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$firstWordInString=1">
						<xsl:call-template name="containsWords">
							<xsl:with-param name="testString" select="$testString"/>
							<xsl:with-param name="testWords" select="$otherWords"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>



		Test if a string contains a word as a word, not a word form or fragment.

		eg testString = "bar foobar"\
		testWord  = "foo" returns 0\
		testWord = "bar" returns 1\
		eg2 testString = "not_met" testWord = "met" returns 0

		param testString: The string to test (usually a class name or filter list)
		param testWord: A single word - that is one which contains no spaces
		return 1 if testString contains testWord, otherwise 0
	<xsl:template name="containsWord">
		<xsl:param name="testString"/>
		<xsl:param name="testWord"/>
		<xsl:choose>
			<xsl:when test="$testString=$testWord">
				<xsl:value-of select="1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="paddedString" select="concat(' ', $testString, ' ')"/>
				<xsl:variable name="paddedWord" select="concat(' ', $testWord, ' ')"/>
				<xsl:choose>
					<xsl:when test="contains($paddedString, $paddedWord)">
						<xsl:value-of select="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	-->
</xsl:stylesheet>
