package com.springboot.prj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.springboot")
@SpringBootApplication
public class LibApplication {
	public static void main(String[] args) {
		SpringApplication.run(LibApplication.class, args);
	}

}
