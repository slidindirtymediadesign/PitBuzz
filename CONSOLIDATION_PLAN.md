# Codebase consolidation pass

Current working-tree audit:
- Files: 57
- PHP endpoints/helpers: 16
- SQL fragments: 14
- Duplicate PHP filenames found: 0
- SQL fragments containing ALTER TABLE: 3

## Required consolidation order
1. Preserve one canonical `/api/v1` endpoint per operation.
2. Move common JSON/body/session/authorization/error logic into shared bootstrap helpers.
3. Replace fragmented production SQL with one clean baseline schema plus numbered migrations.
4. Make race-program ordering authoritative for ADVANCE.
5. Make event ID part of race-program and announcement ownership.
6. Add explicit foreign keys after existing data compatibility is checked.
7. Add automated authorization/routing regression tests before mobile packaging.

Fragmented SQL remains reference material only after the canonical schema is generated; it is not the production install path.
