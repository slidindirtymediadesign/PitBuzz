# Database migration policy

Production/beta deployments use only:
- `schema.mysql.sql` for a clean installation, once finalized.
- numbered migrations for an existing installation.

Files named `*.sql` that were created during design remain reference fragments and are never bulk-imported independently.

Rules:
1. A migration runs exactly once and is recorded in `schema_migrations`.
2. Never silently drop user/team/race/message data.
3. Destructive changes require an explicit backup and release note.
4. New columns get safe defaults or a backfill step before becoming required.
5. Foreign keys are added only after orphan checks pass.
6. App code and DB migration must remain backward-compatible during a rolling beta update.
