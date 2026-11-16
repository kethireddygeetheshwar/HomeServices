# HomeServices — service experience enhancement

## What changed

- The service-detail hero now changes its headline and explanation according to the service category: Electrician, Plumber, Carpenter, Painter, Mechanic, or Cleaning.
- Added a `Before the visit` guide with three practical preparation tips for each of those six categories and a generic fallback for any future category.
- Kept the guide framed as preparation advice, not as a guarantee that every provider listing includes a particular task.
- Added a small pointer hint to tell desktop users that the 3D showcase responds to pointer movement. The scene remains decorative to assistive technology.
- Updated the service directory grid to center compact result sets (one or two providers) rather than leaving them aligned to the left in an otherwise empty three-column layout.
- Added responsive styles for the preparation guide and reduced-motion handling.

## Preserved

- Spring Boot, Thymeleaf, MariaDB and the existing security configuration.
- Provider listing data, pricing, durations and review records.
- Availability filtering by estimated service duration and backend duration validation.
- Existing category-specific Three.js scenes and SVG fallbacks.

## Verification

Run `python tools/verify_source.py` from the project root. It checks project structure, role security markers, HTML template safety markers, JavaScript syntax, Three.js template slots, the six category-specific service-detail branches, the 21 preparation cards (18 category cards + 3 fallback cards), and the responsive grid rule.

This offline check does not replace a fresh Maven build, a MariaDB integration test, or live browser testing. Those must be run locally in STS/Chrome.
