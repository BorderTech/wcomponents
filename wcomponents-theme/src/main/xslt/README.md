# XSLT Information

## XSL Element to HTML elements

**READ THIS IF YOU ARE EVEN THINKING ABOUT CHANGING THE XSLT**: WComponents inserts a DOCTYPE definition into the XML to allow lazy use of &nbsp; The upshot of this is that IE believes the XML is HTML and this has a flow on effect with some elements. Any HTML shorttag elements will cause a problem if the XSLT contains a `<shorttag></shorttag>` as IE (at least up to IE8) will output `<shorttag></shorttag></shorttag><//shorttag>`.

To mitigate against accidentally doing this we use `<xsl:element>` for element contruction and the build process XSLT compression includes a step to reduce the verbosity of this for elements which are not HTML self-closing. So when you make any HTML element in XSLT please use:

``` xml
<xsl:element name="elementname">
    <xsl:attribute...> etc
</xsl:element>
```

Then the XSLT compressor will make it into `<elementname attribute=".." />` if it is safe to do so.

## Default Namespace

The default HTML namespace `xmlns="http://www.w3.org/1999/xhtml"` was removed due to bug [https://bugzilla.mozilla.org/show_bug.cgi?id=631455]. It was then discovered that it also causes **MASSIVE** bugs in IE, these bugs do not show symptoms very often in IE but when you hit the right combination then you have some very serious issues to deal with so basically **NEVER** put it back. Hmm ok this line also caused serious problems in Chrome. Caused line breaks to double up for example. Maybe we were wrong to ever put this in.

## Why online XSLT tutorials are dangerous

If you ever use `<xsl:for-each>` without providing a detailed justification in triplicate and signed in blood we will hunt you down and **mock you**. Do not learn your XSLT chops online.

`<xsl:for-each>` is incredibly slow. We have cleaned up other transforms where removing `xsl:for-each` and replacing them with apply-templates or apply-imports has resulted in transform times being reduced from hundreds of seconds to tens of milliseconds with one extreme case (admittedly in IE6/MS XML4) being reduced from over 14 minutes to around 600 milliseconds.

**YES: 14 MINUTES to 600 MILLISECONDS BY REMOVING xsl:for-each**.

## Debug  variables and tests

We have a 'magic' XSL param $isDebug. This is set to 1. You can use this param as part of a xsl:if or xsl:when to output content only in debug mode. For example one could choose to output a marker if you are debugging a XSLT issue such as:

``` xml
<xsl:if test="$isDebug=1">
    <xsl:comment>
        <xsl:value-of select="concat('indent is ',$indent,'&#xA;')"/>
    </xsl:comment>
</xsl:if>
```

The $isDebug param and any branch which has it as part of the test is stripped from the compressed XSLT.

## Coding Standards

The individual XSLT source files are built to a final XSLT file using ANT and a third party XSLT compressor then a further XSLT to XSLT transform. This allows us to use complete well formed XSLT documents in source which helps most XSLT aware IDEs do code completion, highlighting and basic validity checking of each source file. It also allows us to create more readable XSLT with less concern for the file size of the final XSLT files sent to the end user.

The following standards apply to XSLT:

* [CSS](http://www.w3.org/Style/CSS/),
* [XSLT](http://www.w3.org/TR/xslt),
* [XPath](http://www.w3.org/TR/xpath/);
* [HTML5](https://html.spec.whatwg.org/). **Note:** the HTML5 specification copy republished and amended by the W3C is **not** to be used as it does not accurately reflect the HTML5 specification and provides no easily accessible documentation of the changes made by the W3C to the WHATWG specification. For further details see the [note in the WHATWG specification](https://html.spec.whatwg.org/multipage/introduction.html#is-this-html5?).

### Formatting

* Indentation is by **TAB** one tab per level of indent.
* All variable and template names should be in **camelCase** and descriptive to help with coding and reuse without concern for the file size served to the final client - that is what the compressor is for.
* The XSLT file should be well formed including the `<xsl:stylesheet ... />` root element which should itself declare all necessary namespaces including html:, xsl: and ui:. This is to assist IDE auto-complete.
* Use `xsl:import` to reference external source files which are required to define named templates used in any `<xsl:call-template ...>` elements (again to assist IDE auto-complete).
* Use `xsl:element` to output any HTML element which is self closing or shorttag (see above).
* Any variable which is required to create a simple attribute to an output element must be declared before the opening tag of the element. This is due to a flaw in the XSLT compression library and requires an example to explain.

  Assume we have a variable used to calculate a value for the 'for' attribute of a label element. This value is then re-used elsewhere so it is more efficient to hold it in a variable than calculate it twice. The `xsl:variable` must appear before the label element (or `<xsl:element name="label" >`) output opening tag.
  ``` xml
  <!-- use this -->
  <xsl:variable name="foo">
    ...
  </xsl:variable>
  <xsl:element name="input">
    <xsl:attribute name="data-bar">
        <xsl:value-of select="$foo"/>
    </xsl:attribute>
  </xsl:element>
  <!-- do not use this -->
  <xsl:element name="input"_
    <xsl:variable name="foo">
        ...
    </xsl:variable>
    <xsl:attribute name="data-bar">
        <xsl:value-of select="$foo"/>
    </xsl:attribute>
  </xsl:element>

  <!-- but this is acceptable if the value calculated is not to be reused -->
  <!-- do not declare the variable foo -->
  <xsl:element name="input"_
    <xsl:attribute name="data-bar">
        <!-- place the calculation of the value here -->
    </xsl:attribute>
  </xsl:element>
  ```
* Never (really: **NEVER**) use `xsl:foreach` in live code. It _may_ be acceptable in debug mode but the reason for its use should be included in a comment for each use.

### Applying templates

If the schema for an element indicates that the element has only element children (for example ui:menu) then using `<xsl:apply-templates />` to apply those templates should be safe enough. However, if the XML is indented or has any extraneous space then this space is a text node and will be copied through. This shouldn't matter in HTML, but sometimes, and in some browsers, it does. Therefore, if we are applying templates in an element which only have element children we prefer to use `<apply-templates select="*"/>` to ensure we do not get these extraneous text nodes. They can play havoc with content wrapping in closely styled buttons (such as those in a BAR menu) in Chrome, for example.

## Things to do

* Improve in-file documentation.
