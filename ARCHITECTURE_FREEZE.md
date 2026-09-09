# Team PitBuzz beta architecture freeze

## One source of truth
- MySQL/MariaDB backend owns users, teams, riders, classes, race state, announcements, messages, preferences and delivery state.
- Android and iPhone consume the same versioned HTTP API.
- UI never grants permissions; every privileged mutation is authorized server-side.
- Private-message audio defaults OFF and cannot be overridden by sender.
- Race Control is a system permission, never inherited from Team Owner/Admin.

## API conventions
- JSON requests/responses.
- Successful writes return `{ "ok": true }` plus changed resource when useful.
- Errors use `{ "error": "...", "code": "STABLE_CODE" }`.
- IDs are integers; timestamps are ISO-8601 at the client boundary.
- POST endpoints are idempotent where retries are expected.
- 401 authentication, 403 authorization, 409 conflict, 422 validation, 429 throttling.

## Release discipline
1. Consolidate SQL into one clean install/migration path.
2. Consolidate shared PHP bootstrap/auth/authorization helpers.
3. Remove prototype/hard-coded data.
4. Validate every mutation for ownership/scope.
5. Run syntax/schema/static checks.
6. Run workflow/API integration tests.
7. Test Android and iPhone against the same backend.
8. Freeze beta schema/API before packaging.
