# Race Control ADVANCE algorithm

`race_program.sort_order` is authoritative. Never calculate the next race as `race_number + 1`.

For the active event:
1. Load non-skipped program rows ordered by `sort_order`.
2. Current RACE becomes the previous STAGING row.
3. STAGING becomes the previous GET READY row.
4. GET READY becomes the next eligible program row after the promoted staging/get-ready sequence.
5. Mark prior current race complete when appropriate.
6. Persist the three slots and program statuses in one database transaction.
7. Write before/after state to `race_control_history`.
8. Queue rider-aware alerts only after the transaction commits.
9. Dedupe keys prevent repeated alerts if dispatch retries.

Manual SET/CORRECT:
- accepts explicit program race IDs/numbers for the active event;
- validates each exists and is not skipped;
- commits all slot/status changes atomically;
- records history;
- never silently renumbers the race program.

This supports skipped races, inserted races and non-sequential race numbers without breaking staging.
