import initialise from "wc/dom/initialise.mjs";
import processResponse from "wc/ui/ajax/processResponse.mjs";
import "wc/ui/cancelUpdate.mjs";
import cancel from "wc/ui/cancelButton.mjs";

describe("wc/ui/cancelButton.mjs", () => {
    let testHolder;




beforeEach (function() {

    testHolder = document.body;
	testHolder.innerHTML =
			`

				<form id="cancelButton">
				  		<fieldset class = "wc_unsaved" id="valgroup">
				  		<div>
						<label for="name">Enter name: </label>
                        <input> type = "text" id = "name" name = "name"><br><br>
                        <button> type = "submit" value = "submit" name = "submit"</button>
                        <button> type = "cancel" value = "cancel" onclick = "history.go(-1)"</button>

						
						</div>
						</fieldset>
				</form>
			`;

	
});

afterEach(function() {

		testHolder.innerHTML = "";
		
	});


	it("cancle button can be pressed ",()=>{

	});
});
