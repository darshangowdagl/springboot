package com.darshan.bfh;

import com.darshan.bfh.service.QualifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public CommandLineRunner runOnStartup(QualifierService qualifierService) {
		return args -> {
			log.info("Starting Bajaj Finserv Health Java Qualifier workflow...");
			try {
				qualifierService.executeWorkflow();
				log.info("Workflow completed.");
			} catch (Exception e) {
				log.error("Workflow failed: {}", e.getMessage(), e);
			}
		};
	}
}


