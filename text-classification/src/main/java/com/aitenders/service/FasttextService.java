package com.aitenders.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service

public class FasttextService {
	@Value("${aconym.stopwords.directory}")
	private String stopWordDirectory;
	FasttextSingletonService instance;
	private FasttextService()
	{
		instance=FasttextSingletonService.getInstance(stopWordDirectory);
	}
	
	public void setup(){
		instance.setup();
	}
	public void load(){
		System.out.println("From Service ::"+ stopWordDirectory);
		instance.load();
	}

	public void classify(String text){
		instance.classify2(text);
	}
	public void read(String name) throws IOException{
		instance.readArrf(name);	
	}
	public void readAnClassify(String fileName) throws Exception{
		instance.read_and_classify(fileName);
	}
	public void prepareXmlTestResult(String fileName) throws Exception{
		instance.prepare_xml_test_summary(fileName);
	}
	
}

