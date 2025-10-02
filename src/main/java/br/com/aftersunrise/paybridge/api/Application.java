package br.com.aftersunrise.paybridge.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "br.com.aftersunrise.paybridge")
@EnableJpaRepositories(basePackages = "br.com.aftersunrise.paybridge.infrastructure.repositories")
@EntityScan(basePackages = "br.com.aftersunrise.paybridge.domain.model")
public class Application {
	static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}