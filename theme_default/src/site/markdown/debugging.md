# Testing, Debugging and Examples

## Debugging using the WComponents JAVA examples

The WComponents JAVA examples allows client side debugging of live samples with proper form submissions etc. This has
significant advantages for some aspects of theme development, especially debugging write-state, submission and AJAX
issues. The following configuration properties are optional but recommended since they allow you to use the debug
versions of the JavaScript and CSS within the WComponents examples.

<pre><code>
#Enabling debug and debug.clientSide is useful in the static examples for debugging
#This property turns on the debugging tool kit within the LDE examples
wcomponent.debug.enabled=true
#This property can also be set from within the debugging tool kit
wcomponent.debug.clientSide.enabled=true
</code></pre>

## Using a locally built theme with the WComponent examples
The WComponent example application will use the bundled theme. This is the WComponent default theme and is almost
certainly not what you want to test. Two run time properties are used to set a different theme.

<pre><code>
wcomponent.lde.theme.dir=/PATH/TO/theme/target/classes/theme
wcomponent.theme.name=MY_THEME
</code></pre>


## Testing theme JavaScript

The WComponents theme uses [DOH](http://dojotoolkit.org/reference-guide/1.9/util/doh.html) and is in the process of
moving to [intern](https://theintern.github.io/) for unit tests.

The tests reference a set of hard-coded "example" pages which were originally used for CSS testing. The WComponent LDE
can be configured to load these pages and this must be done as a precondition to running the tests.

### To run the tests
1. Start the LDE with the following properties to run the set of static XML pages:
<pre><code>
wcomponent.lde.theme.dir=/PATH/TO/theme/target/classes/theme<br/>
wcomponent.lde.webdocs.dir=/PATH/TO/theme/target/test-classes<br/>
wcomponent.theme.name=MY_THEME<br/>
<br/>
\#OPTIONAL changing the server port allows you to run the WComponent examples and theme test pages concurrently<br/>
\#Port 8080 is the default if this property is not set<br/>
wcomponent.lde.server.port=8081
</code></pre>
2. invoke `mvn test` against the theme POM;
3. open the browsers in which you are going to run tests http://localhost:PORT/MY_THEME/doh/runner.html to load the
  doh runner (intern instructions to follow).
