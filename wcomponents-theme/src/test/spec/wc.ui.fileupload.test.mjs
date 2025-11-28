import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import clearInput from "wc/file/clearSelector.mjs";
import clearSelector from "wc/file/clearSelector.mjs";
import validate from "wc/file/validate.mjs";
import instance from "wc/ui/fileUpload.mjs";

describe("wc/ui/fileUpload.mjs", () => {
    let testHolder;
    let CONTAINER;
    let inputElementWd;
    let mockfile;
    let file1;
    let Selector;
    let file2;
    



beforeEach (function() {
       testHolder = document.body;
	testHolder.innerHTML =
			`

				<form id="Fileupload" action="/upload-endpoint" method="post" enctype="multipart/form-data">
				  		<fieldset class = "wc-fileupload" id="valgroup">
				  		<div>
                        <label> for "file-uploader" > File Selector </label>
                        <input type="file" id="File1" name="uploadedFile()" multiple accept=".pdf, .doc">
                        <button type="button" onclick="clearFileInput()">Clear File</button>
                         <button type="submit">Upload</button>

                      

						</div>
						</fieldset>
				</form>
			`;


    file1 = document.getElementById("File1");
    CONTAINER = ".wc-fileupload",
    inputElementWd = `${CONTAINER} > input[type='file']`;
    Selector = document.getElementById("file-uploader");

    
    

});

afterEach(function() {
testHolder.innerHTML = "";
		
		
});

it("element is an uploadable document ",()=>{
    instance.clearInput(file1);
    file1.cloneNode(true);

    

    function cloner(){
        const node = document.getElementById("file1");
        const clone = node.cloneNode(true);
        document.body.appendChild(clone);
    }

    


    


    

   
    
   


	});


it("sets file to empty value ",()=>{
    
    

	});


it("upload a file via a file input ",()=>{

	});

it("file selector on first use ",()=>{

	});



});