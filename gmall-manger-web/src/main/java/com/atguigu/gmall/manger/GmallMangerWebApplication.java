package com.atguigu.gmall.manger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.atguigu.gmall")
public class GmallMangerWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallMangerWebApplication.class, args);
	}
}
