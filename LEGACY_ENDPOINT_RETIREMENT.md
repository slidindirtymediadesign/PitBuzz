# Endpoint retirement

Release API uses one implementation per operation.

Retired from active `/api`:
- old `race-control.php` -> archived as `legacy/race-control.pre-v1.php`
- canonical implementation is `api/race-control-v1.php`

Before release:
- refactor `active-event.php` to the common authenticated/system-role helper
- refactor `race-program.php` to the common authenticated/system-role helper
- after `/api/v1` routing is finalized, remove legacy filenames from the public web root entirely

Archived code is reference only and must not be routable in production.
