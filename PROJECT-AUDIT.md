# HomeServices project audit and implementation notes

## Existing architecture

- Java 21 and Spring Boot 3.5.16.
- Spring MVC and Thymeleaf server-rendered pages.
- Plain JavaScript and CSS for interactions and styling.
- Spring Security with session login, BCrypt password hashing, role-based URL rules and CSRF protection.
- Spring Data JPA/Hibernate with MariaDB.
- Roles: customer, provider, admin.
- Core domain entities include users, provider profiles, service categories, service offerings, availability slots, bookings and reviews.

## Existing business workflows

- Customer and provider registration.
- Admin approval/rejection of provider profiles.
- Approved providers publish service offerings and availability slots.
- Customers browse services by city/category/text, inspect service details, check published slots and submit booking requests.
- Providers accept/reject/start/complete bookings.
- Customers cancel eligible bookings and submit one review for a completed booking.
- REST API endpoints expose category/service search, provider availability and booking creation/cancellation.

## Data consistency and access-control foundations

- Spring Security protects customer/provider/admin routes by role.
- Passwords use BCrypt.
- CSRF protection applies to state-changing form/API requests.
- Booking entities use optimistic versioning.
- Booking/availability workflow repositories include pessimistic locking for important concurrent state transitions and overlap checks.

## Limitations confirmed from the supplied source

- No integrated payment processor.
- No GPS/maps/distance-based search; city is text filtering.
- No saved addresses or customer favorites.
- No email/SMS notifications.
- No provider earnings ledger.
- No provider identity-document verification; admin approval is the current process.
- Admin dashboard covers provider approvals and platform counts, not a full analytics/back-office suite.
- Six seeded categories exist: Electrician, Carpenter, Painter, Mechanic, Plumber and Cleaning.

## Visual update in this archive

- Procedural Three.js hero scene on the home page.
- Category-bound Three.js showcase on the service directory and service detail pages.
- Existing SVG art retained as fallback and used for repeated cards.
- WebGL scene animation pauses offscreen; reduced-motion and low-resource/data-saving fallback paths are included.
- Homepage adds a section that accurately explains the existing booking journey.
- Trust labels use “approved” instead of claiming formal provider verification.
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JPA_SHOW_SQL` and `HIBERNATE_SQL_LOG_LEVEL` are wired through Spring Boot property placeholders.
- `.gitignore` added to keep build output, local IDE config, environment files and logs out of commits.

## Intentionally not changed

Java controllers/services/entities/repositories, Maven dependencies, database schema, authentication rules and booking business logic were preserved in this visual iteration.
