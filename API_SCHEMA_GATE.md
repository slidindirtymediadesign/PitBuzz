# API/schema compatibility gate

Current SQL-aware validation:
- Active API tables referenced: 19
- Canonical/recovered tables defined: 24
- Missing active API table definitions: **0**

`announcement_playback` has been folded into the canonical delivery schema with foreign keys to announcements and users.

Next gate: column-level API/schema compatibility and final clean-install schema generation.
