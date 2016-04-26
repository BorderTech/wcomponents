/**
 * Experimental clock widget
 */
define(["lib/date", "wc/ajax/ajax", "wc/loader/resource", "wc/dom/textContent", "wc/timers", "wc/config",
	"wc/date/Format", "wc/date/interchange"],
	function(date, ajax, resource, textContent, timers, wcconfig, Format, interchange) {
		var defaultFormat = "dd MMM yyyy, hh:mm a",
			defaultFormatWithSeconds = "dd MMM yyyy, hh:mm:ss a",
			instance = new Clock(),
			config = wcconfig.get("wc/ui/clock");

		date.timezone.zoneFileBasePath = resource.getResourceUrl() + "timezone";
		if (config && config.defaultZone) {
			date.timezone.defaultZoneFile = config.defaultZone;
		}
		else {
			date.timezone.defaultZoneFile = ["australasia"];
		}

		date.timezone.transport = function(request) {
//			var url = request.url;
//			if (/\/[^\.]+$/.test(url)) {
//				request.url += ".txt";
//			}
			request.cache = true;
			request.responseType = ajax.responseType.TEXT;
			request.onError = request.error;
			request.callback = request.success;
			ajax.simpleRequest(request);
		};

		function startTicking(config) {
			var formatter,
				start = config.start || Date.now(),
				result = {
					date: new date.Date(start, config.timezone),
					timer: null
				};
			if (config.format) {
				formatter = new Format(config.format);
			}
			else {
				formatter = new Format(config.seconds ? defaultFormatWithSeconds : defaultFormat);
			}
			tick();
			function tick() {
				var nextTick,
					element,
					parsed = interchange.fromDate(result.date, true),
					dateString = formatter.format(parsed);

				if (config.id && (element = document.getElementById(config.id))) {
					textContent.set(element, dateString);
				}

				if (config.callback) {
					try {
						config.callback(dateString, result.date);
					}
					catch (ex) {
						config.callback = null;
						console.error(ex);
					}
				}

				if (config.seconds) {
					nextTick = 1000;
				}
				else {
					nextTick = 60 - result.date.getSeconds();
					nextTick *= 1000;
				}

				result.date.setTime(result.date.getTime() + nextTick);
				result.timer = timers.setTimeout(tick, nextTick);
			}
			return result;
		}

		function processClocks(clocks) {
			var i, next, len = clocks.length;
			for (i = 0; i < len; i++) {
				next = clocks[i];
				startTicking(next);
			}
		}

		/**
		 *
		 * @constructor
		 */
		function Clock() {
			var inited = false;

			this.register = function(clocks) {
				if (!inited) {
					date.timezone.init({ callback: function() {
						inited = true;
						processClocks(clocks);
					}});
				}
				else {
					processClocks(clocks);
				}
			};
		}
		return instance;
	});
