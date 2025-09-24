import "wc/ui/prompt.mjs";
import prompt from "wc/ui/prompt.mjs";


describe("wc/ui/prompt.mjs",()=> {
	let callbackSpy;
	let messageSpy;
	let objectSpy;
	let calledMessage;


	beforeEach(() => {
		callbackSpy = jasmine.createSpy('callbackSpy');
		messageSpy = jasmine.createSpy('messageSpy');
		objectSpy = jasmine.createSpy('objectSpy');
		jasmine.clock().install();

		spyOn(prompt,"confirm").and.returnValue(true);

		calledMessage = null;


	});

	afterEach(()=>{

		jasmine.clock().uninstall();

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
		prompt.confirm ("please choose option", );


		jasmine.clock().tick(250);

		expect(prompt.confirm).toHaveBeenCalledWith("please choose option");
		









	});

});
