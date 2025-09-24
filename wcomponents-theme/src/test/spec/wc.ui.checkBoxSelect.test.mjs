import checkBoxSelect from "wc/ui/checkBoxSelect.mjs";
import domTesting from "@testing-library/dom";
import getFilteredGroup from "wc/dom/shed.mjs";


describe("wc/ui/radiobuttonselect", () => {
	let testHolder;
	let checkbox1;
	let checkbox2;
	let checkbox3;



	beforeEach (function() {
		testHolder = document.body;
		testHolder.innerHTML =
			`

				<form id="checkbox form">
					<span class="wc-input-wrapper" id="dd1" style="width:10em">
						<select id="dd1_input" data-testid="dd1" name="dd1" required style="width:5em">
				  		<fieldset>
				  		<div>
						<input type="checkbox"name="options" id = "op1" value = "option1"/>
						<input type="checkbox"name="options" id = "op2" value ="option2"/>
						<input type="checkbox"name="options" id = "op3" value = "option3"/>
						</div>
						</fieldset>
					</span>
				</form>
			`;






	});

	afterEach(function() {

		testHolder.innerHTML = "";
	});

	it("user can select checkbox ",()=>{
		checkbox1 = document.getElementById('op1') ;
		checkbox2 = document.getElementById('op2') ;
		checkbox3 = document.getElementById('op3') ;

		expect(checkbox1).not.toBeNull();
		checkbox1.checked = true;
		checkbox2.checked = false;
		checkbox3.checked = true;

		expect(checkbox1.checked).toBeTrue();
		expect(checkbox2.checked).toBeFalse();
		expect(checkbox3.checked).toBeTrue();



	});

	it("user can un-select checkbox ",()=>{
		checkbox1 = document.getElementById('op1') ;
		checkbox2 = document.getElementById('op2') ;
		checkbox3 = document.getElementById('op3') ;


		checkbox1.checked = true;
		checkbox2.checked = false;
		checkbox3.checked = false;

		expect(checkbox1.checked).toBeTrue();
		expect(checkbox2.checked).toBeFalse();
		expect(checkbox3.checked).toBeFalse();

		checkbox1.checked = false;
		checkbox2.checked = false;
		checkbox3.checked = false;

		expect(checkbox1.checked).toBeFalse();
		expect(checkbox2.checked).toBeFalse();
		expect(checkbox3.checked).toBeFalse();









	});

	it("click + shift will go to the last selected ",()=>{
		checkbox1 = document.getElementById('op1') ;
		checkbox2 = document.getElementById('op2') ;
		checkbox3 = document.getElementById('op3') ;

		checkbox1.checked = false;
		checkbox2.checked = true;
		checkbox3.checked = false;

		const ClickShiftEvent = onclick;








	});


	it("will check the checkbox when the space bar is entered", ()=>{
		checkbox1 = document.getElementById('op1') ;
		checkbox2 = document.getElementById('op2') ;
		checkbox3 = document.getElementById('op3') ;

		checkbox1.checked = false;
		checkbox2.checked = false;
		checkbox3.checked = false;

		const BackSpaceEvent = new KeyboardEvent("keydown", {
			bubbles: true,
			cancelable: true,
			code: "Backspace",
			view: window
		});

		checkbox1.dispatchEvent(BackSpaceEvent);
		expect(checkbox1.checked).toBeTruthy;








	});

	it("the last activated item will be the last checkbox checked ",()=>{



	});







});
