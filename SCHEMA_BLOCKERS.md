# Schema consolidation blockers

The current working tree creates 15 tables.

Static audit found:
- Foreign-key referenced tables missing from the current consolidated material: none
- ALTER targets whose base CREATE TABLE is absent from the current consolidated material: announcements, users

## Release rule
`schema.mysql.review.sql` is NOT promoted to `schema.mysql.sql` until every missing base table is recovered/rebuilt and ALTER-based additions are folded into their canonical CREATE TABLE definitions.

This prevents shipping an install script that only works when an older database happens to exist.
