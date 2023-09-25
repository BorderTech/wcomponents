import triggerManager from "wc/ajax/triggerManager.mjs";
import Trigger from "wc/ajax/Trigger.mjs";

describe("wc/ajax/triggerManager", () => {
	const testContent = `
		<form id = "fred">
			<button id="swan" name="gamma">Click</button>
			<fieldset id="adam">
				<button id="foo">Click</button>
				<button id="bar">Click</button>
				<button id="jim">Click</button>
			</fieldset>
		</form>`;

	let testHolder, trig, trig2, trig3, trig4;

	beforeAll(function() {
		testHolder = document.body;
		testHolder.innerHTML = testContent;
		trig = new Trigger({ id: "foo", loads: ["fred"] });
		trig2 = new Trigger({ id: "bar", loads: ["fred"] });
		trig3 = new Trigger({ id: "adam", loads: ["fred"] });
		trig4 = new Trigger({ id: "gamma", loads: ["fred"] });
	});
	
	afterAll(function() {
		testHolder.innerHTML = "";
	});
			
	it("testTriggerManagerAddTrigger", function() {
		triggerManager.addTrigger(trig);
		const actual = triggerManager.getTrigger("foo");
		expect(actual).toBe(trig);
	});

	// This test fails. Returns "undefined" instead.
	it("testTriggerManaagerRemoveTrigger", function() {
		triggerManager.addTrigger(trig);
		triggerManager.removeTrigger(trig.id);
		const actual = !!(triggerManager.getTrigger(trig.id));
		expect(actual).toBeFalse();
	});
	
	// This test fails. Returns "undefined" instead.
	it("testTriggerManagerAddTwoSameTriggers", function() {
		triggerManager.addTrigger(trig);
		triggerManager.addTrigger(trig);
		triggerManager.removeTrigger(trig.id);
		const actual = !!(triggerManager.getTrigger(trig.id));
		expect(actual).toBeFalse();
	});
	
	it("testTriggerManagerAddManyGetOne", function() {
		triggerManager.addTrigger(trig);
		triggerManager.addTrigger(trig2);
		triggerManager.addTrigger(trig3);
		const actual = triggerManager.getTrigger(trig2.id);
		expect(actual).toBe(trig2);
	});

	it("testTriggerManagerElementIDTrigger", function() {
		triggerManager.addTrigger(trig);
		const element = document.getElementById("foo");
		const actual = triggerManager.getTrigger(element);
		expect(actual).toBe(trig);
	});
	
	it("testTriggerManagerElementNameTrigger", function() {
		triggerManager.addTrigger(trig4);
		const element = document.getElementById("swan");
		const actual = triggerManager.getTrigger(element);
		expect(actual).toBe(trig4);
	});
	
	it("testTriggerManagerNestedTrigger", function() {
		triggerManager.addTrigger(trig3);
		const element = document.getElementById("jim");
		const actual = triggerManager.getTrigger(element);
		expect(actual).toBe(trig3);
	});
});
