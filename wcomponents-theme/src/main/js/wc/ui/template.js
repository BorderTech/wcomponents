define(["wc/template", "wc/dom/initialise", "wc/i18n/i18n!"], function (template, initialise) {
	/*
	 * This module exists because QC158400.
	 */
	initialise.register({
		/**
		 * Very early initialisation to do page-load-time i18n on forms (WApplications).
		 * @function module:wc/template.preInit
		 * @public
		 */
		preInit: function () {
			var documentElement = document.documentElement;
			if (documentElement) {
				// probably should stick this attribute on each form instead
				if (documentElement.getAttribute("data-wc-rendered") === "server") {
					console.log("Server side rendered");
					return;  // it was alredy rendered on the server
				}
				else {
					documentElement.setAttribute("data-wc-rendered", "client");
				}
			}
			Array.prototype.forEach.call(document.getElementsByTagName("form"), function (form) {
				template.process({ source: form });
			});
		}
	});
	return 1;
});
