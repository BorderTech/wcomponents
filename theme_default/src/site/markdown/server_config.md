# Configuring the Webserver for the Theme

The WComponents client side framework (a.k.a. the "themes") is critically dependent on the correct configuration of your
webserver. Incorrect configuration on the server will result in two major negative impacts:

1. Web apps will not function correctly, everything from complete and utter failure of the entire web app or otherwise
   failure of individual features is possible.
2. Web apps will perform poorly.

## Assumptions for web server config
The client side framework assumes the following:

* All resources served to the theme will have the correct http "Content-Type" header set.
* All resources served to the theme will be set to cache forever. The http headers should be set to ensure that the
  browser fetches resources from cache ALWAYS and does not send 304 ("Not Modified") requests.

### Content Types
In general it is likely that your server is configured correctly "out of the box" however there are one or two less
common content types employed by the theme.

In many cases it is theoretically possible for client side code to work around the server responding with the incorrect
content type header, however the WComponents theme makes no such guarantees. It is up to you to configure your server
correctly. This means, for example, that if you serve an XML resource then make sure you set an XML content type.

WComponents ships with content type mappings you can use as a reference when configuring your webserver.

### Cache Control
WComponents is targeted primarily at the construction of webapps, not small web pages. We expect the users of these
webapps are not one-off visitors but repeat users who will use the web app regularly.

Given this usage pattern we can deliver a rich client side framework and assume that it is only ever loaded the first
time the user visits the web app, after that we want the browser to fetch the framework from cache and thereby
experience great performance benefits.

In general usage the only time we want the browser to bypass cache is when a new version of the framework is deployed.
This is handled by loading all resources with a "cachebuster" which is a querystring parameter that changes from one
build of the framework to the next.

For example, to load the resource "all.xsl" would look something like this:
`\<?xml-stylesheet type="text/xsl"  href="all.xsl?foo=bar"?\>`

In this case "foo=bar" is our cachebuster. When a new version of the framework is released this should change, for
example: `\<?xml-stylesheet type="text/xsl"  href="all.xsl?foo=bar100"?\`>

We have now changed the cachebuster to "foo=bar100" which will cause the browser to load the new resource from the
server, bypassing cache.