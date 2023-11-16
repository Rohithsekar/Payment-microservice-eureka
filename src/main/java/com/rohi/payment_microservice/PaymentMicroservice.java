package com.rohi.payment_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

/*

 with version 2022.0.1, @EnableEurekaClient is deprecated. Instead, use @EnableDiscoveryClient or use the version
 2020.0.3 for @EnableEurekaClient

 */

@SpringBootApplication
@EnableDiscoveryClient
public class PaymentMicroservice {

	public static void main(String[] args) {

		ApplicationContext context =SpringApplication.run(PaymentMicroservice.class, args);

	}

}
/*change*/