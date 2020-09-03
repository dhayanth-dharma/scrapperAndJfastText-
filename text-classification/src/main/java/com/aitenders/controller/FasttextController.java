package com.aitenders.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitenders.enums.AitendersLanguage;
import com.aitenders.service.FasttextService;
import com.aitenders.service.StopWordComponent;

@RestController
@RequestMapping(path = "/api/")
public class FasttextController {

	@Autowired
	private FasttextService service;
	@Autowired
	private StopWordComponent stopwordComponent;
	
	@GetMapping(path = "setup", produces = MediaType.APPLICATION_JSON_VALUE)
	public String setup(){
		
		try {
			service.setup();
			return "ok";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
	}

	@GetMapping(path = "load" ,produces=MediaType.APPLICATION_JSON_VALUE)
	public String load(){
		try {
			service.load();
			return "ok";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
	}
	@GetMapping(path = "read/{fileName}" ,produces=MediaType.APPLICATION_JSON_VALUE)
	public String read(@PathVariable("fileName") String text){
	
		try {
			service.read(text);
			return "ok";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
		
	}
	
	@GetMapping(path = "learn" ,produces=MediaType.APPLICATION_JSON_VALUE)
	public String learn(){
	
		try {
			return "ok";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
		
	}
	@GetMapping(path = "classify/{text}" ,produces=MediaType.APPLICATION_JSON_VALUE )
	public String classify(@PathVariable String text){
		try {
		service.classify(text);
			return "ok";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
	}
	
	@GetMapping(path = "file/{filename}" ,produces=MediaType.APPLICATION_JSON_VALUE )
	public String textFileReading(@PathVariable String filename){
		try {
		service.readAnClassify(filename);
			return "ok";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
	}
	@GetMapping(path = "testsum/{filename}" ,produces=MediaType.APPLICATION_JSON_VALUE )
	public String prepareXmlTestResult(@PathVariable String filename){
		try {
		service.prepareXmlTestResult(filename);
			return "ok";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
	}
	
}
