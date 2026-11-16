# Booking availability duration fix

## What was happening
The booking detail page used to show every available slot from the provider, even if a slot was shorter than the selected service duration. BookingService already rejected these slots during submission, which surfaced as an error after the customer selected one.

## What changed
- Service detail pages now request availability filtered by the service's estimated duration.
- Too-short slots are no longer offered in the customer booking dropdown.
- If no suitable slots exist, the page tells the customer the minimum duration required.
- The booking service retains its server-side duration validation and now reports the actual slot and service durations.
- The availability API accepts an optional `durationMinutes` query parameter; omitting it preserves the previous behavior.
- Added a unit test proving a 60-minute slot is excluded for a 62-minute service while a 75-minute slot remains selectable.

## Immediate workaround with an older build
For the Electrical Testing Service shown in the screenshots, the service duration is 62 minutes. The provider must publish a single available slot of at least 62 minutes for the same provider and date. For example, a 75-minute window such as 11:00 AM–12:15 PM, provided it does not overlap an existing slot. Adjacent short slots are not combined by the current booking model.
