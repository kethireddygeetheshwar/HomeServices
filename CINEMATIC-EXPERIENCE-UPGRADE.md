# HomeServices — Cinematic Experience Upgrade

## What changed

- Rebuilt the homepage hero as a full-height cinematic introduction with layered aurora lighting, subtle grid/grain atmosphere, animated title reveal and a more prominent procedural 3D home showcase.
- Preserved the actual Thymeleaf service category, city search, dynamic categories, service listings, provider information and native navigation/form submission.
- Added a contextual explore action and accessible scroll cue linking to the service categories.
- Added a pointer-following light field, spring-follow cursor aura (native cursor remains visible), contextual cursor labels, and magnetic motion on the primary hero action for fine-pointer devices.
- Added more dimensional orbit rings, floating trust/availability cards, glass treatment and responsive composition around the existing Three.js/SVG scene.
- Improved header depth feedback while scrolling.
- Kept reduced-motion behavior, touch-friendly layouts, Three.js low-resource fallback and the existing service illustrations.

## Files changed

- `src/main/resources/templates/home.html`
- `src/main/resources/static/css/style.css`
- `src/main/resources/static/js/app.js`
- `src/main/resources/templates/fragments/layout.html` (static asset version updated)

## Functional safeguards

The upgrade does not replace backend functionality, fabricate statistics/reviews/provider availability, or intercept the native service search form. Existing Thymeleaf model bindings and route targets are retained.

## Verification

Source-level verification and JavaScript syntax validation should be run after extraction. Browser-based visual testing, Java/Maven compilation, JUnit execution and live MariaDB workflow tests must be performed in the target development environment; do not treat static verification as substitutes for those tests.
