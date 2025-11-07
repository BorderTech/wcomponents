import checkBoxSelect from "wc/ui/checkBoxSelect.mjs";
import domTesting from "@testing-library/dom";
import getFilteredGroup from "wc/dom/getFilteredGroup.mjs";
import shed from "wc/dom/shed.mjs";


describe("wc/ui/checkBoxSelect", () => {
	let testHolder;
	let checkbox1;
	let checkbox2;
	let checkbox3;
	let CONTAINER ;
	let fieldset;
	let lastActivatedCheckbox;
	let checkboxes;




	beforeEach (function() {
		testHolder = document.body;
		testHolder.innerHTML =
			`

				<form id="checkbox form">
				  		<fieldset class="wc-checkboxselect" id="valgroup">
				  		<div>
						<input type="checkbox"name="options" id = "op1" value = "option1"/>
						<input type="checkbox"name="options" id = "op2" value ="option2"/>
						<input type="checkbox"name="options" id = "op3" value = "option3"/>
						</div>
						</fieldset>
				</form>
			`;


		CONTAINER = document.querySelector(".wc-checkboxselect");
		fieldset = document.getElementById("valgroup");
		checkboxes = document.querySelectorAll("checkbox");
		checkbox1 = document.getElementById('op1') ;
		checkbox2 = document.getElementById('op2') ;
		checkbox3 = document.getElementById('op3') ;
		lastActivatedCheckbox = null;





	});

	afterEach(function() {

		testHolder.innerHTML = "";
	});



	it("click + shift will go to the last selected ",()=>{



		checkbox1.checked = false;
		checkbox2.checked = true;
		checkbox3.checked = false;

		const event =  new MouseEvent("click", {

			view: window,
			bubbles: true,
			cancelable: true,
			shiftKey: true,

		});

		checkbox2.dispatchEvent(event);

		lastActivatedCheckbox = checkbox1;
	});




	it("the last activated item will be the last checkbox checked ",()=>{
		const checkArray = Array.from(checkboxes);
		lastActivatedCheckbox = checkbox1;
		checkBoxSelect.doGroupSelect(checkbox1,lastActivatedCheckbox,CONTAINER);
		checkbox1;
		lastActivatedCheckbox;
		CONTAINER;
		shed.isDisabled(checkbox1);
		shed.isDisabled(fieldset);
		shed.isHidden(checkboxes);









		checkbox1.checked = true;
		checkbox2.checked = false;
		checkbox3.checked = false;

















	});







});
