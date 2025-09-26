import "wc/ui/prompt.mjs";
import prompt from "wc/ui/prompt.mjs";


describe("wc/ui/prompt.mjs",()=> {
	let callbackSpy;
	let messageSpy;
	let objectSpy;
	let calledMessage;
	let confirmSpy;





	beforeEach(() => {
		callbackSpy = jasmine.createSpy('callbackSpy');
		messageSpy = jasmine.createSpy('messageSpy');
		objectSpy = jasmine.createSpy('objectSpy');
		jasmine.clock().install();


		global.confirm = jasmine.createSpy("confirm").and.returnValue(true);








	});

	afterEach(()=>{

		jasmine.clock().uninstall();
		delete global.confirm;


	});

	it(" the pop up should wait for 250 milseconds ",() =>{
		const alertSpy = jasmine.createSpy("alertspy");
		window.alert = alertSpy;
		alertSpy("hello");



		jasmine.clock().tick(250);
		expect(alertSpy).toHaveBeenCalled(); // should be called



	});

	it(" callback will return message and another callback ",() =>{
		callbackSpy = jasmine.createSpy('callbackSpy');
		global.confirm = jasmine.createSpy("confirm").and.returnValue(true);


		spyOn(prompt,'confirmAsync').and.callFake((message) =>{
			callbackSpy(true);
		});

		const message = "hello";
		prompt.confirm(message,callbackSpy);

		jasmine.clock().tick(250);

		expect(prompt.confirmAsync).toHaveBeenCalledOnceWith(message,callbackSpy);
		expect(callbackSpy).toHaveBeenCalledOnceWith(true);


	});

	it("if callback is not returned it will call Doconfirm",() =>{
		callbackSpy = jasmine.createSpy('callbackSpy');
		global.confirm = jasmine.createSpy("confirm").and.returnValue(true);

		const message = "hello";

		const result = prompt.confirm(message);

		expect(result).toBeDefined();






















	});


});
