import "wc/ui/radioButtonSelect.mjs";
import instance from "wc/ui/radioButtonSelect.mjs";
import shed from "wc/dom/shed.mjs";
import cbrShedPublisher from "wc/dom/cbrShedPublisher.mjs";
import radioButtonSelect from "wc/ui/radioButtonSelect.mjs";


describe("wc/ui/radiobuttonselect", () => {

	let testHolder;
	let Button1;
	let Button2;
	let Button3;
	let fieldset;
	let radioButtonSelectSelector;






	beforeAll (function() {
		testHolder = document.body;
		testHolder.innerHTML =
		`

				<form id="radio-button-form">
				  		<fieldset class="wc-radiobuttonselect" id="valgroup">
				  		<div>
						<input type="radio"name="options" id="op1" value="option1"/>
						<label for="op1">option1</label>
						<input type="radio"name="options" id="op2" value="option2"/>
						<label for="op1">option2</label>
						<input type="radio"name="options" id="op3" value="option3"/>
						<label for="op3">option3</label>
						</div>
						</fieldset>
				</form>`;




		radioButtonSelectSelector = "fieldset.wc-radiobuttonselect";
		fieldset = document.getElementById("valgroup");

		Button1 = document.getElementById('op1') ;
		Button2 = document.getElementById('op2') ;
		Button3 = document.getElementById('op3') ;









	});

	afterAll(function() {

		testHolder.innerHTML = "";
	});





	it("selcet option 1",() => {

		Button1 = document.getElementById('op1') ;
		Button1.Checked = true;
		expect(Button1.Checked).toBeTrue();


	});



	it("will de-select radio next to it one new radio button is selected",() => {
		expect(fieldset).not.toBeNull();
		expect(fieldset.matches(radioButtonSelectSelector)).toBeTrue();


		instance.setSelectionByValue(fieldset,"option1");

		Button1.Checked = true;
		Button2.Checked = false;
		Button3.Checked = false;

		expect(Button1.checked).toBeTrue;
		expect(Button2.checked).toBeFalse;
		expect(Button3.checked).toBeTrue;


		Button1.Checked = false;
		Button2.Checked = true;
		Button3.Checked = false;

		expect(Button1.checked).toBeFalse;
		expect(Button2.checked).toBeTrue;
		expect(Button3.checked).toBeFalse;





	});


	it("there will be no default set vaulue for the radio selector",() => {


		instance.setSelectionByValue(Button1,"null");

		Button1.Checked = false;
		Button2.Checked = false;
		Button3.Checked = false;







	});





});
