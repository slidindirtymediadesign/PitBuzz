# Core schema recovery

Recovered canonical clean-install definitions for `users` and `announcements`.

The recovered definitions already include the later system-role, event, priority and audio fields. Therefore the old ALTER statements for those fields must be omitted from the final clean-install schema rather than executed again.

Before final schema promotion, the remaining core team/message/class tables must also be present as CREATE TABLE definitions and all endpoint column names must be checked against the final schema.
