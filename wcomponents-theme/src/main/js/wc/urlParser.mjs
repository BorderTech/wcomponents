/**
 * Utility to parse URL strings into their constituent parts.
 *
 * <p>Known limitations:</p>
 * <ul>
 * <li>The parser is currently only guaranteed to work with rfc1738 valid http: protocol URLs and relative urls from web
 *     pages which are assumed to be in http syntax. It should, however, manage to parse at least the Common Internet
 *     Scheme Syntax (CISS) parts of other IP protocols outlined in rfc1738.</li>
 * <li>The parser will parse a mailto: protocol URL but will return an object with window.location syntax (and our
 *     customizations). A valid mailto: protocol URL return object will have user, host and hostname properties
 *     correctly identified; note however that the user does not include a password part.</li>
 * <li>The parser may fail if the domain is an IP address. It will incorrectly report the IP address, including [ and ]
 *     for IPv6 addresses, as the host name.</li>
 * <li>This parser will fail (guaranteed) if the scheme/protocol contains a .(dot) character.</li>
 * <li>The parser will include the type of an ftp: protocol URL in the path.</li>
 * <li>The parser will not correctly parse a news: protocol URL where the message identifier is separated from the news
 *     group name by an `@`.</li>
 * <li>The parser will report a WAIS database, wtype and wpath as parts of path.</li>
 * <li>The parser will not correctly split gopher: protocol paths and will not correctly parse or split a gopher:
 *     search.</li>
 * <li>The parser will not correctly parse gopher+: protocol URLS.</li>
 * <li>The parser will fail to correctly identify parts of a prospero: protocol URL after the port.</li>
 * <li>The parser will fail to parse any URL which does not comply with CISS except relative and server relative,
 *     implicitly http: protocol URLs.</li>
 * <li>The parser will incorrect report search name:value pairs if the search string contains =value (no name but an
 *     equals sign) which is not valid under any circumstances so I do not care that it does not work!</li></ul>
 *
 * @todo helper parsers for other none http: protocols (very low priority)
 * @module
 */

const PORT = "port",
	HOST = "host",
	HOST_NAME = "hostname",
	USER = "user",
	PASSWORD = "password",
	HASH = "hash",
	HASH_CLEAN = "hashClean",
	PROTOCOL = "protocol",
	SEARCH = "search",
	PATH = "pathname",
	SEARCH_ARRAY = "searchArray",
	HOST_ARRAY = "hostnameArray",
	PATH_ARRAY = "pathnameArray",
	NO_VALUE = "";  // used to be null but browsers use "" in window.location so i think we should too

const instance = {

	/**
	 * Parse a URL.
	 * Takes any valid URL and break it into its component parts.
	 * A valid URL takes the format: scheme:[/]{2,}user:password@domain:port/path?search#hash
	 *
	 * @example
	 * var result = urlParser.parse("http://www.example.com/business-services/bar.htm?foo=bar&bar=foo#content_anchor");
	 * console.log(JSON.stringify(result));
	 * // prints the following to console:
	 * {
	 *   "pathname":"/business-services/bar.htm",
	 *   "protocol":"http:",
	 *   "search":"?foo=bar&bar=foo",
	 *   "searchArray":{"foo":"bar", "bar":"foo"},
	 *   "hash":"#content_anchor",
	 *   "hashClean":"content_anchor",
	 *   "hostname":"www.example.com",
	 *   "hostnameArray":["www", "example", "com"],
	 *   "pathnameArray":["business-services", "bar.htm"],
	 *   "host":"www.example.com"
	 * }
	 *
	 * @function module:wc/urlParser.parse
	 * @param {String} url the URL to parse.
	 * @returns {module:wc/urlParser~parseObj} the url as an object where each property is generally consistent
	 *    with the names used in "window.location" with some additions.
	 */
	parse: function(url) {
		if (!url) {
			return null;
		}
		return new ParsedUrl(url);
	}
};

/**
 * MDC has this really weird, completely invalid, break DNS thing going on where they put [] around a
 * hostname and return host with the square brackets and hostname without. I am sure this is a
 * misunderstanding of IPv6 [] IP notation, but just to make Rick feel better we allow for it here
 * @see https://developer.mozilla.org/en/DOM/window.location
 *
 * @function
 * @private
 * @param {String} hostname The hostname of the URL.
 * @returns {String} The hostname with the square brackets stripped.
 */
function mDCHostNameFixer(hostname) {
	return hostname.replace(/^\[|]$/g, "");  // strip leading and trailing square brackets [www.google.com]
}

/**
 * parse a query (search) string from a URL
 * note: pairs are URL decoded, so you should just get text back. Valid combos are:
 *   - name=value which returns [name, value]
 *   - name= which returns [name, ""]
 *   - name which returns [name, null]
 * invalid combo is =value which returns [value, null] which is just plain wrong!!
 *
 * @function
 * @private
 * @param {String} qs the querystring to parse
 * @returns {Object} Associative array of name=value pairs
 */
function parseQuerystring(qs) {
	const result = {};
	if (!qs) {
		qs = location.search.substring(1, location.search.length);
	}
	if (qs.length > 0) {
		qs = qs.replace(/\+/g, " ");
		const args = qs.split("&");
		let value = null;
		let name = null;
		let pair = null;
		for (let i = 0; i < args.length; i++) {
			pair = args[i].split("=");
			name = decodeURIComponent(pair[0]);
			value = (pair.length === 2) ? decodeURIComponent(pair[1]) : null;
			result[name] = value;
		}
	}
	return result;
}

/**
 * This is a CISS compliant URL parser which should work with all http: and https: URLS and an assortment of
 * others such as nntp: ftp: and sftp:. See the known failures if you are interested in writing a protocol
 * parser.
 *
 * @todo Someone brave should join up all these regexps
 * @function
 * @private
 * @param {String} url A url string to parse.
 * @returns {module:wc/urlParser~parseObj}
 */
function parser(url) {
	const result = this,
		mDCSquareBracketsRe = /^\[.*]$/;
	let mDCSquareBrackets = false;  // see function mDCHostNameFixer(hostname) below;

	let localUrl = url || result[PATH];
	localUrl = parseUserCreds(localUrl, result);
	localUrl = parsePort(localUrl, result);
	localUrl = parseAnchor(localUrl, result);

	/*
	 * Now we have xyz.domain.tld/abc/def, or server relative url /path..., or relative url
	 * foo/path/bar.html, or a simple relative url foo.html
	 *
	 * The only way we can tell the difference between a relative url and a hostname with no path is by
	 * testing the protocol. No protocol means a (server-)relative URL
	 */
	const pathnameSeparator = localUrl.indexOf("/");

	if (pathnameSeparator < 0) {
		if (result[PROTOCOL] === "") {
			// no protocol means a relative url like foo.html and all we have is simple path
			result[PATH_ARRAY] = [localUrl];
		} else {
			// we have no path and the hostname is all that is left;
			result[HOST_NAME] = mDCHostNameFixer(localUrl);
			mDCSquareBrackets = mDCSquareBracketsRe.test(localUrl);
			result[HOST_ARRAY] = result[HOST_NAME].split(".");
			// result[HOST_ARRAY] = url.split(result[HOST_NAME]);
		}
	} else if (pathnameSeparator === 0) {
		// we have a server relative URL because it starts with "/" and all we have left is path
		result[PATH_ARRAY] = localUrl.substring(1).split("/");  // split the path without the leading "/" otherwise pathnameArray[0] is always empty
	} else if (result[PROTOCOL] === "") {
		// relative URL with complex path and the first character is not a "/"
		result[PATH_ARRAY] = localUrl.split("/");
	} else {
		// the first slash separates the hostname from the path
		const hostname = localUrl.substring(0, pathnameSeparator);
		const pathname = localUrl.substring(pathnameSeparator);  // Location.pathname includes leading "/"

		result[HOST_NAME] = mDCHostNameFixer(hostname);
		mDCSquareBrackets = mDCSquareBracketsRe.test(hostname);
		result[HOST_ARRAY] = result[HOST_NAME].split(".");
		result[PATH] = pathname;
		result[PATH_ARRAY] = pathname.substring(1).split("/");  // split the path without the leading "/" otherwise pathnameArray[0] is always empty
	}
	if (result[HOST_NAME] && result[PORT] !== NO_VALUE) {
		result[HOST] = (mDCSquareBrackets ? "[" : "") + result[HOST_NAME] + (mDCSquareBrackets ? "]" : "") + ":" + result[PORT];
	} else {
		result[HOST] = (mDCSquareBrackets ? "[" : "") + result[HOST_NAME] + (mDCSquareBrackets ? "]" : "");
	}
	return result;
}

/**
 * Parses a specific part of a URL.
 * @private
 * @function
 * @param {string} url The URL to parse
 * @param {Object} parsed The object to add the parsed parts to.
 * @returns {string} The URL which may be modified to remove the part that was parsed by this routine.
 */
function parseUserCreds(url, parsed) {
	let result = url;
	const userRe = /^([^:]+)?:?([^@]+)?@/;
	const match = userRe.exec(result);
	if (match) {
		parsed[USER] = match[1];
		parsed[PASSWORD] = match[2];
		result = result.replace(userRe, "");
	}
	return result;
}

/**
 * Parses a specific part of a URL.
 * @private
 * @function
 * @param {string} url The URL to parse
 * @param {Object} parsed The object to add the parsed parts to.
 * @returns {string} The URL which may be modified to remove the part that was parsed by this routine.
 */
function parsePort(url, parsed) {
	let result = url;
	const portRe = /:([0-9]*)/;  // 0-n digits after a colon. the url http://www.domain.com:/ is invalid but working url
	/*
	 * Ports are always numbers and come immediately after a colon
	 * NOTE: according to rfc1738 there should always be a port but no major browser will return a default
	 * port value for window.location.port when the URL in the address bar does not have an explicit port.
	 */
	const match = portRe.exec(result);
	if (match) {
		parsed[PORT] = match[1] || NO_VALUE;
		result = result.replace(portRe, "");
	}
	return result;
}

/**
 * Parses a specific part of a URL.
 * @private
 * @function
 * @param {string} url The URL to parse
 * @param {Object} parsed The object to add the parsed parts to.
 * @returns {string} The URL which may be modified to remove the part that was parsed by this routine.
 */
function parseAnchor(url, parsed) {
	let result = url;
	const endRe = /(\?([^#]+))?(#([^#]+))?$/;
	const match = endRe.exec(result);
	/*
	 * The hash, if it exists, is always at the end of a http: URL UNLESS the url simply ends with a hash
	 * character with nothing after it
	 */
	if (match) {
		if (match[1]) {
			parsed[SEARCH] = match[1];
			parsed[SEARCH_ARRAY] = parseQuerystring(match[2]);
		}
		parsed[HASH] = match[3] || NO_VALUE;
		parsed[HASH_CLEAN] = match[4] || NO_VALUE;
		result = result.replace(endRe, "");
	}
	return result;
}

/**
 * Constructor for an instance of a parsed url.
 * @constructor module:wc/urlParser~ParsedUrl
 * @private
 * @param {String} url A URL.
 */
function ParsedUrl(url) {
	if (!url) {
		return;
	}

	if (typeof url === "string") {
		// turn any backslashes around - damn windows
		url = url.replace(/\\/g, "/");
		// clean any escaped ampersands, we want unescaped ones in the output
		url = url.replace(/&amp;/ig, "&");
		url = url.replace(/#$/, "");  // trailing hash is ignored
		this[PATH] = url;
		/*
		 * Possible to have a port but no scheme; schemes do not have numbers, ports are always numbers.
		 * www.google.com: causes us much mischief, but it also breaks browsers, so we can ignore it
		 * qualitycenter:8080 is valid and painful (it also won't work as an HTML href or src URL since it
		 * lacks a protocol but is not relative, so we can almost ignore it).
		 *
		 * rfc1738 indicates "//" for IP-based protocols, which is all I am interested in. The rfc also
		 * specifies that a scheme must be in lower case, but I am not taking that risk since browsers are
		 * lax in enforcing this (they will rewrite an uppercase protocol, not fail).
		 *
		 * In addition the rfc allows .+- in schemes. For the protocols we are interested in this is
		 * irrelevant, so I am ignoring it (I could ignore the others, but they don't cause me angst).
		 *
		 * Returns scheme including colon, e.g. http: as per rfc. If you don't want the colon strip it yourself.
		 */
		const schemeRe = /^([^:^/]+:)\/{0,2}/;
		const match = schemeRe.exec(url);
		if (match) {
			this[PROTOCOL] = match[1].toLowerCase();
			parser.call(this, url.replace(schemeRe, ""));
		} else {
			parser.call(this);
		}

	}
}

/** @var {String} module:wc/urlParser~ParsedUrl#HASH The value of the url hash (if any).*/
ParsedUrl.prototype[HASH] = NO_VALUE;

/** @var {String} module:wc/urlParser~ParsedUrl#HASH_CLEAN The value of the url hash (if any) with the leading "#" removed.*/
ParsedUrl.prototype[HASH_CLEAN] = NO_VALUE;

/** @var {String[]} module:wc/urlParser~ParsedUrl#HOST_ARRAY The value of the url host segment (if any) as an array split at the DOT separators. */
ParsedUrl.prototype[HOST_ARRAY] = null;

/** @var {String} module:wc/urlParser~ParsedUrl#HOST The value of the url host segment (if any) including the port number if set. */
ParsedUrl.prototype[HOST] = NO_VALUE;

/** @var {String} module:wc/urlParser~ParsedUrl#HOST_NAME The value of the url hostname segment (if any) which excludes the port number when set. */
ParsedUrl.prototype[HOST_NAME] = NO_VALUE;

/** @var {String} module:wc/urlParser~ParsedUrl#PATH The value of the url path segment (if any). */
ParsedUrl.prototype[PATH] = null;

/** @var {String} module:wc/urlParser~ParsedUrl#PATH_ARRAY The value of the url path segment (if any) excluding an protocol as an array split at SLASH separators. */
ParsedUrl.prototype[PATH_ARRAY] = null;

/** @var {String} module:wc/urlParser~ParsedUrl#PORT The value of the url port (if any). */
ParsedUrl.prototype[PORT] = NO_VALUE;

/** @var {String} module:wc/urlParser~ParsedUrl#PROTOCOL The value of the url scheme (if any) including the colon. */
ParsedUrl.prototype[PROTOCOL] = NO_VALUE;

/** @var {String} module:wc/urlParser~ParsedUrl#SEARCH The value of the url query or search string (if any). */
ParsedUrl.prototype[SEARCH] = NO_VALUE;

/** @var {String} module:wc/urlParser~ParsedUrl#SEARCH_ARRAY The value of the url query or search string (if any) split into individual name:value pairs. */
ParsedUrl.prototype[SEARCH_ARRAY] = null;

export default instance;

/**
 * @typedef {Object} module:wc/urlParser~parseObj The object returned from a call to parseUrl.
 * @property {String} [protocol] The scheme including ":", eg "http:".
 * @property {String} [host] The host and port number.
 * @property {String} [hostname] The "domain".
 * @property {String[]} [hostnameArray] Each sub domain of hostname from left to right, for example hostname
 *    "www.example.com" becomes ["www", "example", "com"].
 * @property {String} [user] The user name (not usually set).
 * @property {String} [password] The password (very, very rarely set).
 * @property {String} pathname The path.
 * @property {String[]} pathnameArray Each directory of pathname read from left to right, for example path of
 *    "/info/corp/index.htm" becomes ["info", "corp", "index.htm"].
 * @property {String} [port] The port number as a number-like string.
 * @property {String} [search] The query string.
 * @property {Object} [searchArray] Each name=value pair of querystring URI decoded but note:
 *    <ul>
 *        <li>If there is no value but an equals sign you get {name: ""}.</li>
 *        <li>If there is no value and no equals sign you get {name: null}.</li>
 *        <li>If there is an equals sign and a value but no name you get a slap.</li>
 *    </ul>
 *    TODO: this should be renamed as it is not an array.
 */
