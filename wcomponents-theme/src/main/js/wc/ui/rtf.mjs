/**
 * Provides a Rich Text Field implementation using tinyMCE.
 *
 * Optional module configuration.
 * The config member "initObj" can be set to an abject containing any tinyMCE cofiguration members **except**
 * selector. This allows customised RTF per implementation. This should be added in the template
 */
import initialise from "wc/dom/initialise";
import wcconfig from "wc/config";
import styleLoader from "wc/loader/style";
import tinyMCE from "tinyMCE";

/**
 * Call when DOM is ready to initialise rich text fields.
 *
 * @function
 * @private
 * @param {String[]} idArr An array of RTF ids.
 */
function processNow(idArr) {
	const config = wcconfig.get("wc/ui/rtf", {
		initObj: {
			content_css: styleLoader.getMainCss(true),
			menubar: false,
			plugins: "autolink link lists advlist preview help",
			toolbar: 'undo redo | formatselect | ' +
				' bold italic | alignleft aligncenter ' +
				' alignright alignjustify | bullist numlist outdent indent |' +
				' removeformat | help',
			setup: function (editor) {
				editor.on("change", function () {
					tinyMCE.triggerSave();
				});
			}
		}
	});
	let id;
	while ((id = idArr.shift())) {
		config.initObj["selector"] = `textarea#${id}_input`;
		tinyMCE.init(config.initObj);
	}
}

export default {
	/**
	 * Register Rich Text Fields that need to be initialised.
	 *
	 * @function
	 * @public
	 * @param {String[]} idArr An array of element ids.
	 */
	register: function(idArr) {
		if (idArr?.length) {
			initialise.addCallback(() => processNow(idArr));
		}
	}
};
