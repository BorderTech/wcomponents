(function (global) {
	/**
	 * This module removes the need to know which implementation of has we are using.
	 * @module
	 * @todo Make a decision and remove this!
	 */
	define(["lib/dojo/has"], /** @param {Object?} [has] @ignore */function(has) {
		"use strict";
		return global.has || has;
	});
}(this));
