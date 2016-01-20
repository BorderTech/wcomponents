/**
 * Executes XSLT on XML.
 *
 * The vast majority of this code exists to deal with the nasty bugs and evil complexities of doing
 * XSLT in javascript in different browsers.
 *
 * Only Firefox "just works" and does not need the majority of the code here.
 *
 * @todo move fix code into "fix classes" for the IE and webkit so it is not loaded at all in Firefox.
 *
 * @module
 * @requires module:wc/has
 * @requires module:wc/ajax/ajax
 * @requires module:wc/xml/xmlString
 * @requires module:wc/xml/xpath
 * @requires module:wc/array/toArray
 *
 * @todo Document private members.
 */
define(["wc/has", "wc/ajax/ajax", "wc/xml/xmlString", "wc/xml/xpath", "wc/array/toArray", "module"],
	/** @param has wc/has @param ajax wc/ajax/ajax @param xmlString wc/xml/xmlString @param xpath wc/xml/xpath @param toArray wc/array/toArray @param module module @ignore */
	function(has, ajax, xmlString, xpath, toArray, module) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/xml/xslTransform~XslTransformer
		 * @private
		 */
		function XslTransformer() {
			var $this = this,
				sandbox,
				memoizedApplyXsl = applyXsl,
				xslFileCache = {},
				document;  // deliberately shadow global document


			/**
			 * This is an IE hack and is simply a waste of time to call in other browsers.
			 * Should load any resources (images) EXCEPT scripts, with requests that include cookies.
			 * Assumption is that these resources are cached, at least for the life of this page.
			 * Any external scripts will not be preloaded.
			 *
			 * @function
			 * @private
			 * @param {String} html A block of HTML to preprocess for IE.
			 */
			function preloadResources(html) {
				var preloadDf = document.createDocumentFragment(),
					container,
					SCRIPTRE = /<script.*?>.*?<\/script>/gi;  // html parsing in JS anyone?
				html = html.replace(SCRIPTRE, "");  // strip scripts
				container = document.createElement("div");
				preloadDf.appendChild(container);
				// container.innerHTML = html;
				container.insertAdjacentHTML("afterbegin", html);
			}

			/**
			 * Helper for resolveIncludes - used to merge the import/include href with the parent xsl href.
			 * Only needs to work in webkit.
			 * @param {String} ownerHref The path to the importing xsl file.
			 * @param {String} href The href of the import/include
			 * @returns {String} The url to use to load the import/include
			 */
			function mergeHref(ownerHref, href) {
				var result;
				if (href) {
					if (href[0] === "/") {
						result = href;
					}
					else {
						result = ownerHref + href;
					}
				}
				return result;
			}

			function setParameters(processor, params, clear, ieMode) {
				var key, keys, i;
				if (params) {
					keys = Object.keys(params);
					for (i = 0; i < keys.length; i++) {
						key = keys[i];
						if (clear) {
							if (ieMode) {
								processor.reset();
								break;
							}
							processor.removeParameter(null, key);
						}
						else if (ieMode) {
							processor.addParameter(key, params[key]);
						}
						else {
							processor.setParameter(null, key, params[key]);
						}
					}
				}
			}

			/*
			 * @param {String} [_xslUri] The URI which identifies this XSL (i.e. where you loaded the
			 * XML from). This is primarily used as a cache key, but in IE it may also be used to
			 * work around some major fail in the XML API.
			 * This is not to do with caching the results of HTTP requests, that is left to the browser.
			 * This is to do with the actual parsing and compiling of an XSL once it has been fetched.
			 */
			function applyXsl(_xml, _xsl, _asHtml, _xslUri, _params) {
				var result, compiledXslCache = {}, IEActiveXVersion;

				function leetApplyXsl(xml, xsl, asHtml, xslUri, params) {
					var xsltProcessor;
					if (!xslUri || !(xsltProcessor = compiledXslCache[xslUri])) {
						xsltProcessor = new window.XSLTProcessor();
						if (!has("ff")) {
							/*
							 * WARNING DO NOT CACHE THE XSLTPROCESSOR IN FIREFOX!
							 * It causes a race condition that oftentimes throws the following error:
							 * "(NS_ERROR_NOT_INITIALIZED) [nsIXSLTProcessor.transformToFragment]"
							 * You won't find it in your regular testing, but it DOES happen and we are able to replicate it frequently.
							 * Tested on multiple versions of Firefox, up to 21.
							 *
							 * Just don't do it, it's not worth it.
							 */
							if (xslUri) {
								compiledXslCache[xslUri] = xsltProcessor;
								console.log("Caching compiled XSL: ", xslUri);
							}
							if (has("edge") || has("webkit")) { // the webkit test: chrome on iOS and android still needs this, Safari iOS and UC do not but it does them no harm.
								resolveIncludes(xsl);
							}
						}
						xsltProcessor.importStylesheet(xsl);
					}
					setParameters(xsltProcessor, params, false);
					result = xsltProcessor.transformToFragment(xml, document);
					setParameters(xsltProcessor, params, true);
					return result;
				}

				/*
				 * Resolves "xsl:include" for browsers that can't do it themselves.
				 * The main culprit is webkit. Chrome (15) appears to honor xsl:includes however it
				 * eventually crashes (for example if you do multiple ajax calls on one page it will
				 * eventually die), so don't remove this code if all you do is a simple test with
				 * just a few invocations here and there.
				 * Follows "xsl:include" href uris and loads the XSL stylesheets they point to.
				 * TODO Properly detect absolute/relative/server relative href and merge accordingly.
				 * TODO Do we need to handle elements which will clash (duplicate) existing ones when
				 * included (for example templates/variables/params with the same name, xsl:output and
				 * strip space directives etc)?
				 *
				 * @param {Object} xsl An XSL DOM (it will be modified if it has xsl:include elements)
				 *
				 * In case you were wondering:
				 * <q cite="http://www.w3.org/TR/xslt#stylesheet-element">
				 * The order in which the children of the xsl:stylesheet element occur is not significant
				 * except for xsl:import
				 * <q>
				 *@param {boolean} ignoreMetaElements if true then xsl:output and xsl:strip-spaces are ignored
				 */
				function resolveIncludes(xsl, ignoreMetaElements) {
					var ownerHref, doc,
						attributesToQuery = function(attr) {
							return "[@" + attr.name + "='" + attr.value + "']";
						},
						included = xpath.query("//xsl:include|//xsl:import", false, xsl);
					if (included && included.length) {
						console.info("Applying webkit xsl:include fix");
						ownerHref = module.config().xslUrl.replace(/\/[^\/]+$/, "/");
						included.forEach(function(nextInclude) {
							var next, parent, docRoot, i, dupQuery, dupElement,
								href = mergeHref(ownerHref, nextInclude.getAttribute("href"));
							doc = doc || ajax.loadXmlDoc(href);
							if (doc && (docRoot = doc.documentElement) && docRoot.localName === "stylesheet") {
								parent = nextInclude.parentNode;  // this will be the same as xsl.documentElement
								for (i = 0; i < docRoot.attributes.length; i++) {
									// we are interested in copying any xmlns attributes we might need
									next = docRoot.attributes[i];
									if (!parent.hasAttribute(next.name)) {
										parent.setAttribute(next.name, next.value);
									}
								}
								while ((next = docRoot.firstChild)) {
									if (ignoreMetaElements && (next.localName === "strip-space" || next.localName === "output")) {
										docRoot.removeChild(next);
									}
									// now copy every child from the imported xsl directly into the main xsl
									else if (next.localName !== "include" && next.localName !== "import") {
										xsl.adoptNode(next);
										if (next.localName === "template" && next.hasAttribute("name")) {
											dupQuery = toArray(next.attributes).map(attributesToQuery);
											dupQuery = "//xsl:template" + dupQuery.join("");
											dupElement = xpath.query(dupQuery, true, xsl);
											if (dupElement) {
												parent.removeChild(dupElement);
											}
										}
										parent.insertBefore(next, nextInclude);
									}
									else {
										resolveIncludes(doc, true);
									}
								}
								parent.removeChild(nextInclude);
							}
							else {
								console.warn("Could not include XSL: ", href);
							}
						});
					}
				}

				function ieApplyXsl(xml, xsl, asHtml, xslUri, params) {
					var xslSafe, xmlSafe, compiledXsl, xsltProcessor, result;
					xslUri = xsl.url || xslUri;
					if (IEActiveXVersion === undefined) {
						IEActiveXVersion = getActiveX("MSXML2.FreeThreadedDomDocument", ["6.0", "3.0"]);
						if (IEActiveXVersion && IEActiveXVersion.version) {
							IEActiveXVersion = "." + IEActiveXVersion.version;
						}
						else {
							IEActiveXVersion = "";
						}
					}
					if (!xslUri || !(compiledXsl = compiledXslCache[xslUri])) {
						xsl.setProperty("SelectionLanguage", "XPath");
						xsl.setProperty("SelectionNamespaces", "xmlns:xsl='http://www.w3.org/1999/XSL/Transform'");
						xslSafe = new window.ActiveXObject("MSXML2.FreeThreadedDomDocument" + IEActiveXVersion);  // convert to an engine we know will work with XSLTemplate
						xslSafe.async = false;
						xslSafe.setProperty("ForcedResync", false);  // allow it to use cache, default is true
						try {
							xslSafe.resolveExternals = true;
							xslSafe.setProperty("AllowDocumentFunction", true);
						}
						catch (ignore) {
							// AllowDocumentFunction may throw an exception on older MSXML engines but we can ignore this.
						}
						/*
						 * If the XSL dom was loaded from a URL and it imports or includes other XSL files
						 * we need to reload it to be sure these directives are honored.
						 */
						if (xslUri && xsl.selectSingleNode("//xsl:import|//xsl:include")) {
							xslSafe.load(xslUri);
						}
						else {
							/*
							 * Beware, the href for any includes or imports in here will (obviously
							 * if you think about it) be relative to the current page!
							 */

							xslSafe.loadXML(xsl.xml);
						}
						compiledXsl = new window.ActiveXObject("MSXML2.XSLTemplate" + IEActiveXVersion);
						compiledXsl.stylesheet = xslSafe;
						if (xslUri) {
							compiledXslCache[xslUri] = compiledXsl;
							console.log("Caching compiled XSL: ", xslUri);
						}
					}
					// need to convert XML to an engine acceptable for the transform
					xmlSafe = new window.ActiveXObject("MSXML2.DOMDocument" + IEActiveXVersion);
					xmlSafe.async = false;
					xmlSafe.setProperty("ForcedResync", false);
					xmlSafe.loadXML(xml.xml);
					xsltProcessor = compiledXsl.createProcessor();
					xsltProcessor.input = xmlSafe;
					setParameters(xsltProcessor, params, false, true);
					xsltProcessor.transform();
					result = xsltProcessor.output;  // string
					setParameters(xsltProcessor, params, true, true);
					return asHtml ? result : $this.htmlToDocumentFragment(result);
				}

				try {
					/*
					 * A note on the memory freeing code below.
					 * This is not something I just dreamed up. It is based on an profiling the memory heap and this module
					 * rated relatively high in terms of memory usage. Still tiny but so is the cleanup code.
					 * The cleanup code below makes a big difference in reducing the memory footprint of this module.
					 */
					if (has("gecko-xsltprocessor")) {
						memoizedApplyXsl = leetApplyXsl;
						// ieApplyXsl = null;  // free up some memory
						// instance.htmlToDocumentFragment = function() {};  // free up some memory
						result = leetApplyXsl(_xml, _xsl, _asHtml, _xslUri, _params);
					}
					else if (has("activex")) {
						memoizedApplyXsl = ieApplyXsl;
						// leetApplyXsl = null;  // free up some memory
						result = ieApplyXsl(_xml, _xsl, _asHtml, _xslUri, _params);
					}
					return result;
				}
				finally {
					_xml = null;
					_xsl = null;
				}
			}


			/*
			 * @param {Object} args see transform method For a description of args.
			 * @returns {Object} result An object with xsl, xml and uri properties, as specified below:
			 * <ul>
			 *	<li>xsl An XSL Document</li>
			 *	<li>xml An XML Document</li>
			 *	<li>uri A string representing the URL used to load the XSL (if applicable).
			 *		This can be used to cache compiled XSL against etc.</li>
			 * </ul>
			 */
			function parseArgs(args) {
				var nextResult, x, prop, next, promises = [],
					result = { xml: "", xsl: "", uri: "" },
					loadXml = function(url) {
						return ajax.loadXmlDoc(url, null, false, true);
					},
					props = { Uri: loadXml, Doc: null, String: xmlString.from };

				function checkXsl() {
					var promise, promises = [];
					if (!result.xsl) {
						if (result.xml && (result.xsl = $this.getXslHrefFromProcessingInstruction(result.xml))) {
							console.log("xsl from xml processing instruction href");
							result.uri = result.xsl;
							promise = loadXml(result.xsl).then(store(result, "xsl"));
							promises.push(promise);
							// result.xsl = ajax.loadXmlDoc(result.xsl);
						}
						else if ((result.uri = $this.getXslUrl())) {
							if ("${wc.xml.xslTransform.allowCache}") {
								if (!(result.xsl = xslFileCache[result.uri])) {
									promise = loadXml(result.uri);
									promises.push(promise.then(store(result, "xsl")));
									promises.push(promise.then(store(xslFileCache, result.uri)));
								}
							}
							else {
								console.log("xsl from default xsl url");
								promise = loadXml(result.uri).then(store(result, "xsl"));
								promises.push(promise);
							}
						}
					}
					else {
						result.uri = args.xslUri;
					}
					if (promises.length) {
						return Promise.all(promises).then(function() {
							return result;
						});
					}
					return Promise.resolve(result);
				}

				if (args) {
					for (x in result) {
						if (result.hasOwnProperty(x)) {
							for (prop in props) {  // props keys are used to check possible properties in "args"
								if (props.hasOwnProperty(prop)) {
									next = x + prop;  // next will be xmlUri, xmlDoc, xmlString, xslUri, xslDoc, etc.
									if (args[next]) {
										// "props" values are special handlers
										nextResult = props[prop] ? props[prop](args[next]) : args[next];
										if (nextResult instanceof Promise) {
											promises.push(nextResult.then(store(result, x)));
										}
										else {
											result[x] = nextResult;
										}
										console.log("DTO arg: " + next);
										break;
									}
								}
							}
						}
					}
					if (promises.length) {
						return Promise.all(promises).then(checkXsl);
					}
					else {
						return Promise.resolve(checkXsl());
					}
				}
			}

			function store(obj, prop) {
				return function(val) {
					obj[prop] = val;
					return obj;
				};
			}

			/**
			 * Transform the given XML with the given XSL.
			 *
			 *
			 * @function module:wc/xml/xslTransform.transform
			 * @param {Object} args A DTO with one of the following xml and xsl properties (i.e. one each for xsl and xml).
			 * @param {Element} [args.xmlDoc] An XML DOM
			 * @param {String} [args.xmlString] A well formed XML string
			 * @param {String} [args.xmlUri] A URL to an XML file
			 * @param {Element} [args.xslDoc] An XSL DOM
			 * @param {String} [args.xslString] A well formed XSL string. Warning: href of any xsl:include or xsl:import
			 *    will be relative to the current page.
			 * @param {String} [args.xslUri] A URL to an XSL file. Note that the XSL args may be omitted in which case
			 *    we try to fetch: <ol>
			 *    <li>The XSL from a processing instruction in the XML</li>
			 *    <li>The XSL used to transform the page itself.</li>
			 *    </ol>
			 *    Warning! The only truly safe way to load XSL in Internet Explorer is by passing xslUri (or not passing
			 *    anything and letting this class work out the XSL URL). If you pass xslDoc we will attempt to use it.
			 *    The main trouble will occur if your XSL contains xsl:include or xsl:import elements. Even then you are
			 *    not guaranteed to fail (but you are in dangerous waters).
			 * @param {Object} [args.params] A "map" of parameters to set. I.E. An object where each property is the
			 *    name of a param and the property value is the param value. e.g. {xslEngine:'libxslt'}.
			 * @param {boolean} [asHtml] If true will return an HTML string TODO implement for non IE
			 * @param {Document} [doc] Will be used instead of window.document
			 * @returns {(DocumentFragment|String)} The result of the XSL Transformation.
			 */
			this.transform = function(args, asHtml, doc) {
				function executor(win, lose) {
					var result,
						xml,
						xsl;
					try {
						document = doc || window.document;
						parseArgs(args).then(function(parsedArgs) {
							xml = parsedArgs.xml;
							if (xml) {
								xsl = parsedArgs.xsl;
								if (xsl) {
									result = memoizedApplyXsl(xml, xsl, asHtml, parsedArgs.uri, args.params);
									win(result);
								}
								else {
									lose("Could not extract XSL from args");
								}
							}
							else {
								lose("Could not extract XML from args");
							}
							return result;
						});
					}
					finally {
						document = window.document;
					}
				}
				return new Promise(executor);
			};

			/**
			 * Converts an HTML string to a documentFragment.
			 *
			 * This function is highly IE specific. If you need to use it in a browser (unlikely) then you will need to
			 * create a new version.
			 *
			 * The "sandbox" is required to prevent scripts (which try to execute when being inserted into a
			 * documentFragment) from doing any harm. This IS necessary, without it stuff will break under some
			 * conditions that you will not notice for a long time. The rest of this guff, with two document fragments,
			 * the call to html5Fix and the loop through innerHTML is all here to make IE behave. Don't delete this
			 * willy-nilly unless you want all your ajax to die in IE.
			 *
			 * NOTE: A chain of IE issues makes this seemingly simple task very problematic.
			 *
			 * * Document fragments in IE8 are not documentFragments, they are documents.
			 * * IE executes scripts in the documentFragment. This will either:
			 *
			 *    * If not sandboxed in an iframe: cause unwanted side effects before the dom is ready for the script;
			 *    * If not sandboxed in an iframe: throw exceptions that can not be caught, making life difficult if
			 *      script debugging is not disabled.
			 *
			 *    We can prevent the scripts from executing at all by setting the sandbox iframe to
			 *    "security=restricted" BUT this then poses a whole bunch of new problems, see below.
			 * * IE requests resources (images) when they are added to the documentFragment. If we create the
			 *   documentFragment using a "security=restricted" iframe IE still tries to load resources (images) but
			 *   without sending any cookies. This can cause major problems on the server side.
			 *
			 * The corner we are backed into is to use a security restricted sandbox BUT to attempt to preload images
			 * before they are loaded in the sandbox iframe. The assumption is that once the images are loaded into
			 * cache (with a request that contains cookies) the subsequent request (without cookies) will load the image
			 * from cache and the server will never know.
			 *
			 * @function module:wc/xml/xslTransform.htmlToDocumentFragment
			 * @param {String} html The html to convert.
			 * @returns {DocumentFragment} A documentFragment.
			 */
			this.htmlToDocumentFragment = function(html) {  // this is public because we need it in ajaxRegion
				var result,
					tmpDF,
					tmpElement,
					tmpContainer,
					next;
				if (!document) {
					document = window.document;
				}
				result = document.createDocumentFragment();
				/*
				 * I have removed the lines below and it SEEMS to have no ill effects... Perhaps it used to fail due to another condition that no longer exists?
				 * Anyway if this needs to be reinstated it will need to be a lot more complicated because the string we are stripping may well exist as part of
				 * the payload provided by the user for display, for example some XML string to be displayed verbatim on the page. I have included a unit test to
				 * prevent this bug from being reinstated. The two lines below were not well thought out and are not an acceptable solution. It would also blat half
				 * the darn HTML if the xmlns used single quotes instead and there were double quotes elsewhere in the html.
				 *
				 * TWEAKRE = /\sxmlns[^\"]*".*?"/gi;  // Strip out namespace attributes from rendered HTML which break IE if there is an HTML5 element in the html.
				 * html = html.replace(TWEAKRE, "");
				 */
				preloadResources(html);

				if (has("ie") < 9) {
					if (!sandbox || !sandbox.parentNode) {  // check sandbox exists AND is in the DOM
						sandbox = document.createElement("iframe");
						sandbox.setAttribute("style", "display:none");
						sandbox.setAttribute("security", "restricted");
						document.body.appendChild(sandbox);
					}
					tmpDF = sandbox.contentWindow.document.createDocumentFragment();

					if (html5Fix) {
						html5Fix(tmpDF);
					}
				}
				else {
					tmpDF = document.createDocumentFragment();
				}

				tmpElement = (tmpDF.createElement ? tmpDF : document).createElement("div");
				tmpContainer = tmpDF.appendChild(tmpElement);
				if (noScope) {
					tmpContainer.innerHTML = noScope(html);
					noScope(tmpContainer);
				}
				else {
					tmpContainer.innerHTML = html;
				}
				while ((next = tmpContainer.firstChild)) {
					result.appendChild(next);
				}
				return result;
			};

			/**
			 * Extract the XSL URI from processing instructions in the given XML document.
			 *
			 * @function module:wc/xml/xslTransform.getXslHrefFromProcessingInstruction
			 * @param {Element} xml An XML document object.
			 * @returns {String} The url from the href portion of the XSL processing instruction or null
			 */
			this.getXslHrefFromProcessingInstruction = function(xml) {
				var result, doc, instructionNode, tmp,
					hrefRe = /href\s?=\s?"([^"]*)/;  // extract the href from an XSL processing instruction;

				doc = (xml.nodeType === Node.DOCUMENT_NODE) ? xml : xml.ownerDocument;
				if (doc) {
					instructionNode = xpath.query("//processing-instruction('xml-stylesheet')", true, doc);
					if (instructionNode) {
						tmp = hrefRe.exec(instructionNode.nodeValue);
						if (tmp.length > 1) {
							result = tmp[1];
							result = result.replace(/&amp;/g, "&");
						}
					}
				}
				return result || null;
			};

			/**
			 * Get the URL of the core XSLT file.
			 *
			 * @function module:wc/xml/xslTransform.getXslUrl
			 * @returns {String} The url.
			 */
			this.getXslUrl = function() {
				return module.config().xslUrl;
			};
		}

		var /** @alias module:wc/xml/xslTransform */ instance = new XslTransformer(),
			getActiveX = null,
			html5Fix = null,
			noScope = null;

		has.add("gecko-xsltprocessor", function(g) {
			return (typeof g.XSLTProcessor !== "undefined");
		});

		if (has("activex")) {
			getActiveX = require("wc/fix/getActiveX_ieAll");  // this can only work if "wc/fix/getActiveX_ieAll" is already loaded - the compat script must ensure that.
		}

		if (has("ie") < 9) {
			require(["wc/fix/html5Fix_ie8", "wc/fix/noScope_ie8"], function(arg1, arg2) {
				html5Fix = arg1;
				noScope = arg2;
			});
		}
		return instance;
	});
