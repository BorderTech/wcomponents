import checkBoxSelect from "wc/ui/checkBoxSelect.mjs";
import domTesting from "@testing-library/dom";
import getFilteredGroup from "wc/dom/getFilteredGroup.mjs";
import shed from "wc/dom/shed.mjs";
import fieldset from "wc/ui/fieldset.mjs";


describe("wc/ui/checkBoxSelect", () => {
	let testHolder;
	let checkbox1;
	let checkbox2;
	let checkbox3;
	let CONTAINER;
	let container;
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
						<input type="checkbox"name="options" aria-disabled="false" id = "cb1" value = "option1"/>
						<input type="checkbox"name="options" aria-disabled="false" id = "cb2" value ="option2"/>
						<input type="checkbox"name="options" aria-disabled="false" id = "cb3" value = "option3"/>
						</div>
						</fieldset>
				</form>
			`;


		
		CONTAINER = "${fieldset.getWidget().toString()}.wc-checkboxselect";
		container = document.querySelector(".wc-checkboxselect");
		checkboxes = document.querySelectorAll("checkbox");
		checkbox1 = document.getElementById("cb1") ;
		checkbox2 = document.getElementById("cb2") ;
		checkbox3 = document.getElementById("cb3") ;
		fieldset = document.getElementById("fieldset");
		lastActivatedCheckbox = null;

		spyOn(shed,'isDisabled').and.returnValue(false);
		spyOn(shed,'isHidden').and.returnValue(false);






	});

	afterEach(function() {

		testHolder.innerHTML = "";
		;
	});



	it("click + shift will go to the last selected ",()=>{

		checkbox1.checked = true;
		checkbox2.checked = true;
		checkbox3.checked = false;

		const event =  new MouseEvent("click", {

			view: window,
			bubbles: true,
			cancelable: true,
			shiftKey: true,

		});
		checkbox1.dispatchEvent(event);
		lastActivatedCheckbox = checkbox1;


	
	});

	it("the last activated item will be the last checkbox checked ",()=>{
		const element = checkbox1; 
        const lastActivated = checkbox2;

		checkBoxSelect.doGroupSelect(checkbox1, lastActivatedCheckbox, container);

		checkbox1.checked = true;
		checkbox2.checked = true;
		checkbox3.checked = false;

		const event =  new MouseEvent("click", {

			view: window,
			bubbles: true,
			cancelable: true,
			shiftKey: true,

		});
		checkbox1.dispatchEvent(event);
		lastActivatedCheckbox = checkbox1;
	});

});

