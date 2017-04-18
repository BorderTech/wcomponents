require(["wc/template", "wc/dom/initialise", "wc/dom/removeElement"], function (template, initialise, removeElement) {
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
			var templateSrc = document.getElementById("ui:root"),
				templateTarget;
			if (templateSrc) {
				templateTarget = document.getElementById("wc-root");
				template.process({ source: templateSrc, target: templateTarget });
				removeElement(templateSrc);
				return;
			}
			Array.prototype.forEach.call(document.getElementsByTagName("form"), function (form) {
				template.process({ source: form });
			});
		}
	});
});
