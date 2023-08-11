/* eslint-env node, es6  */
define(["intern!object", "intern/chai!assert", "intern/resources/test.utils!", "wc/ui/timeoutWarn", "/node_modules/@testing-library/dom/dist/@testing-library/dom.umd.js"],
	function (registerSuite, assert, testutils, timeoutWarn, domTesting) {
		let container, uid = 0;

		registerSuite({
			name: "wc/ui/timeoutWarn",
			setup: function () {
				container = testutils.getTestHolder();
			},
			afterEach: function () {
				container.innerHTML = "";
			},
			testBasicRender: function () {
				const testId = `uid-${Date.now()}-${uid++}`;
				container.innerHTML = `<${timeoutWarn.tagName} data-testid='${testId}'></${timeoutWarn.tagName}>`;
				const element = domTesting.within(container).getByTestId(testId);
				assert.isOk(element);
				assert.isTrue(element.hasAttribute("hidden"), "The element should be hidden at first");
			},
			testExpiresAt: function () {
				const timeout = 600;  // seconds
				const expected = new Date();
				expected.setTime(expected.getTime() + (timeout * 1000));  // expect at least this far in the future

				const testId = `uid-${Date.now()}-${uid++}`;
				container.innerHTML = `<${timeoutWarn.tagName} timeout="${timeout}" data-testid='${testId}'></${timeoutWarn.tagName}>`;

				return domTesting.waitFor(() => {
					const element = domTesting.within(container).getByTestId(testId);
					const expiresAt = element.getAttribute("data-expires");
					const actual = new Date(expiresAt);
					assert.isAtLeast(actual.getMinutes(), expected.getMinutes(), `Session should expire in about ${timeout / 60} minutes`);
					// The minute might have ticked over between creating the compare date and the expiry date, even the hour might have!
					assert.isAtMost(actual.getMinutes(), expected.getMinutes() + 1, `Session should expire in at most ${(timeout / 60) + 1} minutes`);
				});
			},
			testErrorDialogIsShown: function () {
				const timeout = 45;  // seconds
				const expected = new Date();
				expected.setTime(expected.getTime() + (timeout * 1000));  // expect at least this far in the future

				const testId = `uid-${Date.now()}-${uid++}`;
				container.innerHTML = `<${timeoutWarn.tagName} timeout="${timeout}" data-testid='${testId}'></${timeoutWarn.tagName}>`;
				let warningSeen = false, expiredSeen = false;
				return domTesting.waitFor(() => {
					const element = domTesting.within(container).getByTestId(testId);
					assert.isFalse(element.hasAttribute("hidden"), "The dialog should be shown");
					if (!warningSeen) {
						if (element.querySelector('.wc-messagebox-type-error')) {
							warningSeen = true;
							console.log("Warning dialog seen at", new Date().getTime());
						}
					}
					if (element.querySelector('.wc-messagebox-type-error')) {
						expiredSeen = true;
						console.log("Expired dialog seen at", new Date().getTime());
					}
					assert.isOk(warningSeen && expiredSeen, "The dialogs should be shown");
				}, { timeout: timeout * 1000 });
			}
		});

	});
