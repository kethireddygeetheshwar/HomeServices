# HomeServices — Immersive Experience Upgrade

This pass adds a richer interaction layer while keeping the existing Spring MVC, Thymeleaf, booking, authentication and database flows intact.

## Added experience

- Ambient animated particles in the homepage hero, created locally in the browser.
- Pointer-following light in the hero and interactive perspective movement on the hero showcase card.
- Floating trust/booking cards around the 3D showcase.
- A top-of-page reading progress indicator.
- Pointer-following light on service/category cards and dashboard panels.
- Button/link ripple feedback and stronger hover lift transitions.
- Gentle breathing motion on the category-specific SVG illustrations.
- Count-up animation for numeric dashboard metrics.
- Existing scroll reveals and card tilt are retained.
- Animation respects `prefers-reduced-motion`; pointer-only effects are disabled on touch devices.

## Files changed

- `src/main/resources/static/js/app.js`
- `src/main/resources/static/css/style.css`
- `src/main/resources/templates/fragments/layout.html` (asset cache version updated)

## Notes

The effects are progressive enhancement. They do not fabricate provider ratings, availability, booking confirmation, payment status, or other marketplace data. Native links and form submissions remain in control of navigation and booking workflows. The existing Three.js scene and its SVG fallback remain in place.

## Verification

- `node --check src/main/resources/static/js/app.js`
- `python tools/verify_source.py`

The verification script checks source structure and frontend syntax only; it does not compile Java, execute JUnit, or connect to MariaDB. Browser-based visual testing is still required on the target machine.
