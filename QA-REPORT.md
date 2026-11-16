# HomeServices QA report

## Existing bundled Maven test report artifacts

The project ZIP contains Surefire XML reports dated 2026-11-09. Those reports record:

| Test suite | Tests | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| `SecurityConfigWebTest` | 4 | 0 | 0 | 0 |
| `LockingRepositoryMetadataTest` | 2 | 0 | 0 | 0 |
| `BookingServiceTest` | 10 | 0 | 0 | 0 |
| `MarketplaceServiceTest` | 1 | 0 | 0 | 0 |
| `ProviderServiceTest` | 5 | 0 | 0 | 0 |
| `RegistrationServiceTest` | 4 | 0 | 0 | 0 |
| **Total** | **26** | **0** | **0** | **0** |

These are report artifacts already present in the supplied archive. They are evidence of a previous test run, not a fresh Maven run performed as part of the current UI update.

## Historical test-fix note

The supplied `TEST-FIX-NOTES.md` describes an earlier failure in `SecurityConfigWebTest.customerCanAccessCustomerApiWhenCsrfTokenIsPresent`, where a test-only probe endpoint returned 404. The later bundled Surefire XML reports show all four tests in that class passing. The current UI update did not modify Java test fixtures or backend code.

## Static checks performed during this update

- `node --check src/main/resources/static/js/app.js`: passed.
- `node --check src/main/resources/static/js/scene3d.js`: passed.
- `python tools/verify_source.py`: passed all configured static source checks.
- `pom.xml` parsed as XML.
- Thymeleaf templates were tokenized with an HTML parser.
- CSS was parsed with zero parse errors.
- Environment-variable placeholders for DB connection settings are present in `application.properties`.
- Three.js scene slots are present on the homepage, services directory and service-detail templates.

## Not run / not claimed

- A fresh `mvn clean test` or `mvn clean package` was not run because Maven is unavailable in this environment.
- No live MariaDB database connection or browser-based authenticated workflow was tested.
- A headless Chromium visual check could not initialize EGL/WebGL in this environment; actual 3D rendering needs verification in a normal desktop browser.
- No deployment, security audit, load test or production-readiness certification is claimed.

## Required local verification

From the project root in STS terminal:

```bash
mvn clean test
```

Then run the application with MariaDB started and verify home, directory, service details, registration/login, customer booking, provider booking actions, admin provider approval, and mobile layout. For a deployable package, run:

```bash
mvn clean package
```
