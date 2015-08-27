## Testing, Debugging and Examples

### Debugging using the WComponents JAVA examples

The WComponents JAVA examples (module wcomponent_examples) allows client side debugging of live samples with proper form
submissions etc. This is required for some aspects of theme development, especially debugging CSS, write-state,
submission and AJAX issues.

The examples are run using the wcomponent_examples_lde module. This module may be configured with a local_app.properties
file to set some useful debugging options. The following configuration properties allow you to use the debug versions of
the JavaScript and CSS within the WComponents examples.

    wcomponent.debug.enabled=true
    wcomponent.debug.clientSide.enabled=true

#### Using a locally built theme with the WComponent examples

The WComponent example application will use the bundled theme. This is the WComponent default theme and is almost
certainly not what you want to test. Two run time properties are used to set a different theme.

    wcomponent.theme.content.path=/theme/
    wcomponent.lde.theme.dir=/PATH/TO/theme/target/classes/theme
    wcomponent.theme.name=MY_THEME

#### Other useful options

You can set a session session timeout using `wcomponent.lde.session.inactive.interval` for example 86400 will set a
timeout of one day.

You can set the webdocs directory to allow non-WComponents application code to run in the "LDE", for example for
running web client unit tests in intern. THis is done with `wcomponent.lde.webdocs.dir` (see below).

### Testing theme JavaScript

The WComponents theme uses [intern](https://theintern.github.io/) for JavaScript unit tests. These can be run using
phantomJs or the Intern chrome driver but you may also start an LDE to do browser testing.

1. invoke `mvn test` against the theme POM, this will run the tests in phantomJS but is required to build the tests;
2. run the wcomponents_examples_lde with the following properties in local_app.properties:

        wcomponent.lde.webdocs.dir=/PATH/TO/theme/ # This is to the source root, not the target root!
        # OPTIONAL changing the server port allows you to run the WComponent examples and test server concurrently
        # Port 8080 is the default if this property is not set
        wcomponent.lde.server.port=8081

3. open the browsers in which you are going to run tests to
  `http://localhost:PORT/node_modules/intern/client.html?config=target/test-classes/THEME_NAME/intern` to load the
  client test runner.
