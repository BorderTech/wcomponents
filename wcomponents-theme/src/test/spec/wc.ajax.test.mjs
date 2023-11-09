import ajax from "wc/ajax/ajax.mjs";

describe("wc/ajax", () => {

	it("makes a synchronous request for XML", function(done) {
		const callback = jasmine.createSpy().and.callFake(function (response) {
			expect(response.documentElement.nodeName).toBe("note");
			expect(callback).toHaveBeenCalledTimes(1);
			done();
		});

		const url = "test/resource/note.xml",
			request = {
				url,
				callback,
				cache: false,
				async: false,
				responseType: "responseXML"
			};

		ajax.simpleRequest(request);

	});

	it("makes a synchronous request for text", function(done) {
		const callback = jasmine.createSpy().and.callFake(function (response) {
			expect(response).toBe("pass the dutchie");
			expect(callback).toHaveBeenCalledTimes(1);
			done();
		});

		const url = "test/resource/note.txt",
			request = {
				url,
				callback,
				cache: false,
				async: false,
				responseType: "responseText"
			};

		ajax.simpleRequest(request);
	});

	it("makes an asynchronous request for XML", function(done) {
		const callback = jasmine.createSpy().and.callFake(function (response) {
			expect(response.documentElement.nodeName).toBe("note");
			expect(callback).toHaveBeenCalledTimes(1);
			done();
		});

		const url = "test/resource/note.xml",
			request = {
				url,
				callback,
				cache: false,
				async: true,
				responseType: "responseXML"
			};

		ajax.simpleRequest(request);
	});

	it("makes an asynchronous request for text", function(done) {
		const callback = jasmine.createSpy().and.callFake(function (response) {
			expect(response).toBe("pass the dutchie");
			expect(callback).toHaveBeenCalledTimes(1);
			done();
		});

		const url = "test/resource/note.txt",
			request = {
				url,
				callback,
				cache: false,
				async: true,
				responseType: "responseText"
			};

		ajax.simpleRequest(request);
	});

});
