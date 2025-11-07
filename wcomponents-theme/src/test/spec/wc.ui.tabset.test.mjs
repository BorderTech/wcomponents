import tabset from 'wc/ui/tabset.mjs';
import formUpdateManager from "wc/dom/formUpdateManager.mjs";
import containerload from "wc/ui/containerload.mjs";
import expand from 'wc/date/expandYear.mjs';
import shed from "wc/dom/shed.mjs";

describe("wc/ui/tabset.mjs", () => {
	let testHolder;
	let tab1;
	let tab2;
	let tab3;
	let tablist;
	let tabs;
	let container;
	let currenttab;





	beforeAll (function() {
		testHolder = document.body;
		testHolder.innerHTML =
		`

				<form id="tablist-form">
				  		<fieldset class="wc-tabset" id="valgroup">
				  		<div>
						<button role="tab" class="accordion" aria-selected="false" aria-controls="tabpanel-1" id="tab1">tab1</button>
						<button role="tab"  aria-selected="false" aria-controls="tabpanel-2" id="tab2">tab1</button>
						<button role="tab" class="accordion" aria-selected="false" aria-controls="tabpanel-3" id="tab3">tab1</button>
						</div>
						</fieldset>
				</form>`;


		tab1 = document.getElementById('tab1');
		tab2 = document.getElementById('tab2');
		tab3 = document.getElementById('tab3');
		tablist = document.getElementsByName('tabpanel-id');
		

		tabs = [testHolder.querySelector('[role="tab"]')];
		currenttab = 0;
		tabs[currenttab].focus();
		tabs[currenttab].setAttribute("aria-selected","true");

		testHolder.addEventListener("keydown", (event) => {
		if(!event.ctrlKey)return;
		const direction = event.key ==="PgUp"? -1:event.key === "PgDn" ? 1: 0;
		if(!direction) return;
		});


	
	







	});


	afterAll(function() {

		testHolder.innerHTML = "";
	});


	it("selectOnNavigate will return attributres ",() => {
		tabset.selectOnNavigate(tab1);


	});



	it(" when an acordian is expanded   ",() => {
		tabset.areAllInExpandedState(tab1,true);



	});



	it(" shedObserver  ",() => {

		tabset.shedObserver(tab1, "SELECT");
		tabset.shedObserver(tab1, "collapse");
		tabset.shedObserver(tab1, "Deselect");
		tabset.shedObserver(tab1, "EXPAND");





	});

	it(" activate - shows tabs content   ",() => {
		tabset.activate(tab1);



	});


	it(" control + page up/down    ",() => {

	


		const PressKey = (key) =>{
			const keyevent =  new KeyboardEvent("keydown", {
				
				key: "PageUp",
				bubbles: true,
				ctrlKey: true,

			});
			tab1.dispatchEvent(keyevent);
		};

		
	});


	it(" onItemSelection ",() => {
		tabset.onItemSelection("SELECT", tab1);

	});


});