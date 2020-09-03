package com.aitenders.fasttex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.aitenders.service","com.aitenders.*"})
@SpringBootApplication

public class FasttextApplication {

	public static void main(String[] args) {
		SpringApplication.run(FasttextApplication.class, args);
	}

}
