require(["wc/template", "wc/dom/initialise"], function (template, initialise) {
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
			Array.prototype.forEach.call(document.getElementsByTagName("form"), function (form) {
				template.process({ source: form });
			});
		}
	});
});
