(function(){
	var ajaxInterecepts = 0,
		pendingAjaxCount = 0;

	(function(){
		/**
		 * This script is intended to help Selenium tests determine if the page is "ready".
		 * "Ready" means:
		 *	- The page is loaded (duh)
		 *	- There are no queued scripts about to execute (i.e. short timeouts)
		 *	- There are no pending AJAX requests.
		 *
		 * Selenium simply needs to call <code>window.isPageReady();</code> to determine
		 * if the page is ready.
		 *
		 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
		 * The vast majority of the code here is for Internet Explorer which makes it very
		 * difficult to wrap ActiveX objects.
		 *
		 * Unfortunately Internet Explorer does not allow us to subclass ActiveX objects
		 * by adding them to the prototype chain of a constructor.
		 *
		 * Also we cannot simply override the send method on the object because Internet Explorer
		 * tries to execute "send" even if you just reference it like so "o.send" with no parentheses.
		 *
		 * Also Internet Explorer does not let you use Object.defineProperty on ActiveX objects.
		 *
		 * AWESOME! Therefore we must write a facade that overrides every method even if we only want
		 * to override just one of them. We must also override properties and keep them in sync with
		 * the underlying ActiveX object.
		 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
		 */
		var ActiveXConstructor = window.ActiveXObject,
			isIe = /MSIE (\d)/.test(window.navigator.userAgent),
			XMLHttpRequestConstructor = window.XMLHttpRequest,
			AJAX_ACTIVEX_RE = /.+\.(XMLHTTP)/,//|FreeThreadedDomDocument|DOMDocument
			canLog = !!window.console,
			XMLHTTPRequestMethods =  ["abort", "getAllResponseHeaders", "getResponseHeader", "open", "send", "setRequestHeader",
				"overrideMimeType", "init", "openRequest", "sendAsBinary",
				"setProperty", "loadXML", "load"],
			XMLHTTPRequestProps = ["readyState", "responseBody", "responseText", "responseXML", "status", "statusText", "timeout",
				"response", "responseType", "upload", "withCredentials", "channel", "multipart",
				"resolveExternals", "async"];

		if(ActiveXConstructor)
		{
			window.ActiveXObject = function(servernametypename, location)
			{
				var result;

				if(location)
				{
					result = new ActiveXConstructor(servernametypename, location);
				}
				else
				{
					result = new ActiveXConstructor(servernametypename);
				}
				try
				{
					if(AJAX_ACTIVEX_RE.test(servernametypename))
					{
						result = wrapXmlHttp(result, XMLHTTPRequestMethods, XMLHTTPRequestProps);
					}
				}
				catch(ex)
				{
					if(canLog)
					{
						window.console.error(ex);
					}
				}
				return result;
			};
		}

		if(XMLHttpRequestConstructor)
		{
			window.XMLHttpRequest = function()
			{
				return wrapXmlHttp(new XMLHttpRequestConstructor(), XMLHTTPRequestMethods, XMLHTTPRequestProps);
			};
		}

		/**
		 *
		 * OMG the object which provides the facade is a DIV! That's because we need to override properties
		 * and you can't have accessor properties on POJOs in Internet Explorer. So we use a div instead
		 * which CAN have accessor properties.
		 * Of course this is the EXACT opposite in Safari 5 (but that can be patched using defineGetter and d
		 * efineSetter)
		 */
		function wrapXmlHttp(xmlHttp, methods, props)
		{
			var i, result = isIe? document.createElement("div") : {},
				prop;

			eventHandler("onreadystatechange");
			//eventHandler("ontimeout");


			for(i=props.length - 1; i >= 0; i--)
			{
				prop = props[i];
				Object.defineProperty(result, prop, {get: accessorFactory(prop), set: accessorFactory(prop)});
			}

			for(i=methods.length - 1; i >= 0; i--)
			{
				prop = methods[i];
				if(typeof xmlHttp[prop] !== "undefined")
				{
					result[prop] = methodFactory(xmlHttp, prop);
				}
			}

			function eventHandler(onEvent)
			{
				xmlHttp[onEvent] = function(){
					if(onEvent == "ontimeout" || (onEvent == "onreadystatechange" && result.readyState === 4))
					{
						pendingAjaxCount--;
						if(pendingAjaxCount < 0)
						{
							debugger;
						}
						if(canLog)
						{
							console.log("Overridden event handler: ", onEvent);
							console.log("Pending Ajax Requests: ", pendingAjaxCount);
						}
					}
					if(result[onEvent])
					{
						result[onEvent].apply(result, arguments);
					}
				};
			}

			/**
			 * Returns curried functions for use as override methods on the facade which will automatically
			 * call the corresponding methods on the underlying object.
			 */
			function methodFactory(obj, mthd)
			{
				return function(){
					var result;
					try
					{
						if(canLog)
						{
							console.log("Calling overridden method: ", mthd);
						}
						if(mthd === "send" || mthd === "load" || mthd === "loadXML")
						{
							ajaxInterecepts++;
							pendingAjaxCount++;
							if(canLog)
							{
								console.log("Pending Ajax Requests: ", pendingAjaxCount);
							}
						}
						//this switch should NOT be necessary but caters for the awesomeness of IE host objects
						switch(arguments.length){
							case 0:
								result = obj[mthd]();
								break;
							case 1:
								result = obj[mthd](arguments[0]);
								break;
							case 2:
								result = obj[mthd](arguments[0], arguments[1]);
								break;
							case 3:
								result = obj[mthd](arguments[0], arguments[1], arguments[2]);
								break;
							case 4:
								result = obj[mthd](arguments[0], arguments[1], arguments[2], arguments[3]);
								break;
							default:
								throw new Error("Need more cases!");
						}
					}
					catch(ex)
					{
						if(mthd === "send" || mthd === "load" || mthd === "loadXML")
						{
							pendingAjaxCount--;
							if(pendingAjaxCount < 0)
							{
								debugger;
							}
						}
					}
					return result;
				};
			}

			/**
			 * Returns curried accessor methods for use in accessor properties on the facade.
			 */
			function accessorFactory(prop)
			{
				return function(arg){
					var result;
					if(arguments.length)
					{
						result = (xmlHttp[prop] = arg);
					}
					else
					{
						result = xmlHttp[prop];
					}
					return result;
				};
			}
			return result;
		}
	})();


	/**
	 * This code set up the pendingTimeout collection, which
	 * lets us know if there are any pending timeouts.
	 */
	(function(){

		var limit = 510,  // don't make it 500 or less because of ajax.Trigger
			theTrueClearTimeout = window.clearTimeout,
			theTrueSetTimeout = window.setTimeout;

		window.pendingTimeouts = {};

		window.setTimeout = function(func, delay)
		{
			var outerArgs = arguments,
				id,
				timeoutWrapper = function(){
					var i, len, args = [];
					try
					{
						for(i=2, len=outerArgs.length; i<len; i++)
						{
							args[args.length] = outerArgs[i];
						}
						func.apply(this, args);
					}
					finally
					{
						delete window.pendingTimeouts[id];
					}
				};
			id = theTrueSetTimeout(timeoutWrapper, delay);
			if(delay < limit)
			{
				window.pendingTimeouts[id] = true;
			}
			return id;
		};

		window.clearTimeout = function(id)
		{
			delete window.pendingTimeouts[id];
			return theTrueClearTimeout(id);
		};
	}());

	/**
	 * Adds event listeners to the load and submit events to
	 * indicate when the page is loaded / about to be unloaded.
	 */
	(function(){

		addEvent(window, "load", loadEvent);

		function loadEvent()
		{
			  window.setTimeout(function(){
					var i, forms = document.forms;
					//dynamically loaded forms will not be wired up in IE due to submit event not propagating
					for(i=forms.length-1; i>=0; i--)
					{
						addEvent(forms[i], "submit", submitEvent);
					}
					window.pageLoaded = true;
				}, 5);
		}

		function submitEvent()
		{
			  window.pageLoaded = false;
		}

		function addEvent(element, type, listener)
		{
			if (window.addEventListener)
			{
				element.addEventListener(type, listener, false);
			}
			else if (window.attachEvent)
			{
				element.attachEvent("on" + type, listener);
			}
		}

	}());

	/**
	 * "Maximizes" the window, as Selenium sometimes has issues
	 * interacting with controls which are partially visible.
	 */
	(function(){
		window.moveTo(0,0);
		window.resizeTo(screen.width,screen.height);
	}());

	function getPendingTimeoutCount()
	{
		var prop, pendingTimeoutCount;
		if(Object.keys)
		{
			pendingTimeoutCount = Object.keys(window.pendingTimeouts).length;
		}
		else
		{
			pendingTimeoutCount = 0;
			for(prop in window.pendingTimeouts)
			{
				if(window.pendingTimeouts.hasOwnProperty(prop))
				{
					pendingTimeoutCount++;
				}
			}
		}
		return pendingTimeoutCount;
	}

	if(window.isPageReady)
	{
		throw new Error("LOADED SELENIUMHELPER TWICE!");
	}

	/**
	 * Indicates whether the page is ready for user interaction.
	 * This is called by the WComponent Selenium Webdriver wrappers.
	 */
	window.isPageReady = function isPageReady()
	{
		var pendingTimeoutCount = getPendingTimeoutCount();
		if(window.console)
		{
			console.log(window.isPageReady.status());
		}
		return window.pageLoaded && pendingTimeoutCount === 0 && !pendingAjaxCount;
	}

	window.isPageReady.status = function(){
		return ["pageLoaded: ", window.pageLoaded,
			"\npendingTimeoutCount: ", getPendingTimeoutCount(),
			"\npendingAjaxCount: ", pendingAjaxCount,
			"\nAjaxIntercepts: ", ajaxInterecepts].join("");
	};

	/* useful when testing this script... turn on modem speeds in fiddler to see it work...
	(function(){

		window.setInterval(function(){
			if(!isPageReady())
			{
				document.body.style.background = "red";
			}
		}, 300);

		window.setInterval(function(){
				if(isPageReady())
				{
					document.body.style.background = "green";
				}
			}, 2000);
	})();
	*/
})();