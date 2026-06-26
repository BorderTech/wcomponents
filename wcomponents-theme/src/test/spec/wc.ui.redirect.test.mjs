import redirect from "wc/ui/redirect.mjs";

const { afterAll, beforeEach, describe, document, expect, it, spyOn, spyOnProperty, window } = globalThis;

describe("wc/ui/redirect", () => {
	const origWindow = window.parent;

	beforeEach(() => {
		let storedUrl = "";

		window.parent = {
			// @ts-ignore
			location: {
				get href() {
					return storedUrl;
				},
				set href(url) {
					storedUrl = url;
				},
				replace: function (url) {
					// @ts-ignore
					this.href = url;
				}
			}
		};
	});

	it("does nothing with an empty url", () => {
		spyOnProperty(window.parent.location, "href", "set")
			.and.throwError("href should not be getting touched for empty url");
		redirect.register("");
	});

	it("redirects to the given non-launch url", () => {
		return testRegister("https://github.com/BorderTech/wcomponents", false);
	});

	it("does not redirect to new url given a launch url", () => {
		return testRegister("mailto:test@email.com", true);
	});

	it("recognises attachment links as launch links", () => {
		expect(redirect.isLaunchUrl("mailto:test@email.com")).toBeTrue();
	});

	it("recognises pseudo-protocol links as launch links", () => {
		expect(redirect.isLaunchUrl("https://example.com/wc_content=attach")).toBeTrue();
	});

	it("does not recognise regular navigation links as launch links", () => {
		expect(redirect.isLaunchUrl("https://example.com")).toBeFalse();
	});

	// used for async "register" tests, i.e. anything where url isn't blank
	function testRegister (inputUrl, isLaunchUrl) {
		return new Promise((resolve) => {
			spyOnProperty(window.parent.location, "href", "set").and.callFake((url) => {
				expect(url).toBe(inputUrl);
				expect(isLaunchUrl).toBeFalse();
				resolve();
			});

			spyOn(document, "getElementById").and.callFake(() => {
				expect(isLaunchUrl).toBeTrue();
				resolve();
			});
			redirect.register(inputUrl);
		});
	}

	afterAll(() => {
		window.parent = origWindow;
	});
});
