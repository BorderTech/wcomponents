import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import clearSelector from "wc/file/clearSelector.mjs";
import validate from "wc/file/validate.mjs";
import instance from "wc/ui/fileUpload.mjs";

describe("wc/ui/fileUpload.mjs", () => {
    let testHolder;
    let CONTAINER;
    let inputElementWd;
    let mockfile;
    let file1;



beforeEach (function() {
       testHolder = document.body;
	testHolder.innerHTML =
				`

				<form id="myform">
				  		<fieldset class = "wc-fileupload" id="valgroup">
				  		<div>
                        <input type="file" id="File1" name="uploadedFile" multiple accept=".pdf, .doc">
                        <button type="button" onclick="clearFileInput()">Clear File</button>
                        <button type="submit">Upload</button>

						</div>
						</fieldset>
				</form>

                <script>
                    function clearFileInput() {
                     document.getElementById('myform').reset();
                    }
                </script>
			`;


    file1 = document.getElementById("file1");
    CONTAINER = ".wc-fileupload",
    inputElementWd = `${CONTAINER} > input[type='file']`;
    

});

afterEach(function() {

		
		
});

it("element is an uploadable document ",()=>{
    

	});


it("sets file to empty value ",()=>{

	});


it("upload a file via a file input ",()=>{

	});

it("file selector on first use ",()=>{

	});



});
