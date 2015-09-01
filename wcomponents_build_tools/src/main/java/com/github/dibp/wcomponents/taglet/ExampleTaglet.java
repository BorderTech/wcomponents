package com.github.dibp.wcomponents.taglet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * This taglet is used to highlight java source code snippets.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ExampleTaglet implements Taglet
{
    /** The taglet name. */
    private static final String NAME = "@example";

    /** The pattern to use to determine whether the example code has been wrapped in PRE tags. */
    private static final Pattern PRE_TAG_PATTERN = Pattern.compile("(<pre>|<PRE>).*(</pre>|</PRE>)", Pattern.DOTALL);

    /** The set of Java keywords, taken from http://java.sun.com/docs/books/tutorial/java/nutsandbolts/_keywords.html . */
    private static final Set<String> KEYWORDS = new HashSet<String>(Arrays.asList(
       "abstract", "continue", "for", "new", "switch",
       "assert", "default", "goto", "package", "synchronized",
       "boolean", "do", "if", "private", "this",
       "break", "double", "implements", "protected", "throw",
       "byte", "else", "import", "public", "throws",
       "case", "enum", "instanceof", "return", "transient",
       "catch", "extends", "int", "short", "try",
       "char", "final", "interface", "static", "void",
       "class", "finally", "long", "strictfp", "volatile",
       "const", "float", "native", "super", "while"
    ));

    /**
     * @return the name of this custom tag.
     */
    public String getName()
    {
        return NAME;
    }

    /**
     * @return false - this tag can not be used in field documentation.
     */
    public boolean inField()
    {
        return false;
    }

    /**
     * @return true - this tag can be used in constructor documentation.
     */
    public boolean inConstructor()
    {
        return true;
    }

    /**
     * @return true - this tag can be used in method documentation.
     */
    public boolean inMethod()
    {
        return true;
    }

    /**
     * @return false - this tag can not be used in overview documentation.
     */
    public boolean inOverview()
    {
        return false;
    }

    /**
     * @return true - this tag can be used in package documentation.
     */
    public boolean inPackage()
    {
        return true;
    }

    /**
     * @return true - this tag can not be used in type (class or interface) documentation.
     */
    public boolean inType()
    {
        return true;
    }

    /**
     * @return false - this is a block tag.
     */
    public boolean isInlineTag()
    {
        return false;
    }

    /**
     * Registers this Taglet.
     * @param tagletMap the map to register this tag to.
     */
    public static void register(final Map tagletMap)
    {
        ExampleTaglet tag = new ExampleTaglet();
        Taglet t = (Taglet) tagletMap.get(tag.getName());

        if (t != null)
        {
            tagletMap.remove(tag.getName());
        }

        tagletMap.put(tag.getName(), tag);
    }

    /**
     * Given the <code>Tag</code> representation of this custom tag, return its string representation.
     *
     * @param tag the <code>Tag</code> representation of this custom tag.
     * @return the String representation of this custom tag.
     */
    public String toString(final Tag tag)
    {
        return "<DT><B>Example:</B><DD>"
               + "<div style='white-space: pre; font: 12px/1.25 monospace; border: 1px solid black; padding: 0.25em;'>"
               + highlight(tag.text()) + "</div></DD>\n";
    }

    /**
     * Given an array of <code>Tag</code>s representing this custom tag, return its string representation.
     *
     * @param tags the array of <code>Tag</code>s representing of this custom tag.
     * @return the String representation of this custom tag.
     */
    public String toString(final Tag[] tags)
    {
        if (tags.length == 0)
        {
            return null;
        }

        String result = "\n<DT><B>Example:</B><DD>";

        for (int i = 0; i < tags.length; i++)
        {
            result += "<div style='white-space: pre; font: 12px/1.25 monospace; border: 1px solid black; padding: 0.25em;'>";
            result += highlight(tags[i].text());
            result += "</div>";
        }

        return result + "</DD>\n";
    }

    /**
     * Attempts to syntax highlight the Java source.
     * @param source the source to highlight
     * @return the highlighted source, or the original source on error.
     */
    private String highlight(final String source)
    {
        String stripped = source.trim();

        // Strip off any pre tag (if present)
        if (PRE_TAG_PATTERN.matcher(stripped).matches())
        {
            stripped = stripped.substring(5, stripped.length() - 6);
        }

        try
        {
            return doHighlight(stripped);
        }
        catch (Exception e)
        {
            return source;
        }
    }

    /**
     * Performs syntax highlighting of the given Java source.
     * @param source the source to highlight
     * @return the highlighted source.
     */
    private String doHighlight(final String source)
    {
        StringBuffer buf = new StringBuffer();
        String[] lines = source.split("\n");
        int lineCount=lines.length;
        boolean inComment = false;
        boolean inJavadoc = false;

        for (int i = 0; i < lineCount; i++)
        {
            boolean inString = false;
            String line = lines[i];
            int textPos = 0;
            char lastChar=' ';

            for (int j = 0; j < line.length(); j++)
            {
                switch (line.charAt(j))
                {
                    case '"':
                    {
                        if (!inComment && !inJavadoc)
                        {
                            if (!inString)
                            {
                                inString = true;
                            }
                            else if (inString && lastChar != '\'')
                            {
                                inString = false;
                            }
                        }

                        break;
                    }
                    case '*':
                    {
                        if (lastChar == '/' && !inComment && !inJavadoc && !inString)
                        {
                            parseRow(buf, line.substring(textPos, j - 1));
                            textPos = j - 1;

                            // Start of a multi-line comment
                            // peek at next char for javadoc comment
                            if (j + 1 < line.length() && line.charAt(j + 1) == '*')
                            {
                                inJavadoc = true;
                            }
                            else
                            {
                                inComment = true;
                            }
                        }

                        break;
                    }
                    case '/':
                    {
                        if (lastChar == '*' && !inString && (inComment || inJavadoc))
                        {
                            if (inJavadoc)
                            {
                                inJavadoc = false;
                                appendJavadocComment(buf, line.substring(textPos, j + 1));
                            }
                            else // inComment
                            {
                                inComment = false;
                                appendComment(buf, line.substring(textPos, j + 1));
                            }

                            textPos = j + 1;
                        }
                        else if (lastChar == '/' && !inComment && !inJavadoc && !inString)
                        {
                            // Rest of the line is a comment
                            parseRow(buf, line.substring(textPos, j - 1));

                            if (inJavadoc)
                            {
                                appendJavadocComment(buf, line.substring(j - 1));
                            }
                            else
                            {
                                appendComment(buf, line.substring(j - 1));
                            }

                            j = line.length();
                            textPos = j;
                        }

                        break;
                    }
                }

                lastChar = j < line.length() ? line.charAt(j) : 0;
            }

            // At EOL
            if (inJavadoc)
            {
                appendJavadocComment(buf, line.substring(textPos));
            }
            else if (inComment)
            {
                appendComment(buf, line.substring(textPos));
            }
            else
            {
                parseRow(buf, line.substring(textPos));
            }

            buf.append("\n");
        }

        return buf.toString();
    }

    private void parseRow(final StringBuffer buf, final String text)
    {
        boolean inString = false;
        int textPos = 0;
        char lastChar = ' ';
        String lastToken = "";
        char nextChar;

        for (int j = 0, textLen = text.length(); j < textLen; j++)
        {
            nextChar = text.charAt(j);
            if (inString)
            {
                if (nextChar == '"' && lastChar != '\\')
                {
                    appendStringLiteral(buf, text.substring(textPos, j + 1));
                    inString = false;
                    textPos = j+1;
                }
            }
            else
            {
                switch (nextChar)
                {
                    case '"':
                    {
                        appendText(buf, text.substring(textPos, j));
                        inString = true;
                        textPos = j;
                        break;
                    }
                    case '}':
                    case '{':
                    case ')':
                    case '(':
                    case ';':
                    case ':':
                    case ' ':
                    case '\t':
                    {
                        if (isJavaKeyword(lastToken))
                        {
                            appendText(buf, text.substring(textPos, j - lastToken.length()));
                            appendKeyword(buf, lastToken);
                            textPos = j;
                        }

                        lastToken = "";

                        break;
                    }
                    default:
                        lastToken += nextChar;
                }
            }

            lastChar = nextChar;
        }

        lastToken = text.substring(textPos);

        if (isJavaKeyword(lastToken.trim()))
        {
            appendKeyword(buf, lastToken);
        }
        else
        {
            appendText(buf, lastToken);
        }
    }

    /**
     * Appends a Java keyword to the buffer.
     * @param buf the buffer to append to.
     * @param keyword the keyword to append.
     */
    private void appendKeyword(final StringBuffer buf, final String keyword)
    {
        append(buf, "color: blue; font-weight: bold", keyword);
    }

    /**
     * Appends a Java comment to the buffer.
     * @param buf the buffer to append to.
     * @param text the text to append.
     */
    private void appendComment(final StringBuffer buf, final String text)
    {
        append(buf, "color: green", text);
    }

    /**
     * Appends a Javadoc comment to the buffer.
     * @param buf the buffer to append to.
     * @param text the text to append.
     */
    private void appendJavadocComment(final StringBuffer buf, final String text)
    {
        append(buf, "color: gray", text);
    }

    /**
     * Appends a Java String literal to the buffer.
     * @param buf the buffer to append to.
     * @param literal the literal to append.
     */
    private void appendStringLiteral(final StringBuffer buf, final String literal)
    {
        append(buf, "color: red", literal);
    }

    /**
     * Appends plain text to the buffer.
     * @param buf the buffer to append to.
     * @param text the text to append.
     */
    private void appendText(final StringBuffer buf, final String text)
    {
        buf.append(text);
    }

    /**
     * Appends CSS styled text to the buffer.
     * @param buf the buffer to append to.
     * @param style the CSS style to use.
     * @param text the text to append.
     */
    private void append(final StringBuffer buf, final String style, final String text)
    {
        buf.append("<span style='").append(style).append("'>");
        buf.append(text.replace(' ', '\u00a0'));
        buf.append("</span>");
    }

    /**
     * Indicates whether the given token a java keyword.
     * @param token the token to check
     * @return true if the supplied token is a java keyword, false otherwise
     */
    private boolean isJavaKeyword(final String token)
    {
        return KEYWORDS.contains(token);
    }
}
