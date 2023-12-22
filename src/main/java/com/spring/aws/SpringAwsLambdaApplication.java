package com.spring.aws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

import java.util.function.Function;

@SpringBootApplication
public class SpringAwsLambdaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAwsLambdaApplication.class, args);
	}

	/*
	@Bean
	public Function<Message<String>, Message<String>> routingFunction() {
		return message -> {
			String routingExpression = message.getHeaders().get("routingExpression", String.class);
			if (routingExpression != null) {
				return message;
			} else {
				throw new IllegalArgumentException("Missing routingExpression header");
			}
		};
	}
	*/
}
