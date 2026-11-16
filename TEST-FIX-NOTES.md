# SecurityConfigWebTest fix

## What failed
`customerCanAccessCustomerApiWhenCsrfTokenIsPresent` expected HTTP 200 but received HTTP 404. The request passed through the security chain and reached Spring MVC's static-resource handler, which means the test-only `ProbeController` mapping was not registered in the MVC test slice.

## Change
Moved the test probe endpoints from a package-private nested controller to a public top-level `ProbeController` under `src/test/java/com/homeservices/config/`. `SecurityConfigWebTest` now explicitly selects `ProbeController.class` with `@WebMvcTest`.

This controller is test source only; it is not packaged in the production application JAR.

## Run in STS
Right-click the project > Run As > Maven build... > Goals:

```text
clean test
```

Expected: all 26 tests pass. If this test still returns 404, share the updated Surefire report and confirm the test class and `ProbeController.java` both appear under `src/test/java`.
