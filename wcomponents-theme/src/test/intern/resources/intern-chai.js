/**
 * This module exists to provide AMD compatibility when moving from intern 3 to 4.
 */
define(function() {
	var chai;
	return {
		load: function (id, parentRequire, callback/* , config */) {
			chai = chai || intern.getPlugin("chai");
			if (id === "assert") {
				callback(chai.assert);
			} else {
				callback(chai);
			}
		}
	};
});
