/**
 * This module removes the need to know which implementation of has we are using.
 * @module
 * @todo Make a decision and remove this!
 */
define(["${wc.amd.has}"], /** @param {Object?} [has] @ignore */function(has) {
	"use strict";
	var global = window;
	return global.has || has;
});
