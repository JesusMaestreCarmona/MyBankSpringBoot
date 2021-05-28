package com.myBank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class MyBankSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyBankSpringBootApplication.class, args);
	}

}
