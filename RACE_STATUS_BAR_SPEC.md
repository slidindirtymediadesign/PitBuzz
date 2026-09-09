# Persistent Race Status Bar

Visible at the top of the rider/team app at all times:

| Left | Center | Right |
|---|---|---|
| RACE # | STAGING | GET READY |
| current race number | staging race number | preparation race number |

Race Control:
- Everyone may view.
- Only system `race_admin` / `super_admin` may change.
- Team Owner/Admin permission alone never grants control.
- `ADVANCE` is one tap: STAGING becomes RACE #, GET READY becomes STAGING, and the following race becomes GET READY.
- Race Control may manually SET all three when the program changes, a race is skipped, rerun, delayed, or otherwise reordered.
- Optional labels support class names beneath each number.
- Future RaceLineHQ integration can write to the same API automatically.
