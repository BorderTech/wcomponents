import escapeRe from "wc/string/escapeRe.mjs";

describe("wc/string/escapeRe", function() {

	it("testEscapeReString", function() {
		const testString = "a.b|c*d?e+f(g)h{i}j[k]l^m$n\\o",  // have to double escape backslashes,
			expected = "a\\.b\\|c\\*d\\?e\\+f\\(g\\)h\\{i\\}j\\[k\\]l\\^m\\$n\\\\o",  // have to double escape backslashes,
			result = escapeRe(testString);
		expect(result).toBe(expected);
	});

	it("testEscapeReStringWithWildcard", function() {
		const testString = "a.b|c*d?e+f(g)h{i}j[k]l^m$n\\o",  // have to double escape backslashes,
			expected = "a\\.b\\|c.*d\\?e\\+f\\(g\\)h\\{i\\}j\\[k\\]l\\^m\\$n\\\\o",  // have to double escape backslashes,
			result = escapeRe(testString, true);
		expect(result).toBe(expected);
	});

});
