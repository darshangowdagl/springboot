## Bajaj Finserv Health - Java Qualifier (Spring Boot)

### 👤 Candidate
**Name:** Darshan Gowda GL  
**Reg No:** PES2UG22CS161  
**Email:** darshandarshugl101218@example.com  

This Spring Boot application automatically:
- Sends a POST request on startup to generate a webhook and access token
- Determines the assigned question based on the last two digits of the regNo
- Provides a placeholder to add the final SQL query
- Submits the SQL query to the test webhook using the JWT access token

### Tech Stack
- Java 17
- Spring Boot 3 (Spring Web, JSON via Jackson)
- RestTemplate (HTTP client)

### How it works
1. On application startup (`CommandLineRunner`), the app sends:
   - POST `https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA`
   - Body contains candidate details.
2. The app parses response JSON to extract:
   - `webhook` (String)
   - `accessToken` (String, JWT)
3. Based on `regNo` last two digits:
   - Odd → Question 1 (this project is configured for odd since regNo ends with 61)
4. Add your final SQL to the placeholder inside `QualifierService`.
5. The app then sends:
   - POST `https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA`
   - Headers:
     - `Authorization: <accessToken>`
     - `Content-Type: application/json`
   - Body:
     ```json
     { "finalQuery": "YOUR_SQL_QUERY_HERE" }
     ```

### Code locations
- Main class: `src/main/java/com/darshan/bfh/Application.java`
- Service (workflow): `src/main/java/com/darshan/bfh/service/QualifierService.java`
- Models:
  - `GenerateWebhookRequest`
  - `GenerateWebhookResponse`
  - `FinalQueryRequest`

### Insert your SQL
Open `QualifierService.java` and replace the placeholder:
```java
String finalSqlQuery = "-- TODO: Replace this with the final SQL query for Question 1";
```

### Build
Requires Maven and JDK 17+.
```bash
mvn clean package
```

### Run
```bash
java -jar target/bfh-qualifier-1.0.0.jar
```

You should see logs for each step in the console.

### Notes
- No REST controllers are exposed; the flow is triggered automatically on startup.
- Uses `RestTemplate` for both POST requests.
- The JWT is included as-is in the `Authorization` header for the second request.


