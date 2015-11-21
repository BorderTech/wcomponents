define(["intern!object", "intern/chai!assert", "./resources/test.utils"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var urlParser,
			urls = ["http://[www.google.com]:80/search?q=devmo#test",
				"http://www.wrox.com:80/asp/book.htm?abc",
				"http://[www.google.com]:80/search/farm/house?q=develop&aq=above&aqi=par#test",
				"http://henry:abc456@www.google.com:80",
				"http://henry@www.google.com",
				"index.html",
				"/index.html",
				"#only",
				"#",
				"ftp://file.location.org",
				"http://www.google.com:80?arg1&arg2=4.0&amp;arg3",
				"mailto:banjo@gmail.com",
				"find/the/wombat.html",
				"http://www.google.com:?arg1=2",
				"albert@www.google.com?",
				"/wps/abc_wcomponents-examples/ajaxServlet?s=10&cprofile_correlation_id=[fooadmin:1234e0c2:6]"],
			parsedUrls = {};

		function _parseIt(next, i) {
			parsedUrls[i] = urlParser.parse(next);
			++i;
		}

		registerSuite({
			name: "urlParser",
			setup: function() {
				return testutils.setupHelper(["wc/urlParser"], function(p) {
					urlParser = p;
					urls.forEach(_parseIt);
				});
			},
			testBracketHostUrlHash: function() {
				var thisParsedUrl = parsedUrls[0];
				assert.strictEqual("#test", thisParsedUrl.hash);
			},
			testBracketHostUrlHost: function() {
				var thisParsedUrl = parsedUrls[0];
				assert.strictEqual("[www.google.com]:80", thisParsedUrl.host);
			},
			testBracketHostUrlHostname: function() {
				var thisParsedUrl = parsedUrls[0];
				assert.strictEqual("www.google.com", thisParsedUrl.hostname);
			},
			testBracketHostUrlPathname: function() {
				var thisParsedUrl = parsedUrls[0];
				assert.strictEqual("/search", thisParsedUrl.pathname);
			},
			testBracketHostUrlPort: function() {
				var thisParsedUrl = parsedUrls[0];
				assert.strictEqual("80", thisParsedUrl.port);
			},
			testBracketHostUrlProtocol: function() {
				var thisParsedUrl = parsedUrls[0];
				assert.strictEqual("http:", thisParsedUrl.protocol);
			},
			testBracketHostUrlSearch: function() {
				var thisParsedUrl = parsedUrls[0];
				assert.strictEqual("?q=devmo", thisParsedUrl.search);
			},
			testBracketHostUrlSearchArrayUndefined: function() {
				var thisParsedUrl = parsedUrls[0];
				var searchArray = thisParsedUrl.searchArray;
				assert.isUndefined(searchArray.test);
			},
			testBracketHostUrlSearchArray: function() {
				var thisParsedUrl = parsedUrls[0];
				var searchArray = thisParsedUrl.searchArray;
				assert.strictEqual("devmo", searchArray.q);
			},
			testBracketHostUrlHashClean: function() {
				var thisParsedUrl = parsedUrls[0];
				assert.strictEqual("test", thisParsedUrl.hashClean);
			},
			testBracketHostUrlHostNameArray: function() {
				var thisParsedUrl = parsedUrls[0];
				var expected = ["www", "google", "com"];
				var hostNameArray = thisParsedUrl.hostnameArray;
				assert.deepEqual(expected, hostNameArray);
			},
			testBracketHostUrlPathNameArray: function() {
				var thisParsedUrl = parsedUrls[0];
				var expected = ["search"];
				var pathNameArray = thisParsedUrl.pathnameArray;
				assert.deepEqual(expected, pathNameArray);
			},
			testShortSearchUrlHash: function() {
				var thisParsedUrl = parsedUrls[1];
				assert.strictEqual("", thisParsedUrl.hash);
			},
			testShortSearchUrlHost: function() {
				var thisParsedUrl = parsedUrls[1];
				assert.strictEqual("www.wrox.com:80", thisParsedUrl.host);
			},
			testShortSearchUrlPathname: function() {
				var thisParsedUrl = parsedUrls[1];
				assert.strictEqual("/asp/book.htm", thisParsedUrl.pathname);
			},
			testShortSearchUrlPathNameArray: function() {
				var thisParsedUrl = parsedUrls[1],
					expected = ["asp", "book.htm"];
				assert.deepEqual(expected, thisParsedUrl.pathnameArray);
			},
			testShortSearchUrlSearch: function() {
				var thisParsedUrl = parsedUrls[1];
				// debug("testing url ", thisParsedUrl);
				assert.strictEqual("?abc", thisParsedUrl.search);
			},
			testShortSearchUrlSearchArray: function() {
				var thisParsedUrl = parsedUrls[1],
					expected = null;
				assert.strictEqual(expected, thisParsedUrl.searchArray.abc);
			},
			testExtendedSearchUrlSearch: function() {
				var thisParsedUrl = parsedUrls[2];
				assert.strictEqual("?q=develop&aq=above&aqi=par", thisParsedUrl.search);
			},
			testExtendedSearchUrlSearchArray0: function() {
				var thisParsedUrl = parsedUrls[2],
					searchArray = thisParsedUrl.searchArray;
				assert.strictEqual("develop", searchArray.q);
			},
			testExtendedSearchUrlSearchArray1: function() {
				var thisParsedUrl = parsedUrls[2],
					searchArray = thisParsedUrl.searchArray;
				assert.strictEqual("above", searchArray.aq);
			},
			testExtendedSearchUrlSearchArray2: function() {
				var thisParsedUrl = parsedUrls[2],
					searchArray = thisParsedUrl.searchArray;
				assert.strictEqual("par", searchArray.aqi);
			},
			testUsernameandPasswordUrlHost: function() {
				var thisParsedUrl = parsedUrls[3];
				assert.strictEqual("www.google.com:80", thisParsedUrl.host);
			},
			testUsernameandPasswordUrlHostname: function() {
				var thisParsedUrl = parsedUrls[3];
				assert.strictEqual("www.google.com", thisParsedUrl.hostname);
			},
			testUsernameandPasswordUrlUsername: function() {
				var thisParsedUrl = parsedUrls[3];
				assert.strictEqual("henry", thisParsedUrl.user);
			},
			testUsernameandPasswordUrlpassword: function() {
				var thisParsedUrl = parsedUrls[3];
				assert.strictEqual("abc456", thisParsedUrl.password);
			},
			testUsernameNoPasswordUrl: function() {
				var thisParsedUrl = parsedUrls[4];
				assert.strictEqual("henry", thisParsedUrl.user);
			},
			testRelativeUrlProtocol: function() {
				var thisParsedUrl = parsedUrls[5];
				assert.strictEqual("", thisParsedUrl.protocol);
			},
			testRelativeUrlHostname: function() {
				var thisParsedUrl = parsedUrls[5];
				assert.strictEqual("", thisParsedUrl.host);
			},
			testRelativeUrlHost: function() {
				var thisParsedUrl = parsedUrls[5];
				assert.strictEqual("", thisParsedUrl.hostname);
			},
			testRelativeUrlPort: function() {
				var thisParsedUrl = parsedUrls[5];
				assert.strictEqual("", thisParsedUrl.port);
			},
			testRelativeUrlHash: function() {
				var thisParsedUrl = parsedUrls[5];
				assert.strictEqual("", thisParsedUrl.hash);
			},
			testRelativeUrlHashClean: function() {
				var thisParsedUrl = parsedUrls[5];
				assert.strictEqual("", thisParsedUrl.hashClean);
			},
			testRelativeUrlSearch: function() {
				var thisParsedUrl = parsedUrls[5];
				assert.strictEqual("", thisParsedUrl.search);
			},
			testRelativeUrlSearchArray: function() {
				var thisParsedUrl = parsedUrls[5];
				var isNullParsedURL = thisParsedUrl.searchArray;
				// var isNull = (null === isNullParsedURL);
				assert.isTrue(isNullParsedURL === null);
			},
			testRelativeUrlPathName: function() {
				var thisParsedUrl = parsedUrls[5];
				assert.strictEqual("index.html", thisParsedUrl.pathname);
			},
			testRelativeUrlUserUndefined: function() {
				var thisParsedUrl = parsedUrls[5];
				assert.isUndefined(thisParsedUrl.user);
			},
			testServerRelativeUrlHost: function() {
				var thisParsedUrl = parsedUrls[6];
				assert.strictEqual("", thisParsedUrl.host);
			},
			testServerRelativeUrlHostName: function() {
				var thisParsedUrl = parsedUrls[6];
				assert.strictEqual("", thisParsedUrl.hostname);
			},
			testServerRelativeUrlPathName: function() {
				var thisParsedUrl = parsedUrls[6];
				assert.strictEqual("/index.html", thisParsedUrl.pathname);
			},
			testServerRelativeUrlPathNameArray: function() {
				var thisParsedUrl = parsedUrls[6];
				var expected = ["index.html"];
				assert.deepEqual(expected, thisParsedUrl.pathnameArray);
			},
			testHashUrlhost: function() {
				var thisParsedUrl = parsedUrls[7];
				assert.strictEqual("", thisParsedUrl.host);
			},
			testHashUrlhostname: function() {
				var thisParsedUrl = parsedUrls[7];
				assert.strictEqual("", thisParsedUrl.hostname);
			},
			testHashUrlhash: function() {
				var thisParsedUrl = parsedUrls[7];
				assert.strictEqual("#only", thisParsedUrl.hash);
			},
			testHashUrlhashClean: function() {
				var thisParsedUrl = parsedUrls[7];
				assert.strictEqual("only", thisParsedUrl.hashClean);
			},
			testHashOnlyUrlhash: function() {
				var thisParsedUrl = parsedUrls[8];
				assert.strictEqual("", thisParsedUrl.hash);
			},
			testHashOnlyUrlhashClean: function() {
				var thisParsedUrl = parsedUrls[8];
				assert.strictEqual("", thisParsedUrl.hashClean);
			},
			testFTPUrlProtocol: function() {
				var thisParsedUrl = parsedUrls[9];
				assert.strictEqual("ftp:", thisParsedUrl.protocol);
			},
			testFTPUrlPort: function() {
				var thisParsedUrl = parsedUrls[9];
				assert.strictEqual("", thisParsedUrl.port);
			},
			testUrlFunnyAmpSearch: function() {
				var thisParsedUrl = parsedUrls[10];
				assert.strictEqual("?arg1&arg2=4.0&arg3", thisParsedUrl.search);
			},
			testUrlFunnyAmpSearchArray: function() {
				var thisParsedUrl = parsedUrls[10];
				var searchArray = thisParsedUrl.searchArray;
				assert.strictEqual(null, searchArray.arg1);
			},
			testUrlFunnyAmpSearchArray2: function() {
				var thisParsedUrl = parsedUrls[10];
				var searchArray = thisParsedUrl.searchArray;
				assert.strictEqual("4.0", searchArray.arg2);
			},
			testUrlFunnyAmpSearchArray3: function() {
				var thisParsedUrl = parsedUrls[10];
				var searchArray = thisParsedUrl.searchArray;
				assert.strictEqual(null, searchArray.arg3);
			},
			testMailtoUrlProtocol: function() {
				var thisParsedUrl = parsedUrls[11];
				assert.strictEqual("mailto:", thisParsedUrl.protocol);
			},
			testMailtoUrlUser: function() {
				var thisParsedUrl = parsedUrls[11],
					expected = "banjo";
				assert.strictEqual(expected, thisParsedUrl.user);
			},
			testMailtoUrlHost: function() {
				var thisParsedUrl = parsedUrls[11];
				assert.strictEqual("gmail.com", thisParsedUrl.host);
			},
			testMailtoUrlHostArray: function() {
				var thisParsedUrl = parsedUrls[11],
					expected = ["gmail", "com"];
				assert.deepEqual(expected, thisParsedUrl.hostnameArray);
			},
			testMailtoUrlNullSearchArray: function() {
				var thisParsedUrl = parsedUrls[11];
				assert.strictEqual(null, thisParsedUrl.searchArray);
			},
			testParseUrlCompRelPathArray: function() {
				var thisParsedUrl = parsedUrls[12],
					expected = ["find", "the", "wombat.html"];
				assert.deepEqual(expected, thisParsedUrl.pathnameArray);
			},
			testUrlNoPortPort: function() {
				var thisParsedUrl = parsedUrls[13];
				assert.strictEqual("", thisParsedUrl.port);
			},
			testUrlNoPortSearch: function() {
				var thisParsedUrl = parsedUrls[13];
				assert.strictEqual("?arg1=2", thisParsedUrl.search);
			},
			testUrlNoPortHost: function() {
				var thisParsedUrl = parsedUrls[13];
				assert.strictEqual("www.google.com", thisParsedUrl.host);
			},
			testUrlNoPortHostname: function() {
				var thisParsedUrl = parsedUrls[13];
				assert.strictEqual("www.google.com", thisParsedUrl.hostname);
			},
			testUrlFunnyQuerySearch: function() {
				var thisParsedUrl = parsedUrls[14];
				assert.strictEqual("", thisParsedUrl.search);
			},
			testUrlFunnyQuerySearchArray: function() {
				var thisParsedUrl = parsedUrls[14];
				assert.strictEqual(null, thisParsedUrl.searchArray);
			},
			testUrlFunnyQuerySearchArray1: function() {
				var thisParsedUrl = parsedUrls[15];
				assert.strictEqual("", thisParsedUrl.protocol);
			},
			testUrlMatchesBrowser: function() {
				var parsed = urlParser.parse(window.location.href);
				// assert.strictEqual(window.location.hash, parsed.hash);  // Internet Explorer gets this wrong
				assert.strictEqual(window.location.host, parsed.host, "host");
				assert.strictEqual(window.location.hostname, parsed.hostname, "hostname");
				assert.strictEqual(window.location.pathname, parsed.pathname, "pathname");
				assert.strictEqual(window.location.port, parsed.port, "port");
				assert.strictEqual(window.location.protocol, parsed.protocol, "protocol");
				assert.strictEqual(window.location.search, parsed.search, "search");
			}
		});
	});
