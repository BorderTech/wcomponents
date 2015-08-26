## Building the Theme

WComponents theme implementations are built using [Maven](http://maven.apache.org/). The source code is not able to be
simply edited and run in a browser as it is for a web site.

The theme has Maven configuration which should never need to be changed. The usual Maven goals are used.

Unless otherwise configured only the default WComponents theme will be built. You should review the theme user.xml file
and follow its directions for configuring theme build options.

### Skipping automated tests

Maven has several flags for skipping automatic tests when running a build. The one to use for skipping theme tests is
`-DskipTests`. Your IDE may have a configuration to set this as a default for non-test builds (Netbeans has this).

## Further information

* [Testing and debugging](./debugging.html)
* The theme user.xml for documentation regarding building particular implementations
* [Apache Maven](http://maven.apache.org/)
