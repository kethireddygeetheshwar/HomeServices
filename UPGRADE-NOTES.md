# HomeServices upgrade notes — 16 November 2026

This update is based on the supplied `HomeServices-Service-Experience-Upgrade(2).zip` project. The original archive was not modified.

## Changes in this pass

- Refined the shared visual system with clearer focus states, touch-friendly button sizing, restrained hover feedback, improved card surfaces and additional small-screen spacing rules.
- Added explicit reduced-motion CSS handling so the interface remains comfortable for users who request less motion.
- Added client-side filtering to the admin provider-application queue. It filters only the pending applications already rendered by the server; it does not change approval or rejection permissions or server-side workflows.
- Added a production Spring profile override that requires `ADMIN_EMAIL` and `ADMIN_PASSWORD` to be supplied explicitly.
- Added a startup guard requiring a 16-character minimum bootstrap admin password when the `prod` profile is active.
- Updated setup documentation to distinguish the development bootstrap account from production credentials.
- Kept existing routes, entities, booking state transitions, locking, and database mappings unchanged in this pass.

## Verification performed

- `python tools/verify_source.py` — passed all bundled offline structural checks.
- `node --check src/main/resources/static/js/app.js` — passed.
- `node --check src/main/resources/static/js/scene3d.js` — passed.
- ZIP archive integrity check — performed after packaging.

## Verification not performed

- Maven is not installed in the editing environment, so `mvn test` and a Java dependency-aware compile could not be run here.
- No live MariaDB instance or browser session was available, so database-backed workflows and visual QA at desktop/mobile breakpoints still need to be run locally.
- The admin filter is a convenience filter over the current pending list, not server-side pagination or a full admin search API.

## Production start reminder

Set `ADMIN_EMAIL`, `ADMIN_PASSWORD` (16+ characters), `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`, then activate `prod` using `--spring.profiles.active=prod`. The local default administrator credentials are for development only.
