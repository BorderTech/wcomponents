import formUpdateManager from "wc/dom/formUpdateManager.mjs";

describe("wc/dom/formUpdateManager", function() {
	/**
	 * There is a requirement to ensure that the enctype of the form is set correctly if there is a file selector
	 * in the form. We don't care how it happens or where the code is located, all we care about is that when we
	 * call update on wc/dom/formUpdateManager the enctype is looked after by someone, somewhere.
	 *
	 * Not that we care but the code used to be wc/dom/checkEnctype and is now in wc/dom/formUpdateManager.
	 */
	
	const html = `
		<form name="checkEnctype1">
			<label id="maleLabel" for="male">Male</label>
			<input type="radio" name="sex" id="male" />
			<br />
			<label id="femaleLabel">Female
				<input type="radio" name="sex" id="female" />
			</label>
			<br />
			<label id="yesLabel" for="yes">Yes</label>
			<input type="radio" name="sex" id="yes" />
		</form>
		<form name="checkEnctype2">
			<label id="maleLabelX" for="maleX">Male</label>
			<input type="radio" name="sex" id="maleX" />
			<br />
			<label id="femaleLabelX">Female
				<input type="radio" name="sex" id="femaleX" />
			</label>
			<br />
			<label id="yesLabelX" for="yesX">Yes</label>
			<input type="radio" name="sex" id="yesX" />
			<input type="file"/>
		</form>`;
	
	const multipartEncType = "multipart/form-data",
		testHolder = document.body;

	beforeEach(function() {
		testHolder.innerHTML = html;
	});

	afterEach(function() {
		testHolder.innerHTML = "";
	});

	it("testCheckEnctypeNoRewrite", function() {
		const form = document.forms["checkEnctype1"];
		let enctype = form.enctype;
		expect(enctype).not.toBe(multipartEncType);
		formUpdateManager.update(form);
		enctype = form.enctype;
		expect(enctype).not.toBe(multipartEncType);
	});

	it("testCheckEnctypeYesRewrite", function() {
		const form = document.forms["checkEnctype2"];
		let enctype = form.enctype;
		expect(enctype).not.toBe(multipartEncType);
		formUpdateManager.update(form);
		enctype = form.enctype;
		expect(enctype).toBe(multipartEncType);
		// assert.strictEqual(multipartEncType, enctype);
		// This was originally strictEqual. Check with Rick
	});
});
