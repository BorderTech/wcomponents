# JavaScript Developer Information

## Code Standards
The following comprises the expected code standards for the core WComponents theme JavaScript. This is a work in progress.

The JavaScript code standards are based on the [Google JavaScript Style Guide](https://google-styleguide.googlecode.com/svn/trunk/javascriptguide.xml) but amended where required; note especially that the general rules regarding line length and indentation apply such that where the Google guidelines specify 2 spaces it can be read as one TAB and 4 spaces can be read as two TABs.


### Mandatory Requirements

All JavaScript must cause no errors or warnings when linted using [eslint](http://http://eslint.org/) with the configuration as set in build-import.xml macro javascript.lint. You should refer to that macro and the eslint defaults for more information but in plain language some specific requirements are:

* "use strict" must be defined in each JavaScript file as the first declaration inside the body of the function argument to the file's declare statement;
* line ending semi-colons are **not optional**;
* brace style is Stroustrup;
* braces are always required for single-statement loops, conditions etc;
* a space is required before a block opening brace;
* a space is required after **every** keyword;
* indentation uses **TAB** one TAB per level;
* each case in a switch is indented;
* all variables must be declared therefore means globals are forbidden except for those in the AMD and browser environments (require, define, window and document) and the eslint config globals(KeyEvent). The outcome of this is that even commonly used assumptions (such as using alert rather than window.alert) will result in build failure;
* there **must not** be declared but unused members in any scope except arguments in a function declaration;
* a single white space is required at the beginning of any comment.

In addition to the lint rules the following **should** be applied:

* the source of truth for attribute names (including HTML element class names) is the XML schema
* code should comply with the [Ecma International](http://www.ecma-international.org/publications/standards/Ecma-262.htm) standards.
* maximum line length is 120 characters and may be exceeded where a break would cause parsing or reading issues;
* One var declaration per function. All vars are hoisted by compressors and JIT compilers and JavaScript does not have branch scope so declare everything up front.
* `==` and `!=` must not be used unless absolutely required. In cases where they are used they must be commented with the reason otherwise they may be replaced with `===` and `!==` without checking your reasoning (everyone is presumed guilty until proven innocent);
* function variables should not mask class variables which, in turn, should not mask arguments to the function argument of the class' define which should not mask globals;
* arguments should generally not be manipulated in a function unless they are returned by the function.

## JavaScript Documentation
See [The wiki page](https://github.com/BorderTech/wcomponents/wiki/Theme-JSDoc-Conventions) for details of our JSDoc conventions.

## Other comments
Comments are generally in line with the [Google JavaScript Style Guide](https://google-styleguide.googlecode.com/svn/trunk/javascriptguide.xml) except for the line length and indent rules as described above. Comments may be single line or multiline as appropriate to the comment, we are not that precious though we do enforce a space at the beginning of each comment!

## Making properties and methods public for testing
This is not discouraged and may sometimes be the best way to unit test a function. If a private function is particularly complex and either does not depend directly on user interaction or is a complex event driven function (such as some key event helpers) then it should be made public in order to be unit tested unless there is an obvious mechanism to invoke the function from an existing public function which is also not dependent on user interaction.

**It is always better to publicise and test than not to test at all!**

A function which is public for testing should be published using an expression which provides the public function with the name of the private function preceded by an underscore (\_) character. Use the `@ignore` JSDoc tag to prevent the public method from appearing in the documentation. The expressions for these faux-public members should be at the end of the class block.

Given a private function foo() which is called internally as foo() then:
``` javascript
// Use this
/**
 * Usual JSDoc gubbins ...
 * @function
 * @private
 */
function foo() {
    ....
}
/**
 * Make {@link #foo} public for testing only.
 * @ignore
 */
this._foo = foo;

/* Do not use this as this form allows an internal call to this._foo() and foo()
  which may result in unexpected or inconsistent results. */
this._foo = function foo() {
    ....
};
```

## Source order
The use of define to create JavaScript "class" style objects generally makes the source order within the define's function argument mostly irrelevant. We prefer to keep things in some semblance of order though.

Variables are always declared first and in a single block. This applies to class variables and function variables. This is not optional (see above).

Functions should generally be declared before they are used but when they are nested in a constructor this is not strictly required.

Members on the prototype chain of a constructor should be declared before the constructor when the constructor is itself nested in the function argument of a define or require. This is kinda optional but doing otherwise messes with JSDoc under some conditions.
