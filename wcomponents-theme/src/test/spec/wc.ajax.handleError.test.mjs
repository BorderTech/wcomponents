import wcconfig from "wc/config.mjs";
import i18n from "wc/i18n/i18n.mjs";
import handleError from "wc/ajax/handleError.mjs";

describe("wc/ajax/handleError", () => {

	/**
	 *
	 * @param {number} status
	 * @param {string} responseText
	 * @param {string} [statusText]
	 * @return {{responseText: string, statusText: string, status: number }}
	 */
	function getMockResponse(status, responseText, statusText) {
		return {
			status,
			responseText,
			statusText
		};
	}

	beforeAll(function() {
		["wc/ui/xhr", "wc/ui/multiFileUploader"].forEach(id => {
			const realConfig = wcconfig.get(id);
			if (realConfig?.messages) {
				wcconfig.set({ messages: null }, id);
			}
		});
	});
		

	it("testFaux500", function() {
		const expected = "500 response text",
			response = getMockResponse(500, expected),
			actual = handleError.getErrorMessage(response);
		expect(actual).toBe(expected);
	});

	it("testFaux500WithStatusText", function() {
		const expected = "500 status text",
			response = getMockResponse(500, null, expected),
			actual = handleError.getErrorMessage(response);
		expect(actual).toBe(expected);
	});

	it("testSilly200", function() {
		const expected = i18n.get("xhr_errormsg"),
			response = getMockResponse(200, "foo", "bar"),
			actual = handleError.getErrorMessage(response);
		expect(actual).toBe(expected);
	});

	it("testConfig", function() {
		let expected, response, actual;
		try {
			wcconfig.set({ messages: {
				403: "Oh noes! A 403 occurred!",
				404: "I can't find it!",
				418: function(resp) {
					// this is an example of handling a JSON response body
					let data;
					try {
						data = JSON.parse(resp.responseText);
						data = data.message;
					} catch (ex) {
						data = resp.responseText;
					}
					return data + " " + resp.status;
				},
				200: "Some gateway proxies don't know basic HTTP",
				error: "An error occurred and I have not set a specific message for it!"
			}}, "wc/ui/xhr");

			// 403
			expected = "Oh noes! A 403 occurred!";
			response = getMockResponse(403, "foo", "bar");
			actual = handleError.getErrorMessage(response);
			expect(actual).toBe(expected);
			// 404
			expected = "I can't find it!";
			response = getMockResponse(404, "foo", "bar");
			actual = handleError.getErrorMessage(response);
			expect(actual).toBe(expected);
			// 418
			expected = "Short and stout 418";
			response = getMockResponse(418, "{ \"message\": \"Short and stout\" }", "I'm a teapot");
			actual = handleError.getErrorMessage(response);
			expect(actual).toBe(expected);
			// 200
			expected = "Some gateway proxies don't know basic HTTP";
			response = getMockResponse(200, "foo", "bar");
			actual = handleError.getErrorMessage(response);
			expect(actual).toBe(expected);
			// 500
			expected = "An error occurred and I have not set a specific message for it!";
			response = getMockResponse(500, "foo", "bar");
			actual = handleError.getErrorMessage(response);
			expect(actual).toBe(expected);

		} finally {
			wcconfig.set(null, "wc/ui/xhr");
		}
	});
});
