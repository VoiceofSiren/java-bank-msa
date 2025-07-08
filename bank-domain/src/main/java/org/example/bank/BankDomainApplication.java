package org.example.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.example.bank")
public class BankDomainApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankDomainApplication.class, args);
	}

}
