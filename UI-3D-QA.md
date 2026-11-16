# HomeServices UI and 3D QA notes

## Scope

The update enhances the presentation layer and the documented database environment-variable configuration. It preserves the existing Spring MVC routes, Thymeleaf model attributes, authentication, booking operations, Java business rules and MariaDB data model.

## Scene coverage

| Location | Scene | Rendering approach |
|---|---|---|
| Homepage hero | Home / all services | Procedural Three.js geometry with local SVG fallback |
| Service directory showcase | Selected service category | Procedural Three.js geometry with category-bound SVG fallback |
| Service detail showcase | Selected service category | Procedural Three.js geometry with category-bound SVG fallback |
| Homepage category cards | Electrician, Carpenter, Painter, Mechanic, Plumber, Cleaning | Local SVG + CSS perspective/animation |
| Listing cards | Service category artwork | Local SVG + CSS perspective/animation |

The app's supported service categories are the six listed above. The examples of AC repair and additional services in the high-level design brief were not added as new database categories in this update.

## Progressive enhancement behavior

- The module is loaded from the shared Thymeleaf layout and only initializes nodes marked `data-three-scene`.
- If the CDN module fails, WebGL creation fails, or the device reports data-saving / very low-resource capabilities, the existing local SVG illustration remains visible.
- Rendering is paused while a scene is outside the viewport.
- `prefers-reduced-motion: reduce` results in a static rendered frame, while the CSS fallback also respects reduced motion.
- A lost WebGL context restores the local SVG fallback.

## Follow-up fixes from the user's browser screenshots

The screenshots exposed three concrete integration problems, now fixed in the source:

1. The shared `footer` fragment ended before the `app.js` and `scene3d.js` script tags. Pages using `th:replace="~{fragments/layout :: footer}"` therefore received the footer but not either script. The script tags are now inside the actual footer fragment, so the local SVG initializer and the progressive Three.js module are included in rendered pages.
2. Bootstrap was loaded after the custom stylesheet on the main pages, which overrode the global anchor reset and caused blue underlined navigation/category links. Bootstrap now loads before `style.css` wherever both are used.
3. The empty alerts container created a visible white strip between the navigation and hero. The alerts fragment is now omitted when there is no success or error message.

App and Three.js asset URLs also use a query version (`v=2`) to help browsers avoid stale cached scripts.

## Checks performed

- `node --check src/main/resources/static/js/app.js`: passed.
- `node --check src/main/resources/static/js/scene3d.js`: passed.
- `python tools/verify_source.py`: passed all static source checks, including Three.js template hooks and environment-variable configuration.
- Structural template check confirms both scripts are inside the footer fragment, the empty alerts fragment is conditional, and Bootstrap precedes the custom stylesheet on the affected pages.
- CSS parsed after changes with zero parse errors.
- All Thymeleaf template files tokenized with Python's HTML parser.
- `pom.xml` parsed successfully as XML.
- Bundled Surefire reports from the supplied project show 26 tests, 0 failures, 0 errors and 0 skipped. These reports predate this UI update; they are not a fresh test run.

## Not verified in this environment

- A fresh Maven compilation or JUnit run was not performed because Maven is not installed in this environment.
- A headless Chromium visual test did not initialize EGL/WebGL here, so no visual screenshot test is claimed.
- No live MariaDB, authenticated booking flow, provider lifecycle, or admin workflow was exercised in a running server.
- CDN availability and WebGL behavior still need verification in the user's local Chrome/Edge browser and on an actual mobile device.

## Local verification checklist

- [ ] Start XAMPP MariaDB and run the Spring Boot application in STS.
- [ ] Homepage loads and the large 3D home showcase appears when WebGL/CDN are available.
- [ ] If offline or WebGL is unavailable, the SVG home illustration remains visible.
- [ ] Each of the six supported category links opens the expected filtered service directory.
- [ ] The directory showcase matches the selected category.
- [ ] A service detail page displays the matching category scene.
- [ ] Booking date/slot selection and request submission still work.
- [ ] Customer, provider and admin login/dashboard flows still work.
- [ ] 320px, 375px, 430px, tablet and desktop layouts have no horizontal overflow.
- [ ] OS reduced-motion preference produces a static/non-animated experience.

## Product scope notes

The backend currently supports customer/provider registration, admin provider approval, provider service listings and availability, customer booking requests/cancellations, provider booking lifecycle actions, and reviews on completed bookings. It does not currently implement payment processing, GPS/maps/distance search, customer favorites/saved addresses, email/SMS notifications, provider earnings ledger, or identity-document verification.


## Service-detail enhancement (2026-11-12)

- Added Thymeleaf category-specific hero copy for all six seeded service categories.
- Added a category-specific preparation checklist with a generic fallback.
- Added responsive styles for the checklist and motion-reduction behavior.
- Centered compact service result sets in the listing grid.
- Static checks are required; live visual verification should be performed in STS/Chrome.
