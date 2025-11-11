package com.darshan.bfh.service;

import com.darshan.bfh.model.FinalQueryRequest;
import com.darshan.bfh.model.GenerateWebhookRequest;
import com.darshan.bfh.model.GenerateWebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QualifierService {

	private static final Logger log = LoggerFactory.getLogger(QualifierService.class);

	private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
	private static final String TEST_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

	// Candidate details
	private static final String CANDIDATE_NAME = "Darshan Gowda GL";
	private static final String CANDIDATE_REG_NO = "PES2UG22CS161";
	private static final String CANDIDATE_EMAIL = "darshan@example.com";

	private final RestTemplate restTemplate;

	public QualifierService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void executeWorkflow() {
		log.info("Step 1: Sending POST to generate webhook...");
		GenerateWebhookRequest request = new GenerateWebhookRequest(CANDIDATE_NAME, CANDIDATE_REG_NO, CANDIDATE_EMAIL);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<GenerateWebhookRequest> httpEntity = new HttpEntity<>(request, headers);
		GenerateWebhookResponse response = restTemplate.postForObject(GENERATE_WEBHOOK_URL, httpEntity, GenerateWebhookResponse.class);

		if (response == null) {
			throw new IllegalStateException("Generate webhook response was null.");
		}

		String webhook = response.getWebhook();
		String accessToken = response.getAccessToken();
		log.info("Received webhook: {}", webhook);
		log.info("Received accessToken (JWT): {}", accessToken != null ? "<REDACTED>" : null);

		boolean isOddQuestion = isOddBasedOnRegNo(CANDIDATE_REG_NO);
		log.info("Step 2: Determining assigned question based on regNo ({}): {}", CANDIDATE_REG_NO, isOddQuestion ? "Question 1 (Odd)" : "Question 2 (Even)");

		String finalSqlQuery = """
			SELECT 
				p.AMOUNT AS SALARY,
				CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME,
				TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE,
				d.DEPARTMENT_NAME
			FROM PAYMENTS p
			JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
			JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
			WHERE DAY(p.PAYMENT_TIME) <> 1
			ORDER BY p.AMOUNT DESC
			LIMIT 1;
			""";


		log.info("Step 4: Submitting final SQL query to test webhook...");
		HttpHeaders submitHeaders = new HttpHeaders();
		submitHeaders.setContentType(MediaType.APPLICATION_JSON);
		submitHeaders.set("Authorization", accessToken);

		FinalQueryRequest finalQueryRequest = new FinalQueryRequest(finalSqlQuery);
		HttpEntity<FinalQueryRequest> submitEntity = new HttpEntity<>(finalQueryRequest, submitHeaders);

		String submitResponse = restTemplate.postForObject(TEST_WEBHOOK_URL, submitEntity, String.class);
		log.info("Submission API response: {}", submitResponse);
	}

	private boolean isOddBasedOnRegNo(String regNo) {
		if (regNo == null || regNo.isEmpty()) {
			return false;
		}
		int len = regNo.length();
		// Extract last two digits; if not available, fallback to last digit
		int start = Math.max(0, len - 2);
		String lastTwo = regNo.substring(start).replaceAll("\\D", "");
		if (lastTwo.isEmpty()) {
			// fallback: extract last numeric digit anywhere
			String digits = regNo.replaceAll("\\D", "");
			if (digits.isEmpty()) return false;
			char c = digits.charAt(digits.length() - 1);
			int d = c - '0';
			return (d % 2) == 1;
		}
		try {
			int num = Integer.parseInt(lastTwo);
			return (num % 2) == 1;
		} catch (NumberFormatException e) {
			log.warn("Failed to parse last two digits from regNo={}, defaulting to even.", regNo);
			return false;
		}
	}
}


