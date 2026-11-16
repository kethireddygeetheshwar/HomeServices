# HomeServices — 3D and animated UI update

This update adds progressive, procedural Three.js scenes to the existing Spring Boot + Thymeleaf application. It preserves the Java backend, MVC routes, authentication, booking operations and MariaDB data model.

## What changed

- **Homepage hero:** keeps the existing local home SVG as a fallback and upgrades the main hero showcase to a procedural Three.js house scene with a small orbit, lighting and restrained movement.
- **Service directory:** adds a category-specific Three.js showcase for the six categories already supported by the application (Electrician, Carpenter, Painter, Mechanic, Plumber and Cleaning).
- **Service details:** adds a larger category-specific Three.js showcase above the existing provider and booking information.
- **Category cards and service cards:** retain the lighter local SVG illustrations and CSS depth effects. These are intentionally not separate WebGL scenes, avoiding a renderer per card and reducing GPU use on long result lists.
- **Booking journey:** adds a homepage section explaining the real three-step booking flow: find a listing, choose a published slot, then send a request for provider confirmation.
- **Trust language:** changes wording from “verified” / “trusted” provider claims to “approved” listings. The current backend records admin approval; it does not implement formal identity/document verification.
- **Database configuration:** `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` environment variables are now wired into `application.properties`, matching the README guidance. SQL logging can be controlled with `JPA_SHOW_SQL` and `HIBERNATE_SQL_LOG_LEVEL`.
- **Repository hygiene:** adds `.gitignore` entries for Maven output, IDE folders, local environment files and logs.
- **Documentation:** clarifies implemented functionality and current product limitations.

## How the 3D scenes work

- Scene geometry is generated in JavaScript from Three.js primitives; there are no external 3D model files to download.
- The module imports the pinned Three.js `0.187.0` module from jsDelivr.
- The module is only applied to the three large showcase slots: home hero, service directory, and service detail.
- Existing SVG illustrations remain beneath the canvas and are shown if the module cannot load or WebGL cannot be created.
- The module also leaves the SVG experience in place on data-saving and very low-resource devices.
- The rendering loop pauses when a scene leaves the viewport and becomes static when `prefers-reduced-motion` is enabled.
- Small service/category cards continue using SVG + CSS perspective rather than creating multiple WebGL renderers.

A live browser render still needs to be checked on the developer's Windows machine and target devices. Static checks cannot establish actual GPU compatibility or visual quality on every browser.

## Files changed

- `src/main/resources/templates/home.html`
- `src/main/resources/templates/services.html`
- `src/main/resources/templates/service-detail.html`
- `src/main/resources/templates/fragments/layout.html`
- `src/main/resources/static/css/style.css`
- `src/main/resources/static/js/scene3d.js` (new)
- `src/main/resources/application.properties`
- `tools/verify_source.py`
- `README.md`, `UI-3D-README.md`, `UI-3D-QA.md`, `QA-REPORT.md`
- `.gitignore` (new)

No Java controller, service, entity, repository, security configuration, Maven dependency, database schema, or booking business-rule source was changed in this visual update.

## Run in STS 5.4.0

1. Extract the ZIP to a new folder. Keep your original project as a backup.
2. In STS, choose **File → Import → Maven → Existing Maven Projects**.
3. Select the extracted project folder containing `pom.xml`, then click **Finish**.
4. Right-click the project → **Maven → Update Project**.
5. Start XAMPP MariaDB and keep your existing `homeservices` database. Do not delete it.
6. Run `HomeServicesApplication.java` using **Run As → Spring Boot App**.
7. Open `http://localhost:8080` and hard refresh with `Ctrl + F5` if old CSS/JS is cached.

The visual showcase uses a CDN for Three.js, so an internet connection is needed for actual 3D rendering. Without it, or without WebGL support, the local SVG artwork remains as the fallback.

## Fixes based on first local browser screenshots

The first screenshot review revealed that the shared footer fragment did not include the JavaScript tags because they were siblings after `</footer>`. This prevented both the local SVG scenes and the Three.js module from running. The script tags have been moved inside the footer fragment. Bootstrap now loads before the custom stylesheet so the HomeServices link styling is not overridden, and the empty alert wrapper no longer leaves a blank strip below the navigation. App asset URLs use `v=2` to avoid stale browser cache entries.

## Verify the main interactions

1. Open the homepage and check the floating house scene.
2. Click each supported service category.
3. Confirm the directory showcase changes to the selected service category.
4. Open a service listing and confirm its detail showcase uses the matching category.
5. Check that the booking date, availability selection and request form still behave as before.
6. Repeat on a narrow mobile viewport and with OS reduced-motion enabled.

## Validation performed in the build environment

- `node --check` passed for `app.js` and `scene3d.js`.
- `python tools/verify_source.py` passed all bundled static source checks, including the new Three.js module and its template slots.
- CSS parsing and Thymeleaf template tokenization were checked after the updates.
- The bundled Surefire XML reports record 26 tests with 0 failures, 0 errors and 0 skipped. These are existing report artifacts, not a new Maven test run during this update.
- Maven was not available in this build environment, so a fresh Java compile and JUnit run were not performed here.
- A headless Chromium visual check could not initialize EGL/WebGL in this build environment. Live browser verification remains a required local check.

## Not implemented / not claimed

- No live payment integration, GPS/maps/distance search, favorites/saved addresses, email/SMS notifications or provider earnings ledger.
- No provider identity-document verification; providers are approved/rejected by the admin flow.
- No rigged human characters or realistic animated trade workers. The Three.js scenes are stylized procedural object compositions.
- No separate WebGL scene on every card. The large hero/showcase regions use WebGL; smaller repeated illustrations use SVG for performance.
- No production-readiness or deployment certification is claimed.


## Service-detail experience enhancement

- Each supported category now has its own hero headline and explanatory copy on the service-detail page.
- Added category-specific pre-visit preparation guidance for Electrician, Plumber, Carpenter, Painter, Mechanic and Cleaning. This is general preparation guidance, not a promise that every listing includes those tasks.
- Added a compact-results grid layout so small service listings are centered instead of leaving an awkward empty third column.
- Preserved the existing service model, booking flow, availability-duration filtering and provider approval behavior.
