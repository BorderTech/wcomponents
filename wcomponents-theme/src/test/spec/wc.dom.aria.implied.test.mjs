import impliedARIA from "wc/dom/impliedARIA.mjs";

describe("impliedARIA", function() {
	const ANY_SEL_STATE = "any";
	// language=HTML
	const html = `
		<form id="testForm" action="#" method="get">
			<div id="impliedRole">
				<input id="i0"/>
				<input id="i1" type="text" value="textbox"/>
				<input id="i2" type="password" value="textbox"/>
				<input id="i3" type="radio" value="radio"/>
				<input id="i4" type="checkbox" value="checkbox"/>
				<input id="i5" type="email" value="textbox"/>
				<input id="i6" type="tel" value="textbox"/>
				<input id="i7" type="submit" value="button"/>
				<textarea id="i8">textbox</textarea>
				<select id="i9">
					<option value="listbox" selected>1</option>
					<option>2</option>
					<option>3</option>
				</select>
				<select id="i10" multiple>
					<option value="listbox" selected>1</option>
					<option>2</option>
					<option>3</option>
				</select>
				<button id="i11" value="button">button</button>
				<a href="link">a</a>
			</div>
			<div id="no_role">
				<span id="norole0">foo</span>
				<div id="norole1">foo</div>
				<h1 id="norole2">foo</h1>
				<span id="norole3">foo</span>
				<fieldset id="norole4"><legend id="norole5">fieldset</legend></fieldset>
				<span role="textbox" id="norole6">foo</span>
				<span role="listbox" id="norole7">foo</span>
				<span role="button" id="norole8">foo</span>
				<span role="option" id="norole9">foo</span>
				<span role="radio" id="norole10">foo</span>
			</div>
			<div id="checkable">
				<input id="c0" type="radio"/>
				<input id="c1" type="checkbox"/>
			</div>
			<select id="selectable">
				<option id="so1">1</option>
				<option id="so2">2</option>
				<option id="so3">3</option>
			</select>
			<div id="not_selectable_any">
				<input id="ns0" type="text"/>
				<input id="ns1" type="hidden"/>
				<input id="ns2"/>
				<input id="ns3" type="tel"/>
				<input id="ns4" type="number"/>
				<input id="ns5" type="email"/>
				<button id="ns6">button</button>
				<textarea id="ns7"></textarea>
				<span id="ns8">foo</span>
				<span id="ns9" role="option">foo</span>
				<span id="ns10" role="radio">foo</span>
				<fieldset id="ns11"><legend id="ns121">fieldset</legend></fieldset>
			</div>
			<div id="requireable">
				<input id="r0" type="text"/>
				<input id="r1" type="radio"/>
				<input id="r2" type="checkbox"/>
				<input id="r3" type="tel"/>
				<input id="r4" type="number"/>
				<input id="r5"/>
				<select id="r6"/>
				<textarea id="r7"></textarea>
			</div>
			<div id="not_requireable">
				<input id="nr0" type="hidden"/>
				<input id="nr1" type="range"/>
				<input id="nr2" type="color"/>
				<input id="nr3" type="submit"/>
				<input id="nr4" type="image"/>
				<input id="nr5" type="reset"/>
				<input id="nr5" type="button"/>
				<span id="nr6">foo</span>
				<span id="nr7" role="textbox">foo</span>
				<span id="nr8" role="listbox">foo</span>
				<fieldset id="nr9"><legend>fieldset</legend></fieldset>
			</div>
			<div id="disableable">
				<input id="d0"/>
				<select id="d1">
					<option id="do1">1</option>
					<optgroup label="foo" id="dg1">
						<option id="do2">2</option>
						<option id="do3">3</option>
					</optgroup>
				</select>
				<textarea id="d2"></textarea>
				<input id="d3" type="hidden"/>
				<input id="d4" type="range"/>
				<input id="d5" type="color"/>
				<input id="d6" type="submit"/>
				<input id="d7" type="image"/>
				<input id="d8" type="reset"/>
				<input id="d9" type="button"/>
				<input id="d10" type="text"/>
				<input id="d11" type="radio"/>
				<input id="d12" type="checkbox"/>
				<input id="d13" type="tel"/>
				<input id="d14" type="number"/>
				<button id="d15">button</button>
			</div>
			<div id="not_disableable">
				<span id="nd0">foo</span>
				<span id="nd1" role="textbox">textbox</span>
				<span id="nd2" role="listbox">listbox</span>
				<span id="nd3" role="group">group</span>
				<span id="nd4" role="button">button</span>
				<span id="nd5" role="option">foo</span>
				<span id="nd6" role="radio">foo</span>
				<span id="nd7" role="checkbox">foo</span>
			</div>
		</form>`;

	function doNativeStateTest(elements, state, notThisState) {
		for (let i = 0; i < elements.length; ++i) {
			let next = elements[i];

			if (notThisState) {
				expect(impliedARIA.supportsNativeState(next, state)).withContext(`native state (${state}) for ${next.id}`).toBeFalse();
			} else {
				expect(impliedARIA.supportsNativeState(next, state)).withContext(`native state (${state}) for ${next.id}`).toBeTrue();
			}
		}
	}

	beforeAll(() => {
		document.body.innerHTML = html;
	});

	afterAll(() => {
		document.body.innerHTML = "";
	});

	it("testGetImpliedRole", function() {
		const container = document.getElementById("impliedRole");
		const children = Array.from(container.children);
		children.forEach(child => {
			/** @type {string} */
			let expected = child["value"];
			if (expected) {
				expect(impliedARIA.getImpliedRole(child)).withContext(`Implied role on ${child.id}`).toBe(expected);
			}

			expected = child["selectedIndex"] ? child["options"][child["selectedIndex"]].value : "";
			if (expected) {
				expect(impliedARIA.getImpliedRole(child)).withContext(`Implied role on ${child.id}`).toBe(expected);
			}

			expected = child["href"] ? child.getAttribute("href") : "";
			if (expected) {
				expect(impliedARIA.getImpliedRole(child)).withContext(`Implied role on ${child.id}`).toBe(expected);
			}
		});
	});

	it("testGetImpliedRoleNoRole", function() {
		const container = document.getElementById("no_role"),
			elements = container.getElementsByTagName("*");
		for (let i = 0; i < elements.length; ++i) {
			let next = elements[i];
			let result = impliedARIA.getImpliedRole(next);
			expect(result).withContext(`element ${next.id} should not have an implied role, got ${result}`).toBeFalsy();
		}
	});

	it("testNativeSelect", function() {
		doNativeStateTest(document.getElementById("selectable").getElementsByTagName("*"), "selected");
	});

	it("testNativeCheck", function() {
		doNativeStateTest(document.getElementById("checkable").getElementsByTagName("*"), "checked");
	});

	it("testNativeSelectCheckAny", function() {
		doNativeStateTest(document.getElementById("checkable").getElementsByTagName("*"), ANY_SEL_STATE);
		doNativeStateTest(document.getElementById("selectable").getElementsByTagName("*"), ANY_SEL_STATE);
	});

	it("testNativeSelectCheckAnyFalse", function() {
		doNativeStateTest(document.getElementById("not_selectable_any").getElementsByTagName("*"), ANY_SEL_STATE, true);
		doNativeStateTest(document.getElementById("not_selectable_any").getElementsByTagName("*"), "selected", true);
		doNativeStateTest(document.getElementById("not_selectable_any").getElementsByTagName("*"), "checked", true);
	});

	it("testNativeRequired", function() {
		doNativeStateTest(document.getElementById("requireable").getElementsByTagName("*"), "required");
	});

	it("testNativeRequiredFalse", function() {
		doNativeStateTest(document.getElementById("not_requireable").getElementsByTagName("*"), "required", true);
	});

	it("testNativeDisabled", function() {
		doNativeStateTest(document.getElementById("disableable").getElementsByTagName("*"), "disabled");
	});

	it("testNativeDisabledFalse", function() {
		doNativeStateTest(document.getElementById("not_disableable").getElementsByTagName("*"), "disabled", true);
	});
});
