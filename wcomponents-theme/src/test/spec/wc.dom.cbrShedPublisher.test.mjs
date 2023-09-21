import cbrShedPublisher from "wc/dom/cbrShedPublisher.mjs";
import shed from "wc/dom/shed.mjs";
import event from "wc/dom/event.mjs";
import domTesting from "@testing-library/dom";


const testContent = `<div>
	<label id="l1" for="r1" data-testid="l1"><input type="radio" id="r1" name="rg1" data-testid="r1"/> one</label>
	<label id="l2" for="r2" data-testid="l2"><input type="radio" id="r2" name="rg1" data-testid="r2" checked/> two</label>
	<label id="l3" for="cb1" data-testid="l3"><input type="checkbox" id="cb1" name="cbg1" data-testid="cb1"/> cb one</label>
	<label id="l4" for="cb2" data-testid="l4"><input type="checkbox" id="cb2" name="cbg2" checked data-testid="cb2"/> cb two</label>
	</div>`;

describe("wc/dom/cbrShedPublisher", () => {

	let testHolder;

	function eventIt(type, id, otherId) {
		return new Promise((win) => {
			shed.subscribe("select", subscriber);
			shed.subscribe("deselect", subscriber);

			let element = domTesting.getByTestId(testHolder, id);
			expect(element).withContext("no element to focus").toBeTruthy();

			element = domTesting.getByTestId(testHolder, (otherId || id));
			expect(element).withContext("no element to click").toBeTruthy();
			event.fire(element, type);
			setTimeout(() => win(false), 50);

			function subscriber() {
				win(true);
			}
		});
	}

	beforeEach(function() {
		testHolder = document.body.appendChild(document.createElement("div"));
		testHolder.innerHTML = testContent;
	});

	afterEach(function() {
		document.body.removeChild(testHolder);
	});


	it("click checkbox to select publishes", function() {
		return eventIt("click", "cb1").then(done => {
			expect(done).toBeTrue();
		});
	});

	it("click checkbox to deselect publishes", function() {
		return eventIt("click", "cb2").then(done => {
			expect(done).toBeTrue();
		});
	});

	it("click checked radio does not publish", function() {
		return eventIt("click", "r2").then(done => {
			expect(done).toBeFalse();
		});
	});

	it("click unchecked radio publishes", function() {
		return eventIt("click", "r1").then(done => {
			expect(done).toBeTrue();
		});
	});

	it("click label publishes", function() {
		return eventIt("click", "r1", "l3").then(done => {
			expect(done).toBeTrue();
		});
	});

	it("click label of unchecked radio publishes", function() {
		return eventIt("click", "r1", "l1").then(done => {
			expect(done).toBeTrue();
		});
	});

	it("click label of checked radio does not publish", function() {
		return eventIt("click", "r2", "l2").then(done => {
			expect(done).toBeFalse();
		});
	});

	it("click disabled checkbox does not publish", function() {
		const id = "cb1";
		const element = /** @type {HTMLInputElement} */(domTesting.getByTestId(testHolder, id));
		element.disabled = true;
		return eventIt("click", id).then(done => {
			expect(done).toBeFalse();
		}).then(() => {
			element.disabled = false;
			return eventIt("click", id).then(done => {
				expect(done).toBeTrue();
			});
		});
	});

	it("change event on checkbox publishes", function() {
		return eventIt("change", "cb1").then(done => {
			expect(done).toBeTrue();
		});
	});

	it("change event on unchecked radio publishes", function() {
		return eventIt("change", "r1").then(done => {
			expect(done).toBeTrue();
		});
	});

	it("change event on checked radio publishes", function() {
		return eventIt("change", "r2").then(done => {
			expect(done).toBeTrue();
		});
	});

	it("get radio widget", function() {
		const w = cbrShedPublisher.getWidget("r"),
			element = domTesting.getByTestId(testHolder, "r1");
		expect(w).toBeTruthy();
		expect(element.matches(w)).toBeTrue();
	});

	it("get checkbox widget", function() {
		const w = cbrShedPublisher.getWidget("cb"),
			element = domTesting.getByTestId(testHolder, "cb1");
		expect(w).toBeTruthy();
		expect(element.matches(w)).toBeTrue();
	});

	it("get widgets", function() {
		const w = cbrShedPublisher.getWidget();
		let element = domTesting.getByTestId(testHolder, "cb1");
		expect(Array.isArray(w)).toBeTrue();
		expect(element.matches(w.join())).withContext("Expected a check box to be a match for the Widget array").toBeTrue();
		element = domTesting.getByTestId(testHolder, "r1");
		expect(element.matches(w.join())).withContext("Expected a radio button to be a match for the Widget array").toBeTrue();
	});
});
