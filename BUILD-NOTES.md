# Build and verification notes

Project configuration in the uploaded source:

- Java 21
- Spring Boot 3.5.16
- MariaDB Connector/J 3.5.7
- MariaDB JDBC URL: `jdbc:mariadb://localhost:3306/homeservices`
- MariaDB dialect is detected from JDBC metadata; no explicit dialect property is required.

## Checks completed while preparing this patch

- POM parsed as valid XML.
- Java source and package paths inspected.
- 80 offline structural/configuration checks pass via `python tools/verify_source.py`.
- All 15 Thymeleaf templates parse; CSS and JavaScript syntax checks pass.
- Java parser scan found no source-syntax errors; javac type-checking cannot complete because Spring/JUnit dependencies are unavailable without Maven.
- The project includes JUnit/Mockito unit tests for registration, booking, provider scheduling and locking metadata.
- Thymeleaf forms use escaped `th:text` rendering; no `th:utext` was found in the template set.
- JavaScript syntax can be checked with `node --check src/main/resources/static/js/app.js`.

## Checks not completed in this environment

The container does not have Maven installed and the archive does not include Maven Wrapper. Therefore `mvn test` and `mvn clean package` could not be run here. The added JUnit tests have not been executed in this environment. A live MariaDB 10.4.32 integration test, real HTTP authorization test and browser workflow test also require the user's local STS/XAMPP environment.

Run `mvn test` from STS or a terminal on the development machine, then run the application and follow the manual verification steps in `QA-REPORT.md`.
