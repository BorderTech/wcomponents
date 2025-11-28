import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import debounce from "wc/debounce.mjs";

describe("wc/ui/clipboard.mjs", () => {
    let testHolder;
	let Button1;
	let value1;
	
	

beforeEach (function() {

    testHolder = document.body;
	testHolder.innerHTML =
			`

				<form id="Clipboard " class = "wc-clipboard">
				  		<fieldset id="valgroup">
						<input type = "text" value = "coppy this text" id = "Ctext1" >
                        <Button> onclick = "CopyFunction()" id ="b1" > Copy Text </Button> 

						function CopyFunction() {
							var TextCopy = document.getElementById("Ctext1");

							TextCopy.select();
							navigator.clipboard.writeText(copyText.value);


                            }

						
				  		
						</fieldset>
				</form>
			`;

	Button1 = document.getElementById("b1");
	value1 = document.getElementById("ctext1");

	
	
            

        

});

afterEach(function() {

		testHolder.innerHTML = "";
		
	});


	it("coppies text shown and displays Copied to clipboard  ",()=>{
		
		const Clickevent =  new MouseEvent("click", {

			view: window,
			bubbles: true,
			cancelable: true,
		
		});

		const result = "Copied to clipboard";
		

		

	});


	it("when cant be coppied and displays error message  ",()=>{
		

		

	});



});