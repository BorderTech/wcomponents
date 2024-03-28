import redirect from "wc/ui/redirect.mjs";

describe("wc/ui/redirect", () => {
	// window = Object.create(Window);

	const delay = 50;  // milliseconds to wait for events and stuff to be actioned
	const origWindow = window.parent;
	const replaceSpy = jasmine.createSpy();

	beforeEach(() => {
		window.parent = {
			location : {
				storedUrl : "",
				set href(url) {
					this.storedUrl = url;
				},
				replace : function (url) {
					this.href = url;
				}
			}
		};
	});

	it("does nothing with an empty url", () => {
		spyOnProperty(window.parent.location, "href", "set")
			.and.throwError("href should not be getting touched for empty url");
		spyOn(redirect, "register").and.callThrough();
		redirect.register("");
		expect(redirect.register).toHaveBeenCalledTimes(1);
	});

	it("redirects to the given non-launch url", () => {
		spyOn(redirect, "register").and.callThrough();
		return testRegister("https://github.com/BorderTech/wcomponents", false);
	});

	it("does not redirect to new url given a launch url", () => {
		spyOn(redirect, "register").and.callThrough();
		return testRegister("mailto:test@email.com", true);
	});

	it("recognises attachment links as launch links", () => {
		spyOn(redirect, "isLaunchUrl").and.callThrough();
		expect(redirect.isLaunchUrl("mailto:test@email.com")).toBeTrue();
		expect(redirect.isLaunchUrl).toHaveBeenCalledTimes(1);
	});

	it("recognises pseudoprotocol links as launch links", () => {
		spyOn(redirect, "isLaunchUrl").and.callThrough();
		expect(redirect.isLaunchUrl("https://example.com/wc_content=attach")).toBeTrue();
		expect(redirect.isLaunchUrl).toHaveBeenCalledTimes(1);
	});

	it("does not recognise regular navigation links as launch links", () => {
		spyOn(redirect, "isLaunchUrl").and.callThrough();
		expect(redirect.isLaunchUrl("https://example.com")).toBeFalse();
		expect(redirect.isLaunchUrl).toHaveBeenCalledTimes(1);
	});

	// used for async "register" tests, i.e. anything where url isn't blank
	function testRegister (inputUrl, isLaunchUrl) {
		return new Promise((resolve) => {
			spyOnProperty(window.parent.location, "href", "set").and.callFake((url) => {
				expect(url).toBe(inputUrl);
				expect(isLaunchUrl).toBeFalse();
				expect(redirect.register).toHaveBeenCalledTimes(1);
				resolve();
			});
			spyOn(document, "getElementById").and.callFake(() => {
				expect(isLaunchUrl).toBeTrue();
				expect(redirect.register).toHaveBeenCalledTimes(1);
				resolve();
			});
			redirect.register(inputUrl);
		});
	}

	afterAll(() => {
		window.parent = origWindow;
	});
});
