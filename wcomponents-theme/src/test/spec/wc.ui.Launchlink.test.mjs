import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import redirect from "wc/ui/redirect.mjs";
import instance from "wc/ui/launchLink.mjs";



describe("wc/ui/Launchlink.mjs", () => {
    let testHolder;
	let selectors;
	let link1;
	let mockgetHref
	





beforeEach (function() {

    testHolder = document.body;
	testHolder.innerHTML =
			`

				<form id="LaunchLinks">
				  		<fieldset id="valgroup">
				  		<div>
						<a href="https://e4-intranet.bcz.gov.au/" id = "L1" >sample link </a>
						<element onbeforeunload="are you sure ...." id = "BU1">

						
						</div>
						</fieldset>
				</form>
			`;
	
	link1 = document.getElementById("L1") ;
	selectors = ["a", "[data-wc-url]"].join();
	mockgetHref = jasmine.createSpy("getHref");
	





	
});

afterEach(function() {

		testHolder.innerHTML = "";
		
	});


	it("page will not load beforeunloadevent when linked is clicked   ",()=>{
		link1.click()

		

	});


	it("page will not load beforeunloadevent when linked is clicked   ",()=>{
		

	});
});
