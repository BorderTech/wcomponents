## Code Standards
The following comprises the expected code standards for the core WComponents implementation code. This is a work in
progress

### General Coding standards
The following apply to all code in the WComponents default theme:

* maximum line length is 120 characters and may be exceeded where a break would cause parsing or reading issues;
* code indentation is made using the TAB character;
* the source of truth for attribute names is the schema; and
* code should comply with relevant standards as defined by:
    * [Ecma International](http://www.ecma-international.org/default.htm) for
      [JavaScript](http://www.ecma-international.org/publications/standards/Ecma-262.htm);
    * [W3C](http://www.w3.org/) for [CSS](http://www.w3.org/Style/CSS/), [XSLT](http://www.w3.org/TR/xslt),
      [XPath](http://www.w3.org/TR/xpath/) and [SVG](http://www.w3.org/Graphics/SVG/);
    * [WHATWG](https://whatwg.org/) for [HTML5](https://html.spec.whatwg.org/). **Note:** the HTML5 specification copy
      republished and amended by the W3C is **not** to be used as it does not accurately reflect the HTML5 specification
      and provides no easily accessible documentation of the changes made by the W3C to the WHATWG specification. For
      further details see the
      [note in the WHATWG specification](https://html.spec.whatwg.org/multipage/introduction.html#is-this-html5?); or
    * other relevant standards body for other technologies.

### JavaScript Coding Standards
The JavaScript code standards are based on the [Google JavaScript Style Guide](https://google-styleguide.googlecode.com/svn/trunk/javascriptguide.xml)
 but amended where required; note especially that the general rules regarding line length and indentation apply such that
 where the Google guidelines specify 2 spaces it can be read as one TAB and 4 spaces can be read as two TABs.

#### Mandatory Requirements
All JavaScript must cause no errors or warnings when linted using [JSHint](http://jshint.com/) with the configuration as
set in jsl.wc.conf in the core source root directory. Some specific requirements are:

* Line ending semi-colons are not optional.
* All variables must be declared. This means globals are forbidden except for those in the lint config; being:
    * define (a global in requirejs);
    * require (a global in requirejs);
    * window;
    * console;
    * document;
    * NodeFilter;
    * Node; and
    * KeyEvent.

    This implies that even commonly used assumptions (such as using alert rather than window.alert) will result in a
    build failure.

In addition to the lint rules the following must be adhered to:

* One var declaration per function. All vars are hoisted by compressors and JIT compilers and JavaScript does not have
branch scope so declare everything up front.
* "use strict"; must be defined in each JavaScript file as the first declaration inside the body of the function
argument to the files declare statement;
* == and != must not be used unless absolutely required. In cases where they are used they must be commented with the
reason otherwise they may be replaced with === and !== without checking your reasoning (everyone is presumed guilty
until proven innocent);
* function variables should not mask class variables which, in turn, should not mask arguments to the function argument
of the class' define which should not mask globals;
* arguments should generally not be manipulated in a function.

#### Code formatting
Code formatting corresponds with the [Google JavaScript Style Guide](https://google-styleguide.googlecode.com/svn/trunk/javascriptguide.xml)
with the following exceptions:

* Braces are always required for single-statement loops, conditions etc;
* else, catch, finally are placed on their own line but `else if` is on its own line but not one line each:

        try {
            ....
        }
        catch (e) {
           ....
        }
        finally {
            ....
        }

        if (someTest) {
            ....
        }
        else if (someOtherTest) {
            ....
        }
        else {
            ....
        }

#### JSDoc

Functions should be commented with [JSDoc 3](http://usejsdoc.org/).

##### AMD modules
The return function of the define should be tagged with the `@exports` tag as per
[JSDoc AMD modules](http://usejsdoc.org/howto-amd-modules.html). This is usually done before the define() declaration;

    //use this:
    /**
     * A module representing a foo control.
     * @module
     */
    define([....], function(....) {
        ....
    });

    //rather than
    define([....], function(....) {
        /**
         * A module representing a foo control.
         * @module
         */
        ....
    });

When the module returns a function use the `@alias` tag.

    //use this:
    /**
     * A module representing a foo control.
     * @module moduleName
     */
    define([....], function(....) {
        /**
         * @alias module:moduleName
         */
        return function() {
            ....
        };
    });

##### Functions
Each function comment should include a brief outline of the purpose of the function. Markdown is acceptable in the
function description. If you use HTML in JSDoc then it must be in XHTML format (closing tags are never optional). Each
function declaration header comment should contain the `@function` tag.
Arguments must be commented using the `@param` tag. The function scope should be explicitly declared using `@public`,
`@private` or `@protected`.

Mandatory tags in a function header block are:

* `@function` along with the name if it is an aliased function;
* one of `@private`, `@public`, `@protected` or `@ignore` (see Making properties and methods public for testing below); and
* `@param` when the function has arguments.
* A constructor must also include the `@constructor` tag and an `@alias` tag if required.

##### Properties
Class level properties should be commented using `@var`, `@constant` etc. The comment should include the type and default
value as well as a description of the variable.

`@since`, `@author` and `@copyright` tags are optional. All WComponents core JavaScript IP is in accordance with the
WComponents licence making `@copyright` mostly irrelevant.

#### Other comments
Comments are generally in line with the [Google JavaScript Style Guide](https://google-styleguide.googlecode.com/svn/trunk/javascriptguide.xml)
except for the line length and indent rules as described above.

#### Making properties and methods public for testing
This is not discouraged and may sometimes be the best way to unit test a function. If a private function is particularly
complex and does not depend on user interaction then it should be made public in order to be unit tested unless there is
an obvious mechanism to invoke the function from an existing public function which is also not dependant on user
interaction.

A function which is public for testing should be published using an expression which provides the public function with
the name of the private function preceded by an underscore (_) character. Use the `@ignore` JSDoc tag to prevent the
public method from appearing in the documentation. The expressions for these faux-public members should be at the end of
the class block.

Given a private function foo() which is called internally as foo() then:

    //Use this
    /**
     * Usual JSDoc gubbins ...
     * @method
     * @private
     */
    function foo() {
        ....
    }
    /**
     * Make foo public for testing only.
     * @ignore
     */
    this._foo = foo;

    /* Do not use this as this form allows an internal call to this._foo() and foo()
      which may result in inexpected or inconsistent results. */
    this._foo = function foo() {
        ....
    };

#### Source order
The use of define to create JavaScript "class" style objects generally makes the source order within the define's
function argument mostly irrelevant. We prefer to keep things in some semblance of order though.

Variables are always declared first and in a single block. This applies to class variables and function variables. This
is not optional (see above).

Functions should generally be declared before they are used but when they are nested in a constructor this is not
strictly required.

Members on the prototype chain of a constructor should be declared before the constructor when the constructor is
itself nested in the function argument of a define or require.

### CSS Coding Standards
The CSS standards comply in spirit with the [Google HTML/CSS Style Guide](https://google-styleguide.googlecode.com/svn/trunk/htmlcssguide.xml)
sections which deal with CSS with the exception of:

* indentation and line length as described above; and
* a semi-colon must **not** be placed after the last declaration of a rule (as this does not comply with the CSS
  specification). Note, however, that a trailing semi-colon is acceptable in SCSS source as it is removed in compilation
  and compression.

The following additions also apply:

* A selector should be placed on a single line but if its length exceeds the 120 character limit it may be broken and
  indented by one TAB, in this case the line break should be *before* a selector modifying combinator (> , ~ etc) if
  present.
* Never qualify an id selector with a type and avoid qualifying a class selector with a type except to avoid ambiguity.
  In this latter case the reason must be commented.
* Do not place a comment inside a declaration block (it causes issues with Safari's developer tools).
* Do not include empty lines in a declaration block (it contravenes the specification).
* Separate rules with at least one empty line.

#### Commenting SCSS
* Each SCSS file should commence with a comment which includes its file name and end with a comment which includes the
  word 'end' and its file name. This makes CSS debugging much easier (remember that comments are stripped in the final
  compressed output).
* If a rule is commented there should not be any empty lines between the last line of the comment and the first selector
  but the comment should not be on the same line as the selector.
* If a particular selector in a multi-selector rule requires a comment it should be placed on the same line as the
  selector and precede the comma or opening brace (if it is the last selector).
* If a particular declaration in a declaration block requires a comment it should be placed in the rule comment and
  include the declaration's property name as part of the comment. This is to avoid placing comments inside a declaration
  block as noted above.


    /* my.component.scss */

    /* This declaration block does something odd.
     * The line-height declaration is used to ... which is needed for ...
     */
    .foo,
    .bar > .somelongclassname [aria-selected='true'] > :first-child {
        ....
        line-height: -2px;
        ....
    }

    /* end my.component.scss */

### XSLT Coding Standards
The individual XSLT source files are built to a final XSLT file using ANT and a third party XSLT compressor with a
XSLT to XSLT transform. This allows us to use complete well formed XSLT documents in source which helps most XSLT aware
IDEs do code completion, highlighting and basic validity checking of each source file.

* The XSLT file should be well formed including the <xsl:stylesheet ... /> root element which should itself declare all
  necessary namespaces including html:, xsl: and ui:.
* Use xsl:import to reference external source files which are required to define named templates used in any
  \<xsl:call-template ...\> elements.
* The <xsl:output /> element and processing instructions are optional as they will be stripped in the build transform.
* The XSLT source files use TAB indentation of all elements. This whitespace is for humans and is stripped out during
  XSLT compression in build.
* All variable and template names should be in camelCase. The XSLT compressor is able to replace template, param and
  variable names, in a similar way to JavaScript compressors/obfuscators. Named templates and variables should have a
  name which is descriptive to help with coding and reuse without concern for the file size served to the final client.
* Use \<xsl:element\> to output any HTML element which is self closing as some XSLT processors have issues with creating
  HTML shorrtag elements when the XSLT includes a closing tag:

        <!-- use this -->
        <xsl:element name="input">
            ....
        </xsl:element>


        <!-- do not use this -->
        <input>
            ...
        </input>

* Any variable which is required to create a simple attribute to an output element must be declared before the opening
  tag of the element. This is due to a flaw in the XSLT compression library and requires an example to explain. Assume
  we have a variable used to calculate a value for the 'for' attribute of a label element. This value is then re-used
  elsewhere so it is more efficient to hold it in a variable than calculate it twice. The xsl:variable must appear
  before the label element (or \<xsl:element name="label"\>) output opening tag.

        <!-- use this -->
        <xsl:variable name="foo">
            ...
        </xsl:variable_
        <xsl:element name="input">
            <xsl:attribute name="data-bar">
                <xsl:value-of select="$foo"/>
            </xsl:attribute>
        </xsl:element>

        <!-- do not use this -->
        <xsl:element name="input"_
            <xsl:variable name="foo">
                ...
            </xsl:variable_
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

* Never (really: *NEVER*) use xsl:foreach in live code. It _may_ be acceptable in debug mode but the reason for its use
  should be included in a comment for each use.

... **To be continued** ...
