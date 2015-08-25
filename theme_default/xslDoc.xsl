<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="text" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
    
    <xsl:strip-space elements="*"/>
    
    <xsl:template match="comment()">
        <xsl:if test="starts-with(.,'**')">
            <xsl:variable name="strippedComment" select="translate(substring-after(.,'**'),'`','-')"/>
            <xsl:value-of select="concat($strippedComment,'&#xA;&#xA;')"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="@*|text()|processing-instruction()"/>
    
    <xsl:template match="*">
        <xsl:apply-templates/>
    </xsl:template>
    
</xsl:stylesheet>