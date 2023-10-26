import aria from "wc/dom/aria.mjs";

/**
 * These tests are taken from aria-toolkit, they were conveniently written for Jasmine way back in 2014.
 * I have reformatted them somewhat to fit our code style.
 *
 * Copyright (C) 2014  Rick Brown
 */
describe("aria", function() {
	/**
	 * These expected arrays are obtained by me manually extracting them from the RDF using xpath queries in OxygenXML.
	 * @type Array
	 */
	const globalStates = ["aria-atomic", "aria-busy", "aria-controls", "aria-describedby",
		"aria-disabled", "aria-dropeffect", "aria-flowto", "aria-grabbed",
		"aria-haspopup", "aria-hidden", "aria-invalid", "aria-label",
		"aria-labelledby", "aria-live", "aria-owns", "aria-relevant"];

	// This fails because the RDF does not reflect the rules in documentation
	// getScopeHelper("menuitem", ["group", "menu", "menubar"]);
	// getScopeHelper("option", ["listbox"]);

	getScopeHelper("listitem", ["list"]);
	getScopeHelper("listbox", []);
	getScopeHelper("radio", []);
	getScopeHelper("button", []);
	getScopeHelper("menuitemcheckbox", ["menu", "menubar"]);
	getScopeHelper("row", ["grid", "rowgroup", "treegrid"]);
	getScopeHelper("foobar", []);
	getScopeHelper("listbox", ["option"], "getMustContain");
	getScopeHelper("combobox", ["listbox", "textbox"], "getMustContain");
	getScopeHelper("option", [], "getMustContain");
	getScopeHelper("checkbox", [], "getMustContain");
	getScopeHelper("menubar", ["menuitem", "menuitemcheckbox", "menuitemradio"], "getScopedTo");
	getScopeHelper("option", ["listbox"], "getScopedBy");


	getSupportedHelper("checkbox", [
		{ name: "aria-checked", value: aria.REQUIRED },
		{ name: "aria-selected", value: undefined }]);

	getSupportedHelper("", [{ name: "aria-checked", value: undefined }]); /* Empty role should return all globals and nothing else */
	getSupportedHelper("foobar", [{ name: "aria-checked", value: undefined }]); /* Nonsense role should return all globals and nothing else */

	function getSupportedHelper(role, expected) {
		const actual = aria.getSupported(role),
			message = " for '" + role + "' role should be ";
		it("all roles should support global attributes", function() {
			globalStates.forEach(function(next) {
				expect(actual[next]).withContext(`${role}.${next}`).toEqual(aria.SUPPORTED);
			});
		});
		if (expected) {
			expected.forEach(function(next) {
				const msg = next.name + message + next.value;
				it (msg, function() {
					expect(actual[next.name]).toEqual(next.value);
				});
			});
		}
	}

	function getScopeHelper(role, expected, funcName) {
		const method = funcName || "getScope",
			isArray = Array.isArray(role);
		let message = method + " for '" + (isArray? role.join(): role) + "' role should return ";
		if (expected) {
			expected.sort();
			message += expected.length ? expected.join() : "an empty array";
		}
		it (message, function() {
			const actual = isArray? aria[method].apply(aria, role) : aria[method](role);
			expect(actual.sort()).toEqual(expected);
		});
	}
});
